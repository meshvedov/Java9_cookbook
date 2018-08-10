package ch2.using_classes;

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
