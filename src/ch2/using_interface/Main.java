package ch2.using_interface;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        double timeSec = 10.0;
        int horsePower = 246;
        int vehicleWeight = 4000;
        Properties drivingConditions = new Properties();
        drivingConditions.put("road_Condition".toUpperCase(), "Wet");
        drivingConditions.put("tire_Condition".toUpperCase(), "New");
        SpeedModel speedModel = FactorySpeedModel.generateSpeedModel(drivingConditions);
        Car car = FactoryVehicle.buildCar(4, vehicleWeight, horsePower);
        car.setSpeedModel(speedModel);
        System.out.println("Car speed " + timeSec + " sec = " + car.getSpeedMph(timeSec) + " mph");

        Truck truck = FactoryVehicle.buildTruck(3300, vehicleWeight, horsePower);
        System.out.println("Payload in pounds: " + truck.getPayloadPounds());
        System.out.println("Payload in kg: " + truck.getPayloadKg(3300));
        //*****************************************************************************
        String[] roadConditions = {SpeedModel.RoadCondition.WET.toString(), SpeedModel.RoadCondition.SNOW.toString()};
        String[] tireConditions = {SpeedModel.TireCondition.NEW.toString(), SpeedModel.TireCondition.WORN.toString()};

        for (String roadCondition : roadConditions) {
            for (String tireCondition : tireConditions) {
                Properties drivingCond = new Properties();
                drivingCond.put(SpeedModel.DrivingCondition.ROAD_CONDITION.toString(), roadCondition);
                drivingCond.put(SpeedModel.DrivingCondition.TIRE_CONDITION.toString(), tireCondition);
                SpeedModel speedModel1 = FactorySpeedModel.generateSpeedModel(drivingCond);
                Car car1 = FactoryVehicle.buildCar(4, vehicleWeight, horsePower);
                car1.setSpeedModel(speedModel1);
                System.out.println("Car speed = " + car1.getSpeedMph(timeSec) + " mph");
            }
        }
    }
}
