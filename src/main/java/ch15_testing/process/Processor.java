package ch15_testing.process;

import ch7_concurrency.api.DateLocation;

import java.util.concurrent.Flow;
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
    }

    private void calcAverageSpeed(int trafficUnitsNumber) {
        result = IntStream.rangeClosed(1, speedLimitByLane.length).mapToDouble(i -> {

        })
    }

    @Override
    public void onError(Throwable throwable) {
        
    }

    @Override
    public void onComplete() {

    }
}
