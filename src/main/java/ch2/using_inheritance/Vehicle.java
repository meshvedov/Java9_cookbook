package main.java.ch2.using_inheritance;

public class Vehicle {
    private SpeedModel speedModel;

    private int weightPounds, horsePower;

    public Vehicle(int weightPounds, int horsePower) {
        this.weightPounds = weightPounds;
        this.horsePower = horsePower;
    }

    protected double getSpeedMph(double timeSec) {
        return this.speedModel.getSpeedMph(timeSec, weightPounds, horsePower);
    }

    public void setSpeedModel(SpeedModel speedModel) {
        this.speedModel = speedModel;
    }
}
