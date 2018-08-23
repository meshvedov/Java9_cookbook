package ch7_concurrency;

import java.util.concurrent.Flow;

public class DemoSubscriber<T> implements Flow.Subscriber<T> {
    private String name;
    private Flow.Subscription subscription;

    public DemoSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1);
    }

    @Override
    public void onNext(T item) {
        System.out.println(name + " received: " + item);
        this.subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println("Completed " + name);
    }
}
