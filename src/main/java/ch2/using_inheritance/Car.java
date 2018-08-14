package ch2.using_inheritance;

public class Car extends Vehicle {
    private int passengersCount;
    private int weightPounds;

    public Car(int passengersCount, int weightPounds, int horsePower) {
        super(weightPounds + passengersCount * 250, horsePower);
        this.passengersCount = passengersCount;
        this.weightPounds = weightPounds;
    }
    public int getPassengersCount() {
        return this.passengersCount;
    }

}
