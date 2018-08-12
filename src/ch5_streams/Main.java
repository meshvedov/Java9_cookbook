import api.SpeedModel;
import api.TrafficUnit;
import api.Vehicle;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
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


    }

    private static Stream<TrafficUnit> getTrafficUnitStream(int trafficNumberUnit) {
        return FactoryTraffic.getTrafficUnitStream(trafficNumberUnit, Month.APRIL, DayOfWeek.FRIDAY, 17, "USA", "Denver", "Main103S");
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
