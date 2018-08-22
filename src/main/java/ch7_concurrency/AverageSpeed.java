package ch7_concurrency;

import ch7_concurrency.api.DateLocation;
import ch7_concurrency.api.SpeedModel;
import ch7_concurrency.api.TrafficUnit;
import ch7_concurrency.api.Vehicle;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class AverageSpeed extends RecursiveTask<Double> {
    private DateLocation dateLocation;
    private double timeSec;
    private int trafficUnitsNumber, threshold;

    public AverageSpeed(DateLocation dateLocation, double timeSec, int trafficUnitsNumber, int threshold) {
        this.dateLocation = dateLocation;
        this.timeSec = timeSec;
        this.trafficUnitsNumber = trafficUnitsNumber;
        this.threshold = threshold;
    }

    @Override
    protected Double compute() {
        if (trafficUnitsNumber < threshold) {
            double speed = FactoryTraffic.getTrafficUnitStream(dateLocation, trafficUnitsNumber)
                    .map(TrafficUnitWrapper::new)
                    .map(tuw -> tuw.setSpeedModel(FactorySpeedModel.generateSpeedModel(tuw.getTrafficUnit())))
                    .map(tuw -> tuw.calcSpeed(timeSec))
                    .mapToDouble(TrafficUnitWrapper::getSpeed)
                    .average()
                    .getAsDouble();
            System.out.println("speed (" + trafficUnitsNumber + ") = " + speed);
            return (double) Math.round(speed);
        } else {
            int tun = trafficUnitsNumber / 2;
            System.out.println("tun = " + tun);
            AverageSpeed as1 = new AverageSpeed(dateLocation, timeSec, tun, threshold);
            AverageSpeed as2 = new AverageSpeed(dateLocation, timeSec, tun, threshold);

//            return doForkJoin1(as1, as2);
//            return doForkJoin2(as1, as2);
//            return doInvoke(as1, as2);
            return doInvokeAll(as1, as2);
        }
    }

    private Double doInvokeAll(AverageSpeed as1, AverageSpeed as2) {
        return ForkJoinTask.invokeAll(List.of(as1, as2))
                .stream()
                .mapToDouble(ForkJoinTask::join)
                .map(Math::round)
                .average()
                .getAsDouble();
    }

    private Double doInvoke(AverageSpeed as1, AverageSpeed as2) {
        double res1 = as1.invoke();
        double res2 = as2.invoke();
        return (double) Math.round((res1 + res2) / 2);
    }

    private Double doForkJoin2(AverageSpeed as1, AverageSpeed as2) {
        as1.fork();
        double res1 = as2.compute();
        double res2 = as1.join();
        return (double) Math.round((res1 + res2) / 2);
    }

    private Double doForkJoin1(AverageSpeed as1, AverageSpeed as2) {
        as1.fork();
        double res1 = as1.join();
        as2.fork();
        double res2 = as2.join();
        return (double) Math.round((res1 + res2) / 2);
    }

    private class TrafficUnitWrapper {
        private TrafficUnit trafficUnit;
        private double speed;
        private Vehicle vehicle;

        public TrafficUnitWrapper(TrafficUnit trafficUnit) {
            this.trafficUnit = trafficUnit;
            this.vehicle = FactoryVehicle.build(trafficUnit);
        }

        public TrafficUnit getTrafficUnit() {
            return trafficUnit;
        }

        public double getSpeed() {
            return speed;
        }

        public TrafficUnitWrapper calcSpeed(double timeSec) {
            double speed = this.vehicle.getSpeedMph(timeSec);
            this.speed = Math.round(speed * this.trafficUnit.getTraction());
            return this;
        }

        public TrafficUnitWrapper setSpeedModel(SpeedModel speedModel) {
            this.vehicle.setSpeedModel(speedModel);
            return this;
        }
    }
}
