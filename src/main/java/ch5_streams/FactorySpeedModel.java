package main.java.ch5_streams;

import main.java.ch5_streams.api.SpeedModel;
import main.java.ch5_streams.api.TrafficUnit;

public class FactorySpeedModel {
    public static SpeedModel generateSpeedModel(TrafficUnit trafficUnit) {
        return new SpeedModelImpl(trafficUnit);
    }

    public static SpeedModel getSpeedModel() {
        return SpeedModelImpl.getSpeedModel();
    }

    private static class SpeedModelImpl implements SpeedModel {
        private TrafficUnit trafficUnit;

        public SpeedModelImpl(TrafficUnit trafficUnit) {
            this.trafficUnit = trafficUnit;
        }

        public static SpeedModel getSpeedModel() {
            return (t, wp, hp) -> {
                double weightPower = 2.0 * hp * 746 * 32.174 / wp;
                return Math.round(Math.sqrt(t * weightPower) * 0.68);
            };
        }

        @Override
        public double getSpeedMph(double timeSec, int weightPounds, int horsePower) {
            double speed = getSpeedModel().getSpeedMph(timeSec, weightPounds, horsePower);
            return Math.round(speed * trafficUnit.getTraction());
        }
    }
}
