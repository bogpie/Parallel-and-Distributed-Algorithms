package doubleCalculator;

import java.util.concurrent.*;


public class DoubleCalculatorMain {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        DoubleCalculator doubleCalculator = new DoubleCalculator();
        Future<Integer> future = doubleCalculator.calculate(10);

        Integer result = future.get();
        System.out.println(result);

        doubleCalculator.shutdown();
    }
}