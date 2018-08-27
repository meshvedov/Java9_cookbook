package ch15_testing.process;

import ch15_testing.TrafficDensity;
import ch15_testing.utils.DbUtil;
import ch15_testing.AverageSpeed;
import ch15_testing.factories.DateLocation;

import java.util.Arrays;
import java.util.concurrent.Flow;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Processor<T> implements Flow.Subscriber<T> {
    private Process process;
    private double timeSec;
    private DateLocation dateLocation;
    private double[] speedLimitByLane;
    private String result;
    private int trafficUnitsNumber;
    private Flow.Subscription subscription;

    public Processor(Process process, double timeSec, DateLocation dateLocation, double[] speedLimitByLane) {
        this.process = process;
        this.timeSec = timeSec;
        this.dateLocation = dateLocation;
        this.speedLimitByLane = speedLimitByLane;
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        this.subscription = subscription;
        this.subscription.request(1); //0
    }

    @Override
    public void onNext(T item) {
        if (item != null) {
            trafficUnitsNumber = (int)item;
            switch (process) {
                case AVERAGE_SPEED:
                    calcAverageSpeed(trafficUnitsNumber);
                    break;
                case TRAFFIC_DENSITY:
                    calcTrafficDensity(trafficUnitsNumber);
                    break;
            }
        }
        this.subscription.request(1);
    }

    private void calcTrafficDensity(int trafficUnitsNumber) {
        result = Arrays.stream(new TrafficDensity().trafficByLane(trafficUnitsNumber, timeSec, dateLocation, speedLimitByLane))
        .map(Object::toString).collect(Collectors.joining(", "));

    }

    private void calcAverageSpeed(int trafficUnitsNumber) {
        result = IntStream.rangeClosed(1, speedLimitByLane.length).mapToDouble(i -> {
            AverageSpeed averageSpeed = new AverageSpeed(trafficUnitsNumber, timeSec, dateLocation, speedLimitByLane, i, 100);
            ForkJoinPool commonPool = ForkJoinPool.commonPool();
            return commonPool.invoke(averageSpeed);
        }).mapToObj(Double::toString).collect(Collectors.joining(", "));
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        System.out.println(process + "(" + trafficUnitsNumber + "):" + result);
        DbUtil.storeResult(process.name(), trafficUnitsNumber, timeSec, dateLocation.toString(), speedLimitByLane, result);
    }
}
