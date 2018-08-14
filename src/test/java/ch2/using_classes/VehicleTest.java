package ch2.using_classes;

import org.junit.Test;

import static org.junit.Assert.*;

public class VehicleTest {

    @Test
    public void getSpeedMph() {
        double timeSec = 10d;
        int engineHP = 246;
        int vehicleWeightPounds = 4000;
        Engine engine = new Engine();
        engine.setHorsePower(engineHP);
        Vehicle vehicle = new Vehicle(vehicleWeightPounds, engine);
        double speed = vehicle.getSpeedMph(timeSec);
        assertEquals("Assert vehicle (" + engineHP
                + " hp, " + vehicleWeightPounds + " lb) speed in "
                + timeSec + " sec: ", 117, speed, 0.001 * speed);
    }

    @Test
    public void testGetSpeedMphException() {
        int vehiclePounds = 4000;
        Engine engine = new Engine();
        try {
            Vehicle vehicle = new Vehicle(vehiclePounds, engine);
            fail("Exception was not thrown");
        } catch (Exception e) {

        }
    }
}