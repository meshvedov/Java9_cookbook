package main.java.ch2.using_classes;

import main.java.ch2.using_classes.Engine;
import main.java.ch2.using_classes.Vehicle;

public class Main {
    public static void main(String[] args) {
        double timeSec = 10.0;
        int horsePower = 246;
        int vehicleWeight = 4000;
        Engine engine = new Engine();
        engine.setHorsePower(horsePower);
        Vehicle vehicle = new Vehicle(vehicleWeight, engine);
        System.out.printf("Vehicle speed (" + timeSec + " sec) = " + vehicle.getSpeedMph(timeSec) + " mph\n");
    }
}
