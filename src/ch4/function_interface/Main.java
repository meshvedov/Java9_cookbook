package ch4.function_interface;

import java.util.function.*;

public class Main {
    public static void main(String[] args) {
        demo1();
        demo2();
        demo3();//introducing lamdas
    }

    private static void demo1() {
        Function<Integer, Double> ourFunc = new Function<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return integer * 10.0;
            }
        };
        System.out.println(ourFunc.apply(1));

        Consumer<String> ourConsumer = new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("The " + s + " is consumed.");
            }
        };
        ourConsumer.accept("Hello !");

        Supplier<String> ourSupplier = new Supplier<String>() {
            @Override
            public String get() {
                return "Success";
            }
        };
        System.out.println(ourSupplier.get());

        Predicate<Double> ourPredicate = new Predicate<Double>() {
            @Override
            public boolean test(Double num) {
                System.out.println("Test if " + num + " is smaller than 20");
                return num < 20;
            }
        };
        System.out.println(ourPredicate.test(2D) ? "yes is smaller" : "no");

        IntFunction<String> function = new IntFunction<String>() {
            @Override
            public String apply(int value) {
                return String.valueOf(value * 10);
            }
        };
        System.out.println(function.apply(1));

        BiFunction<String, Integer, Double> biFunction = new BiFunction<String, Integer, Double>() {
            @Override
            public Double apply(String s, Integer integer) {
                return (s.length() * 10d) / integer;
            }
        };
        System.out.println(biFunction.andThen(x -> x * 10d).apply("1", 2));

        BinaryOperator<Integer> binaryOperator = new BinaryOperator<Integer>() {
            @Override
            public Integer apply(Integer i, Integer j) {
                return i >= j ? i : j;
            }
        };
        System.out.println(binaryOperator.andThen(x -> x * x).apply(1, 2));
    }

    private static void demo2() {
        Function<Integer, Double> multiplyBy10 = createMultiplyBy10(10d);
        System.out.println(multiplyBy10.apply(2));

        Function<Double, Double> subtract7 = createSubtract(7.0);
        System.out.println(subtract7.apply(10d));
    }

    private static Function<Double,Double> createSubtract(double num) {
        Function<Double, Double> ourFunc = new Function<Double, Double>() {
            @Override
            public Double apply(Double aDouble) {
                return aDouble - num;
            }
        };
        return ourFunc;
    }

    private static Function<Integer, Double> createMultiplyBy10(double num) {
        Function<Integer, Double> ourFunc = new Function<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return integer * num;
            }
        };
        return ourFunc;
    }
    //**************************************************************************************************************
    private static void demo3() {
        Function<Integer, Double> ourFunc = i -> i * 10d;
        System.out.println(ourFunc.apply(1));

        Consumer<String> consumer = s -> System.out.println("The " + s + " is consumed");
        consumer.accept("Hello");

        Supplier<String> stringSupplier = () -> "Success";
        System.out.println(stringSupplier.get());

        Predicate<Double> pred = num -> {
            System.out.println("Test if " + num + " is smaller than 20");
            return num < 20;
        };
        System.out.println(pred.test(10d) ? "10 is smaller " : "10 is bigger");


    }
}
