package ro.casalazi.anaf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

/** pastreaza rezultatele anaf dupa cui-ul deja normalizat de service. */
@Primary
@Component
public class RegistruAnafCuCache implements RegistruAnaf {

    private record Intrare(Optional<AnafFirmaDto> rezultat, long expiraLa) {
        boolean expirata(long acum) {
            return acum - expiraLa >= 0;
        }
    }

    private final RegistruAnaf client;
    private final LongSupplier nanoTime;
    private final LimitatorAnaf limitator;
    private final long ttlGasit;
    private final long ttlNegasit;
    private final int capacitate;
    private final ConcurrentHashMap<String, Intrare> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CompletableFuture<Optional<AnafFirmaDto>>> inCurs =
            new ConcurrentHashMap<>();

    @Autowired
    public RegistruAnafCuCache(
            AnafClient client,
            @Value("${casalazi.anaf.cache.ttl-gasit-secunde:900}") long ttlGasit,
            @Value("${casalazi.anaf.cache.ttl-negasit-secunde:60}") long ttlNegasit,
            @Value("${casalazi.anaf.cache.capacitate:1000}") int capacitate) {
        this(client, System::nanoTime, Duration.ofSeconds(ttlGasit),
                Duration.ofSeconds(ttlNegasit), capacitate);
    }

    RegistruAnafCuCache(RegistruAnaf client, LongSupplier nanoTime,
                        Duration ttlGasit, Duration ttlNegasit, int capacitate) {
        if (ttlGasit.isNegative() || ttlGasit.isZero()
                || ttlNegasit.isNegative() || ttlNegasit.isZero() || capacitate < 1) {
            throw new IllegalArgumentException("Duratele și capacitatea cache-ului ANAF trebuie să fie pozitive.");
        }
        this.client = client;
        this.nanoTime = nanoTime;
        this.limitator = new LimitatorAnaf(nanoTime);
        this.ttlGasit = ttlGasit.toNanos();
        this.ttlNegasit = ttlNegasit.toNanos();
        this.capacitate = capacitate;
    }

    @Override
    public Optional<AnafFirmaDto> cauta(String cui) {
        Intrare intrare = intrareValida(cui);
        if (intrare != null) {
            return intrare.rezultat();
        }

        // cererile pentru acelasi cui impart rezultatul fara sa tina un lock in timpul cererii http.
        var rezultatInCurs = new CompletableFuture<Optional<AnafFirmaDto>>();
        var existent = inCurs.putIfAbsent(cui, rezultatInCurs);
        if (existent != null) {
            return asteapta(existent);
        }

        try {
            // o cerere anterioara putea completa cache-ul intre prima citire si putifabsent.
            intrare = intrareValida(cui);
            Optional<AnafFirmaDto> rezultat;
            if (intrare != null) {
                rezultat = intrare.rezultat();
            } else {
                rezultat = limitator.executa(() -> Objects.requireNonNull(client.cauta(cui)));
                salveaza(cui, rezultat);
            }
            rezultatInCurs.complete(rezultat);
            return rezultat;
        } catch (RuntimeException | Error eroare) {
            rezultatInCurs.completeExceptionally(eroare);
            throw eroare;
        } finally {
            inCurs.remove(cui, rezultatInCurs);
        }
    }

    private Intrare intrareValida(String cui) {
        Intrare intrare = cache.get(cui);
        if (intrare == null || !intrare.expirata(nanoTime.getAsLong())) {
            return intrare;
        }
        cache.remove(cui, intrare);
        return null;
    }

    // sincronizam doar scrierile, astfel incat limita de dimensiune sa fie respectata.
    private synchronized void salveaza(String cui, Optional<AnafFirmaDto> rezultat) {
        long acum = nanoTime.getAsLong();
        if (cache.size() >= capacitate) {
            cache.values().removeIf(intrare -> intrare.expirata(acum));
        }
        if (cache.size() < capacitate) {
            long durata = rezultat.isPresent() ? ttlGasit : ttlNegasit;
            cache.put(cui, new Intrare(rezultat, acum + durata));
        }
    }

    private Optional<AnafFirmaDto> asteapta(CompletableFuture<Optional<AnafFirmaDto>> rezultat) {
        try {
            return rezultat.join();
        } catch (CompletionException exceptie) {
            if (exceptie.getCause() instanceof RuntimeException cauza) {
                throw cauza;
            }
            if (exceptie.getCause() instanceof Error cauza) {
                throw cauza;
            }
            throw exceptie;
        }
    }
}
