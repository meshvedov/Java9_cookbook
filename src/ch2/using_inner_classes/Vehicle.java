package ch2.using_inner_classes;

public class Vehicle {
    private int weightPounds;
    private Engine engine;

    public Vehicle(int weightPounds, int horsePower) {
        this.weightPounds = weightPounds;
        engine = new Engine(horsePower);
    }

    private class Engine {
        private int horsePower;

        private Engine(int horsePower) {
            this.horsePower = horsePower;
        }

        private double getSpeedMph(double timeSec) {
            double v = 2.0 * this.horsePower * 746;
            v = v * timeSec * 32.17/getWeightPounds();
            return Math.round(Math.sqrt(v) * 0.68);
        }
    }

    private double getWeightPounds() {
        return weightPounds;
    }

    public double getSpeedMph(double timeSec) {
        return engine.getSpeedMph(timeSec);
    }
}
