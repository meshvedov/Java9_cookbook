package ch2.using_interface;

public interface SpeedModel {
    double getSpeedMph(double timeSec, int weightPounds, int horsePower);

    enum RoadCondition {
        DRY(1.0), WET(0.2), SNOW(0.04);
        private double traction;
        RoadCondition(double traction) {
            this.traction = traction;
        }
        public double getTraction() {
            return traction;
        }
    }

    enum TireCondition {
        NEW(1.0), WORN(0.2);
        private double traction;
        TireCondition(double traction) {
            this.traction = traction;
        }
        public double getTraction() {
            return traction;
        }
    }

    enum DrivingCondition {
        ROAD_CONDITION, TIRE_CONDITION
    }

}
