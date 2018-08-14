package main.java.ch2.using_inheritance;

public class Truck extends Vehicle {
    private int payload;

    public Truck(int payload, int weightPounds, int horsePower) {
        super(weightPounds + payload, horsePower);
        this.payload = payload;
    }

    public int getPayload() {
        return payload;
    }

}
