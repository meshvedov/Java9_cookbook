package ch2.using_classes;

public class Vehicle {
    private int weightPounds;
    private Engine engine;

    public Vehicle(int weightPounds, Engine engine) {
        if (engine == null) {
            throw new NullPointerException("Engine is required parameter");
        }
        this.weightPounds = weightPounds;
        this.engine = engine;
    }

    public double getSpeedMph(double timeSec) {
        double v = 2.0 * engine.getHorsePower() * 746;
        v = v * timeSec * 32.17/weightPounds;
        return Math.round(Math.sqrt(v) * 0.68);
    }
}
