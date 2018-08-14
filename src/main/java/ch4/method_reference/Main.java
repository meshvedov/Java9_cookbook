package main.java.ch4.method_reference;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class Main {
    public static void main(String[] args) {
        demo1_Food();
    }

    private static void demo1_Food() {
        Supplier<String> supplier = Food::getFavorite;
        System.out.println("supplier.get() => " + supplier.get());

        Function<Integer, String> func = Food::getFavorite;
        System.out.println("func.apply(1) => " + func.apply(1));
        System.out.println("func.apply(2) => " + func.apply(2));

        Supplier<Food> constrFood = Food::new;
        Food food = constrFood.get();
        System.out.println("new Food().sayFavorite " + food.sayFavorite());

        Function<String, Food> constrFood1 = Food::new;
        food = constrFood1.apply("Donuts");
        System.out.println("new Food(Donuts).sayFavorite() " + food.sayFavorite());
        food = constrFood1.apply("Carrot");
        System.out.println("new Food(Carrot).sayFavorite() " + food.sayFavorite());

        BiFunction<String, String, Food> constrFood2 = Food::new;
        food = constrFood2.apply("Donuts", "Carrots");
        System.out.println("new Food(Donuts, Carrot).sayFavorite() " + food.sayFavorite());

        Function<Integer, String[]> createArray = String[]::new;
        String[] arr = createArray.apply(3);
        System.out.println("Array length " + arr.length);
    }

    private static class Food {
        private String name;
        public Food() {
            this.name = "Donut!";
        }

        public Food(String name) {
            this.name = name;
        }

        public Food(String name, String another) {
            this.name = name + " and " + another;
        }

        public String sayFavorite() {
            return this.name + (this.name.toLowerCase().contains("donut") ? "? Yes" : "? D'oh!");
        }
        public static String getFavorite() {
            return "Donut!";
        }

        public static String getFavorite(int num) {
            return num > 1 ? String.valueOf(num) + " donuts!" : "Donut!";
        }
    }
}
