package ch5_streams;

import ch5_streams.api.TrafficUnit;
import ch5_streams.api.Vehicle;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class TrafficDensity2Test {
    @Test
    public void testSpeedModel() {
        double timeSec = 10.0;
        int engineHorsePower = 246;
        int vehicleWeightPounds = 4000;
        double speed = FactorySpeedModel.getSpeedModel().getSpeedMph(timeSec, vehicleWeightPounds, engineHorsePower);
        assertEquals("Assert vehicle (" + engineHorsePower + " hp, " + vehicleWeightPounds + " lb) speed in " + timeSec + " sec: ", 117, speed, 0.001 * speed);
    }

    @Test
    public void testCalcSpeed() {
        double timeSec = 10.0;
        TrafficDensity2 trafficDensity2 = new TrafficDensity2();
        Vehicle vehicle = Mockito.mock(Vehicle.class);
        Mockito.when(vehicle.getSpeedMph(timeSec)).thenReturn(100d);
        double traction = 0.2;
        double speed = trafficDensity2.calcSpeed(vehicle, traction, timeSec);
        assertEquals("Assert speed (traction=" + traction + ") in " + timeSec + " sec: ", 20, speed, 0.001 * speed);
    }

    @Test
    public void testCountByLane() {
        int[] count = {0};
        double[] speeds = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12};
        TrafficDensity2 trafficDensity2 = new TrafficDensity2(){
            @Override
            protected double calcSpeed(Vehicle vehicle, double traction, double timeSec) {
                return speeds[count[0]++];
            }
        };
        double timeSec = 10.0;
        int trafficUnitsNumber = speeds.length;
        double[] speedLimitByLane = {4.5, 8.5, 12.5};
        Integer[] expectedCountByLane = {4, 4, 4};
        Integer[] trafficByLane = trafficDensity2.trafficByLane(getTrafficUnitStream(trafficUnitsNumber), trafficUnitsNumber, timeSec, FactorySpeedModel.getSpeedModel(), speedLimitByLane);
        assertArrayEquals("Assert count of " + speeds.length + " vehicles by " + speedLimitByLane.length + " lanes with speed limit "
                        + Arrays.stream(speedLimitByLane).mapToObj(Double::toString).collect(Collectors.joining(", ")),
                expectedCountByLane, trafficByLane);
    }

    private Stream<TrafficUnit> getTrafficUnitStream(int trafficUnitsNumber) {
        return FactoryTraffic.getTrafficUnitStream(trafficUnitsNumber, Month.APRIL, DayOfWeek.FRIDAY, 17, "USA", "Denver", "Main103S");
    }
}
