package ro.casalazi.anaf;

import org.junit.jupiter.api.Test;
import ro.casalazi.errors.anaf.AnafIndisponibilException;
import ro.casalazi.errors.anaf.AnafLimitaException;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class RegistruAnafCuCacheTest {

    private static final String CUI = "1590082";
    private static final String ALT_CUI = "18452300";
    private static final AnafFirmaDto FIRMA =
            new AnafFirmaDto(CUI, "firma", "J1/1/2020", "adresa", true, false);

    private final AtomicLong ceas = new AtomicLong();
    private final AtomicInteger apeluri = new AtomicInteger();

    @Test
    void recitesteFirmaDoarDupaExpirare() {
        var cache = cache(cui -> Optional.of(FIRMA), 2);

        assertEquals(Optional.of(FIRMA), cache.cauta(CUI));
        avanseaza(899);
        assertEquals(Optional.of(FIRMA), cache.cauta(CUI));
        assertEquals(1, apeluri.get());

        avanseaza(1);
        assertEquals(Optional.of(FIRMA), cache.cauta(CUI));
        assertEquals(2, apeluri.get());
    }

    @Test
    void pastreazaTemporarAbsentaConfirmata() {
        var cache = cache(cui -> Optional.empty(), 2);

        assertTrue(cache.cauta(CUI).isEmpty());
        avanseaza(59);
        assertTrue(cache.cauta(CUI).isEmpty());
        assertEquals(1, apeluri.get());

        avanseaza(1);
        assertTrue(cache.cauta(CUI).isEmpty());
        assertEquals(2, apeluri.get());
    }

    @Test
    void eseculNuRamaneInCacheSiConsumaIntervalul() {
        var cache = cache(cui -> {
            if (apeluri.get() == 1) {
                throw new AnafIndisponibilException(null);
            }
            return Optional.of(FIRMA);
        }, 2);

        assertThrows(AnafIndisponibilException.class, () -> cache.cauta(CUI));
        assertThrows(AnafLimitaException.class, () -> cache.cauta(CUI));
        assertEquals(1, apeluri.get());

        avanseaza(1);
        assertEquals(Optional.of(FIRMA), cache.cauta(CUI));
        assertEquals(2, apeluri.get());
    }

    @Test
    void incepeIntervalulLaFinalulApelului() {
        var cache = cache(cui -> {
            avanseaza(3);
            return Optional.empty();
        }, 2);

        cache.cauta(CUI);
        assertThrows(AnafLimitaException.class, () -> cache.cauta(ALT_CUI));
        assertEquals(1, apeluri.get());

        avanseaza(1);
        assertTrue(cache.cauta(ALT_CUI).isEmpty());
        assertEquals(2, apeluri.get());
    }

    @Test
    void capacitateaEsteLimitataSiIntrarileExpirateElibereazaSpatiu() {
        var cache = cache(cui -> Optional.empty(), 1);
        cache.cauta(CUI);
        avanseaza(1);
        cache.cauta(ALT_CUI);
        assertThrows(AnafLimitaException.class, () -> cache.cauta(ALT_CUI));
        assertTrue(cache.cauta(CUI).isEmpty());

        avanseaza(59);
        cache.cauta(ALT_CUI);
        assertTrue(cache.cauta(ALT_CUI).isEmpty());
        assertEquals(3, apeluri.get());
    }

    @Test
    void cererileSimultanePentruAcelasiCuiImpartApelul() throws Exception {
        var inceput = new CountDownLatch(1);
        var elibereaza = new CountDownLatch(1);
        var cache = cache(cui -> {
            inceput.countDown();
            asteapta(elibereaza);
            return Optional.of(FIRMA);
        }, 2);

        try (var executare = Executors.newVirtualThreadPerTaskExecutor()) {
            var prima = executare.submit(() -> cache.cauta(CUI));
            try {
                assertTrue(inceput.await(5, TimeUnit.SECONDS));
                var aDoua = executare.submit(() -> cache.cauta(CUI));
                assertThrows(AnafLimitaException.class, () -> cache.cauta(ALT_CUI));

                elibereaza.countDown();
                assertEquals(Optional.of(FIRMA), prima.get(5, TimeUnit.SECONDS));
                assertEquals(Optional.of(FIRMA), aDoua.get(5, TimeUnit.SECONDS));
                assertEquals(1, apeluri.get());
            } finally {
                elibereaza.countDown();
            }
        }
    }

    private RegistruAnafCuCache cache(RegistruAnaf delegat, int capacitate) {
        return new RegistruAnafCuCache(cui -> {
            apeluri.incrementAndGet();
            return delegat.cauta(cui);
        }, ceas::get, Duration.ofMinutes(15), Duration.ofMinutes(1), capacitate);
    }

    private void avanseaza(long secunde) {
        ceas.addAndGet(TimeUnit.SECONDS.toNanos(secunde));
    }

    private static void asteapta(CountDownLatch latch) {
        try {
            assertTrue(latch.await(5, TimeUnit.SECONDS));
        } catch (InterruptedException exceptie) {
            Thread.currentThread().interrupt();
            throw new AssertionError(exceptie);
        }
    }
}
