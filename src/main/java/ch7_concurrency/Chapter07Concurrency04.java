package ch7_concurrency;

import ch7_concurrency.api.DateLocation;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.concurrent.ForkJoinPool;

public class Chapter07Concurrency04 {
    public static void main(String[] args) {
        demo1_ForkJoin_fork_join();
        demo2_ForkJoin_execute_join();
        demo3_ForkJoin_invoke();
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
