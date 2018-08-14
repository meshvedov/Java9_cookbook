package main.java.ch5_streams.api;

import main.java.ch5_streams.api.Vehicle.VehicleType;

public interface TrafficUnit {
    VehicleType getVehicleType();

    int getHorsePower();

    int getWeightPounds();

    int getPayloadsPounds();

    int getPassengersCount();

    double getSpeedLimitMph();

    double getTraction();

    SpeedModel.RoadCondition getRoadCondition();

    SpeedModel.TireCondition getTireCondition();

    int getTemperature();
}
