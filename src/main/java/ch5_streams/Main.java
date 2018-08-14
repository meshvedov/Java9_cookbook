package main.java.ch5_streams;

import main.java.ch5_streams.api.SpeedModel;
import main.java.ch5_streams.api.TrafficUnit;
import main.java.ch5_streams.api.Vehicle;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        double timeSec = 10.0;
        int trafficUnitNumber = 10;

        SpeedModel speedModel = (t, wp, hp) -> {
            double weightPower = 2.0 * hp * 746 * 32.174 / wp;
            return Math.round(Math.sqrt(t * weightPower) * 0.68);
        };

        BiPredicate<TrafficUnit, Double> limitSpeed = (tu, sp) -> (sp > (tu.getSpeedLimitMph() + 8.0) && tu.getRoadCondition() == SpeedModel.RoadCondition.DRY)
                || (sp > (tu.getSpeedLimitMph() + 5.0) && tu.getRoadCondition() == SpeedModel.RoadCondition.WET)
                || (sp > (tu.getSpeedLimitMph() + 0.0) && tu.getRoadCondition() == SpeedModel.RoadCondition.SNOW);

        BiConsumer<TrafficUnit, Double> printResults = (tu, sp) ->
                System.out.println("Road " + tu.getRoadCondition() + ", tires " + tu.getTireCondition()
                        + ": " + tu.getVehicleType().getType() + " speedMph (" + timeSec + " sec)=" + sp + " mph");

        Traffic api = new TrafficImpl(Month.APRIL, DayOfWeek.FRIDAY, 17, "USA", "Denver", "Main103S");
        api.speedAfterStart(timeSec, trafficUnitNumber, speedModel, limitSpeed, printResults);

        System.out.println();

        List<TrafficUnit> trafficUnits = FactoryTraffic.generateTraffic(trafficUnitNumber, Month.APRIL, DayOfWeek.FRIDAY, 17, "USA", "Denver", "Main103S");
        for (TrafficUnit tu : trafficUnits) {
            Vehicle vehicle = FactoryVehicle.build(tu);
            vehicle.setSpeedModel(speedModel);
            double speed = vehicle.getSpeedMph(timeSec);
            speed = Math.round(speed * tu.getTraction());
            if (limitSpeed.test(tu, speed)) {
                printResults.accept(tu, speed);
            }
        }

        System.out.println();

        getTrafficUnitStream(trafficUnitNumber)
                .map(TrafficUnitWrapper1::new)
                .map(tuw -> {
                    Vehicle vehicle = FactoryVehicle.build(tuw.getTrafficUnit());
                    vehicle.setSpeedModel(speedModel);
                    tuw.setVehicle(vehicle);
                    return tuw;
                })
                .map(tuw -> {
                    double speed = tuw.getVehicle().getSpeedMph(timeSec);
                    speed = Math.round(speed * tuw.getTrafficUnit().getTraction());
                    tuw.setSpeed(speed);
                    return tuw;
                })
                .filter(tuw -> limitSpeed.test(tuw.getTrafficUnit(), tuw.getSpeed()))
                .forEach(tuw -> printResults.accept(tuw.getTrafficUnit(), tuw.getSpeed()));

        System.out.println();

        getTrafficUnitStream(trafficUnitNumber)
                .map(TrafficUnitWrapper2::new)
                .map(tuw -> tuw.setSpeedModel(speedModel))
                .map(tuw -> {
                    double speed = tuw.getVehicle().getSpeedMph(timeSec);
                    speed = Math.round(speed * tuw.getTrafficUnit().getTraction());
                    return tuw.setSpeed(speed);
                })
                .filter(tuw -> limitSpeed.test(tuw.getTrafficUnit(), tuw.getSpeed()))
                .forEach(tuw -> printResults.accept(tuw.getTrafficUnit(), tuw.getSpeed()));

        System.out.println();

        Predicate<TrafficUnit> limitTraffic = tu -> (tu.getHorsePower() < 250 && tu.getVehicleType() == Vehicle.VehicleType.CAR)
                || (tu.getHorsePower() < 400 && tu.getVehicleType() == Vehicle.VehicleType.TRUCK);

        getTrafficUnitStream(trafficUnitNumber)
                .filter(limitTraffic)
                .map(TrafficUnitWrapper3::new)
                .map(tuw -> tuw.setSpeedModel(speedModel))
                .map(tuw -> tuw.calcSpeed(timeSec))
                .filter(tuw -> limitSpeed.test(tuw.getTrafficUnit(), tuw.getSpeed()))
                .forEach(tuw -> printResults.accept(tuw.getTrafficUnit(), tuw.getSpeed()));

        System.out.println();

    }

    private static Stream<TrafficUnit> getTrafficUnitStream(int trafficNumberUnit) {
        return FactoryTraffic.getTrafficUnitStream(trafficNumberUnit, Month.APRIL, DayOfWeek.FRIDAY, 17, "USA", "Denver", "Main103S");
    }

    private static class TrafficUnitWrapper1 {
        private double speed;
        private Vehicle vehicle;
        private TrafficUnit trafficUnit;

        public TrafficUnitWrapper1(TrafficUnit trafficUnit) {
            this.trafficUnit = trafficUnit;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public TrafficUnit getTrafficUnit() {
            return trafficUnit;
        }

        public double getSpeed() {
            return speed;
        }

        public void setSpeed(double speed) {
            this.speed = speed;
        }

        public void setVehicle(Vehicle vehicle) {
            this.vehicle = vehicle;
        }
    }

    private static class TrafficUnitWrapper2 {
        private double speed;
        private Vehicle vehicle;
        private TrafficUnit trafficUnit;

        public TrafficUnitWrapper2(TrafficUnit trafficUnit) {
            this.trafficUnit = trafficUnit;
            this.vehicle = FactoryVehicle.build(trafficUnit);
        }

        public TrafficUnitWrapper2 setSpeedModel(SpeedModel speedModel) {
            this.vehicle.setSpeedModel(speedModel);
            return this;
        }

        public double getSpeed() {
            return speed;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public TrafficUnit getTrafficUnit() {
            return trafficUnit;
        }

        public TrafficUnitWrapper2 setSpeed(double speed) {
            this.speed = speed;
            return this;
        }
    }

    private static class TrafficUnitWrapper3 {
        private double speed;
        private Vehicle vehicle;
        private TrafficUnit trafficUnit;

        public TrafficUnitWrapper3(TrafficUnit trafficUnit) {
            this.trafficUnit = trafficUnit;
            this.vehicle = FactoryVehicle.build(trafficUnit);
        }

        public TrafficUnitWrapper3 setSpeedModel(SpeedModel speedModel) {
            this.vehicle.setSpeedModel(speedModel);
            return this;
        }

        public double getSpeed() {
            return speed;
        }

        public TrafficUnit getTrafficUnit() {
            return trafficUnit;
        }

        public TrafficUnitWrapper3 calcSpeed(double timeSec) {
            double speed = this.vehicle.getSpeedMph(timeSec);
            this.speed = Math.round(speed * this.trafficUnit.getTraction());
            return this;
        }
    }

    public interface Traffic {
        void speedAfterStart(double timeSec, int trafficUnitsNumber, SpeedModel speedModel,
                             BiPredicate<TrafficUnit, Double> limitSpeed, BiConsumer<TrafficUnit, Double> printResult);
    }

    private static class TrafficImpl implements Traffic {
        private Month month;
        private DayOfWeek dayOfWeek;
        private int hour;
        private String country, city, trafficLight;

        public TrafficImpl(Month month, DayOfWeek dayOfWeek, int hour, String country, String city, String trafficLight) {
            this.month = month;
            this.dayOfWeek = dayOfWeek;
            this.hour = hour;
            this.country = country;
            this.city = city;
            this.trafficLight = trafficLight;
        }

        @Override
        public void speedAfterStart(double timeSec, int trafficUnitsNumber, SpeedModel speedModel, BiPredicate<TrafficUnit, Double> limitSpeed, BiConsumer<TrafficUnit, Double> printResult) {
            List<TrafficUnit> trafficUnits = FactoryTraffic.generateTraffic(trafficUnitsNumber, month, dayOfWeek,
                    hour, country, city, trafficLight);
            for (TrafficUnit tu : trafficUnits) {
                Vehicle vehicle = FactoryVehicle.build(tu);
                vehicle.setSpeedModel(speedModel);
                double speed = vehicle.getSpeedMph(timeSec);
                speed = Math.round(speed * tu.getTraction());
                if (limitSpeed.test(tu, speed)) {
                    printResult.accept(tu, speed);
                }
            }
        }
    }
}
