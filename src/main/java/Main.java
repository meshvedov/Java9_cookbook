import java.util.HashMap;
import java.util.Map;

public class Main {
    private static int count = 0;
    public static void main(String[] args) {
        System.out.println("Hello World!");
        factorialUtil(5);
        fibonacciUtil(10);
    }

    private static void fibonacciUtil(int i) {
        count = 0;
        Map<Integer, Integer> map = new HashMap<>();
        System.out.println("fibonacci " + i + " = " + fibonacci(i, map) + ", counts = " + count);
    }

    private static int fibonacci(int n, Map<Integer, Integer> map) {
        count++;
        if (n <= 2) {
            return 1;
        }
        if (map.containsKey(n)) {
            return map.get(n);
        }
        int v1 = fibonacci(n - 1, map);
        map.put(n - 1, v1);
        return v1 + fibonacci(n - 2, map);
    }

    private static void factorialUtil(int i) {
        System.out.println("factorial " + i + " = " + factorial(i) + ", counts = " + count);
    }

    private static int factorial(int i) {
        count++;
        if (i <= 1) {
            return 1;
        }
        return i * factorial(i - 1);
    }
}