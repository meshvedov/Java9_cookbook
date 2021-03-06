package ch5_streams;

import ch5_streams.api.SpeedModel;
import ch5_streams.api.TrafficUnit;
import ch5_streams.api.Vehicle;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TrafficDensity2 {

    public Integer[] trafficByLane(Stream<TrafficUnit> stream, int trafficUnitNumber, double timeSec, SpeedModel speedModel, double[] speedLimitByLane) {
        int lanesCount = speedLimitByLane.length;
        Map<Integer, Integer> trafficByLane = stream
                .limit(trafficUnitNumber)
                .map(TrafficUnitWrapper::new)
                .map(tuw -> tuw.setSpeedModel(speedModel))
                .map(tuw -> calcSpeed(tuw.getVehicle(), tuw.getTraction(), timeSec))
                .map(speed -> countByLane(lanesCount, speedLimitByLane, speed))
                .collect(Collectors.groupingBy(CountByLane::getLane, Collectors.summingInt(CountByLane::getCount)));

        for (int i = 1; i <= lanesCount; i++) {
            trafficByLane.putIfAbsent(i, 0);
        }
        return trafficByLane.values().toArray(new Integer[lanesCount]);
    }

    private CountByLane countByLane(int lanesCount, double[] speedLimitByLane, Double speed) {
        for (int i = 1; i <= lanesCount; i++) {
            if (speed <= speedLimitByLane[i - 1]) {
                return new CountByLane(1, i);
            }
        }
        return new CountByLane(1, lanesCount);
    }

    protected double calcSpeed(Vehicle vehicle, double traction, double timeSec) {
        double speed = vehicle.getSpeedMph(timeSec);
        return Math.round(speed * traction);
    }


    private class CountByLane {
        int count, lane;

        public CountByLane(int count, int lane) {
            this.count = count;
            this.lane = lane;
        }

        public int getCount() {
            return count;
        }

        public int getLane() {
            return lane;
        }
    }

    private static class TrafficUnitWrapper {
        private Vehicle vehicle;
        private TrafficUnit trafficUnit;

        public TrafficUnitWrapper(TrafficUnit trafficUnit) {
            this.trafficUnit = trafficUnit;
            this.vehicle = FactoryVehicle.build(trafficUnit);
        }

        public TrafficUnitWrapper setSpeedModel(SpeedModel speedModel) {
            this.vehicle.setSpeedModel(speedModel);
            return this;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public double getTraction() {
            return trafficUnit.getTraction();
        }
    }
}
