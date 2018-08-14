package ch2.using_inheritance;

import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        double timeSec = 10.0;
        int engineHorsePower = 246;
        int vehicleWeightPounds = 4000;
        Properties drivingConditions = new Properties();
        drivingConditions.put("roadCondition", "Wet");
        drivingConditions.put("tireCondition", "New");
        SpeedModel speedModel = new SpeedModel(drivingConditions);
        Car car = new Car(4, vehicleWeightPounds, engineHorsePower);
        car.setSpeedModel(speedModel);
        System.out.println("Passengers count " + car.getPassengersCount());
        System.out.printf("Car speed " + car.getSpeedMph(timeSec));

    }
}
