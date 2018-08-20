package ch7_concurrency;

import java.util.Random;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

public class Chapter07Concurrency01 {
    public static void main(String[] args) {
        demo1_thread();
        demo2_runnable1();
        demo3_lambda1();
        demo4_synchronize1();
        demo4_synchronize2();

    }
    private static void demo4_synchronize2() {
        System.out.println("demo4 - 2 ====================");

        Thread thr1 = new Thread(() -> System.out.println(IntStream.range(1, 4)
                .peek(x -> DoubleStream.generate(new Random()::nextDouble).limit(10))
                .mapToDouble(x -> {
                    Calculator c = new Calculator();
                    return c.calculate(x);
                }).sum()));
        thr1.start();

        Thread thr2 = new Thread(() -> System.out.println(IntStream.range(1, 4)
                .mapToDouble(x -> {
                    Calculator c = new Calculator();
                    return c.calculate(x);
                }).sum()));
        thr2.start();
    }

    private static void demo4_synchronize1() {
        System.out.println("demo4+===========================");
        Calculator c = new Calculator();
        Thread thread1 = new Thread(() -> System.out.println(IntStream.range(1, 4)
                .peek(x -> DoubleStream.generate(new Random()::nextDouble).limit(10))
                .mapToDouble(c::calculate).sum()));
        thread1.start();
        Thread thread2 = new Thread(() -> System.out.println(IntStream.range(1, 4)
                .mapToDouble(c::calculate).sum()));
        thread2.start();
    }

    private static class Calculator {
        private double prop;
        private Object calculateLock = new Object();

        public double calculate(int i) {
            synchronized (calculateLock) {
                this.prop = 2.0 * i;
                DoubleStream.generate(new Random()::nextDouble).limit(100);
                return Math.sqrt(prop);
            }
        }
    }


    private static void demo3_lambda1() {
        System.out.println("demo3================");
        Thread thread1 = new Thread(() -> IntStream.range(1, 4).peek(Chapter07Concurrency01::doSomething).forEach(System.out::println));
        thread1.start();
        Thread thread2 = new Thread(() -> IntStream.range(11, 14).peek(Chapter07Concurrency01::doSomething).forEach(System.out::println));
        thread2.start();
        IntStream.range(21, 24).peek(Chapter07Concurrency01::doSomething).forEach(System.out::println);
    }

    private static void demo2_runnable1() {
        Thread thread1 = new Thread(new ARunnable(1, 4));
        thread1.start();
        Thread thread2 = new Thread(new ARunnable(11, 14));
        thread2.start();
        IntStream.range(21, 24).peek(Chapter07Concurrency01::doSomething).forEach(System.out::println);
    }

    private static class ARunnable implements Runnable {
        int i1, i2;

        public ARunnable(int i1, int i2) {
            this.i1 = i1;
            this.i2 = i2;
        }

        @Override
        public void run() {
            IntStream.range(i1, i2).peek(Chapter07Concurrency01::doSomething).forEach(System.out::println);
        }
    }

    private static void demo1_thread() {
        Thread thread1 = new AThread(1, 4);
        thread1.start();
        Thread thread2 = new AThread(11, 14);
        thread2.start();
        IntStream.range(21, 24).peek(Chapter07Concurrency01::doSomething).forEach(System.out::println);
    }

    private static class AThread extends Thread {
        int i1, i2;

        AThread(int i1, int i2) {
            this.i1 = i1;
            this.i2 = i2;
        }

        @Override
        public void run() {
            IntStream.range(i1, i2).peek(Chapter07Concurrency01::doSomething).forEach(System.out::println);
        }
    }

    private static int doSomething(int i) {
        IntStream.range(i, 99999).asDoubleStream().map(Math::sqrt).average();
        return i;
    }
}
