package ch7_concurrency;

import ch7_concurrency.api.DateLocation;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class Chapter07Concurrency04 {
    public static void main(String[] args) {
        demo1_ForkJoin_fork_join();
        demo2_ForkJoin_execute_join();
        demo3_ForkJoin_invoke();

        demo4_Flow_submissionPublisher();
    }

    private static void demo4_Flow_submissionPublisher() {
        System.out.println("demo4=======================");
        ExecutorService execService = ForkJoinPool.commonPool();
        try (SubmissionPublisher<Integer> publisher = new SubmissionPublisher<>()) {
            demoSubscribe(publisher, execService, "One");
            demoSubscribe(publisher, execService, "Two");
            demoSubscribe(publisher, execService, "Three");
            IntStream.range(1, 5).forEach(publisher::submit);
        } finally {
            try {
                execService.shutdown();
                int shutdownDelaySec = 1;
                System.out.println("Waiting for " + shutdownDelaySec + " sec before shutting down service...");
                execService.awaitTermination(shutdownDelaySec, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                System.out.println("Caught around execService.awaitTermination(): " + e.getClass().getName());
            } finally {
                System.out.println("Calling execService.shutdownNow()...");
                List<Runnable> l = execService.shutdownNow();
                System.out.println(l.size() + " tasks were waiting to be executed. Service stopped.");
            }
        }
    }

    private static void demoSubscribe(SubmissionPublisher<Integer> publisher, ExecutorService execService, String subscriberName) {
        DemoSubscriber<Integer> subscriber = new DemoSubscriber<>(subscriberName);
        DemoSubscription subscription = new DemoSubscription(subscriber, execService);
        subscriber.onSubscribe(subscription);
        publisher.subscribe(subscriber);
    }

    private static void demo3_ForkJoin_invoke() {
        System.out.println("demo3 ===============================");
        AverageSpeed averageSpeed = createTask();
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        double result = commonPool.invoke(averageSpeed);
        System.out.println("demo3 : result = " + result);
    }

    private static void demo2_ForkJoin_execute_join() {
        System.out.println("demo2 ===============================");
        AverageSpeed averageSpeed = createTask();
        ForkJoinPool commonPool = ForkJoinPool.commonPool();
        commonPool.execute(averageSpeed);
        double result = averageSpeed.join();
        System.out.println("demo2 : result = " + result);
    }

    private static void demo1_ForkJoin_fork_join() {
        System.out.println();
        AverageSpeed averageSpeed = createTask();
        averageSpeed.fork();
        double result = averageSpeed.join();
        System.out.println("demo1 : result = " + result);

    }

    private static AverageSpeed createTask() {
        DateLocation dateLocation = new DateLocation(Month.APRIL, DayOfWeek.FRIDAY, 17, "USA", "Denver", "Main103S");
        return new AverageSpeed(dateLocation, 10, 1001, 100);
    }
}
