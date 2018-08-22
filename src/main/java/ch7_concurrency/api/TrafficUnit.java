package ch7_concurrency.api;

public interface TrafficUnit {
    Vehicle.VehicleType getVehicleType();
    int getHorsePower();
    int getWeightPounds();
    int getPayloadPounds();
    int getPassengersCount();
    double getSpeedLimitMph();
    double getTraction();
    SpeedModel.RoadCondition getRoadCondition();
    SpeedModel.TireCondition getTireCondition();
    int getTemperature();
}