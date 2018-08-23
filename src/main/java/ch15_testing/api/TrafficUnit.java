package ch15_testing.api;

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
    default double getSpeed(){ return 0.0; }
}
