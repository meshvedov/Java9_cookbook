package main.java.ch5_streams;

import main.java.ch5_streams.api.SpeedModel;
import main.java.ch5_streams.api.Vehicle;
import main.java.ch5_streams.api.TrafficUnit;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FactoryTraffic {

    public static Stream<TrafficUnit> getTrafficUnitStream(int trafficUnitNumber, Month month, DayOfWeek dayOfWeek, int hour,
                                                           String country, String city, String trafficLight) {
        return IntStream.range(0, trafficUnitNumber).mapToObj(i -> generateOneUnit(month, dayOfWeek, hour, country, city, trafficLight));
    }

    public static List<TrafficUnit> generateTraffic(int trafficUnitNumber, Month month, DayOfWeek dayOfWeek, int hour, String country, String city, String trafficLight) {
        return IntStream.range(0, trafficUnitNumber).mapToObj(i -> generateOneUnit(month, dayOfWeek, hour, country, city, trafficLight))
                .collect(Collectors.toList());
    }

    public static TrafficUnit generateOneUnit(Month month, DayOfWeek dayOfWeek, int hour, String country, String city, String trafficLight) {
        double r0 = Math.random();
        Vehicle.VehicleType vehicleType = r0 < 0.4 ? Vehicle.VehicleType.CAR : (r0 > 0.6 ? Vehicle.VehicleType.TRUCK : Vehicle.VehicleType.CAB_CREW);
        double r1 = Math.random();
        double r2 = Math.random();
        double r3 = Math.random();
        return new TrafficModelImpl(vehicleType, gen(4, 1), gen(3300, 1000), gen(246, 100), gen(4000, 2000),
                r1 > 0.5 ? SpeedModel.RoadCondition.WET : SpeedModel.RoadCondition.DRY, (r2 > 0.5 ? SpeedModel.TireCondition.WORN : SpeedModel.TireCondition.NEW),
                r1 > 0.5 ? (r3 > 0.5 ? 63 : 50) : 63);
    }

    private static int gen(int i1, int i2) {
        double r = Math.random();
        return (int)Math.rint(r * i1) + i2;
    }

    private static class TrafficModelImpl implements TrafficUnit {
        private Vehicle.VehicleType vehicleType;
        private int passengerCount, payloadPounds, horsePower, weightPounds;
        private SpeedModel.RoadCondition roadCondition;
        private SpeedModel.TireCondition tireCondition;
        private int temperature;

        public TrafficModelImpl(Vehicle.VehicleType vehicleType, int passengerCount, int payloadPounds, int engineHorsePower, int vehicleWeightPounds,
                                SpeedModel.RoadCondition roadCondition, SpeedModel.TireCondition tireCondition, int temperatureFarenheit) {
            this.vehicleType = vehicleType;
            this.passengerCount = passengerCount;
            this.payloadPounds = payloadPounds;
            this.horsePower = engineHorsePower;
            this.weightPounds = vehicleWeightPounds;
            this.roadCondition = roadCondition;
            this.tireCondition = tireCondition;
            this.temperature = temperatureFarenheit;
        }

        @Override
        public Vehicle.VehicleType getVehicleType() {
            return vehicleType;
        }

        @Override
        public int getHorsePower() {
            return horsePower;
        }

        @Override
        public int getWeightPounds() {
            return weightPounds;
        }

        @Override
        public int getPayloadsPounds() {
            return payloadPounds;
        }

        @Override
        public int getPassengersCount() {
            return passengerCount;
        }

        @Override
        public double getSpeedLimitMph() {
            return 55.0;
        }

        @Override
        public double getTraction() {
            SpeedModel.RoadCondition.temperature = getTemperature();
            double rt = getRoadCondition().getTraction();
            double tt = getTireCondition().getTraction();
            return rt * tt;
        }

        @Override
        public SpeedModel.RoadCondition getRoadCondition() {
            return roadCondition;
        }

        @Override
        public SpeedModel.TireCondition getTireCondition() {
            return tireCondition;
        }

        @Override
        public int getTemperature() {
            return temperature;
        }
    }
}
