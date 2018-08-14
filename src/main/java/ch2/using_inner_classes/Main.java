package main.java.ch2.using_inner_classes;

public class Main {
    public static void main(String[] args) {
        double timeSec = 10.0;
        int engineHorsePower = 246;
        int vehicleWeightPounds = 4000;
        Vehicle vehicle = new Vehicle(vehicleWeightPounds, engineHorsePower) {
            @Override
            public double getSpeedMph(double timeSec) {
//                return -1.0d;
                double v = 2.0 * engineHorsePower * 746;
                v = v * timeSec * 32.174 / vehicleWeightPounds;
                return Math.round(Math.sqrt(v) * 0.68);
            }
        };
        System.out.printf("Vehicle speed (" + timeSec + " sec) = " + vehicle.getSpeedMph(timeSec) + " mph\n");
    }
}
