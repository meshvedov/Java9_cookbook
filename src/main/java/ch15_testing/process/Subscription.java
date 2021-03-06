package ch15_testing.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;

public class Subscription<T> implements Flow.Subscription {
    private final Flow.Subscriber<T> subscriber;
    private final ExecutorService executor;
    private Future<?> future;
    private T item;

    public Subscription(Flow.Subscriber<T> subscriber, ExecutorService executor) {
        this.subscriber = subscriber;
        this.executor = executor;
    }

    @Override
    public void request(long n) {
        future = executor.submit(() -> this.subscriber.onNext(item));
    }

    @Override
    public synchronized void cancel() {
        if (future != null && !future.isCancelled()) {
            this.future.cancel(true);
        }
    }
}
