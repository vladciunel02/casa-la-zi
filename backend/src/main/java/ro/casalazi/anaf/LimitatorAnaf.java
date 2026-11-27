package ro.casalazi.anaf;

import ro.casalazi.errors.anaf.AnafLimitaException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

final class LimitatorAnaf {

    private static final long PAUZA = TimeUnit.SECONDS.toNanos(1);

    private final LongSupplier nanoTime;
    private final ReentrantLock apel = new ReentrantLock();
    private boolean aRulat;
    private long ultimulFinal;

    LimitatorAnaf(LongSupplier nanoTime) {
        this.nanoTime = nanoTime;
    }

    <T> T executa(Supplier<T> operatie) {
        if (!apel.tryLock()) {
            throw new AnafLimitaException();
        }

        try {
            if (aRulat && nanoTime.getAsLong() - ultimulFinal < PAUZA) {
                throw new AnafLimitaException();
            }
            try {
                return operatie.get();
            } finally {
                // masuram de la finalul apelului, inclusiv cand acesta esueaza.
                ultimulFinal = nanoTime.getAsLong();
                aRulat = true;
            }
        } finally {
            apel.unlock();
        }
    }
}
