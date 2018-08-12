package api;

import api.Vehicle.VehicleType;

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
