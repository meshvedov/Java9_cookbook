package main.java.ch5_streams;

import main.java.ch5_streams.api.*;

public class FactoryVehicle {
    public static Vehicle build(TrafficUnit trafficUnit) {
        switch (trafficUnit.getVehicleType()) {
            case CAR:
                return new CarImpl(trafficUnit.getPassengersCount(), trafficUnit.getWeightPounds(), trafficUnit.getHorsePower());
            case TRUCK:
                return new TruckImpl(trafficUnit.getPayloadsPounds(), trafficUnit.getWeightPounds(), trafficUnit.getHorsePower());
            case CAB_CREW:
                return new CrewCabImpl(trafficUnit.getPassengersCount(), trafficUnit.getPayloadsPounds(), trafficUnit.getWeightPounds(), trafficUnit.getHorsePower());
            default:
                System.out.println("Unexpected vehicle type " + trafficUnit.getVehicleType());
                return new CrewCabImpl(trafficUnit.getPassengersCount(), trafficUnit.getPayloadsPounds(), trafficUnit.getWeightPounds(), trafficUnit.getHorsePower());
        }

    }

    private static class CarImpl extends VehicleImpl implements Car {
        private int passengersCount;

        public CarImpl(int passengersCount, int weightPounds, int horsePower) {
            super(weightPounds * passengersCount, horsePower);
            this.passengersCount = passengersCount;
        }

        @Override
        public int getPassengersCount() {
            return passengersCount;
        }
    }

    private static class TruckImpl extends VehicleImpl implements Truck {
        private int payloadPounds;

        public TruckImpl(int payloadPounds, int weightPounds, int horsePounds) {
            super(weightPounds + payloadPounds, horsePounds);
            this.payloadPounds = payloadPounds;
        }

        @Override
        public int getPayloadPounds() {
            return payloadPounds;
        }
    }

    private static class CrewCabImpl extends VehicleImpl implements Car, Truck {
        private int payloadPounds;
        private int passengersCount;

        public CrewCabImpl(int passengersCount, int payloadPounds, int weightPounds, int horsePounds) {
            super(weightPounds + payloadPounds + passengersCount * 250, horsePounds);
            this.payloadPounds = payloadPounds;
            this.passengersCount = passengersCount;
        }

        @Override
        public int getPassengersCount() {
            return passengersCount;
        }

        @Override
        public int getPayloadPounds() {
            return payloadPounds;
        }
    }


    private static abstract class VehicleImpl implements Vehicle {
        private SpeedModel speedModel;
        private int weightPounds, horsePower;

        public VehicleImpl(int weightPounds, int horsePounds) {
            this.weightPounds = weightPounds;
            this.horsePower = horsePounds;
        }

        @Override
        public void setSpeedModel(SpeedModel speedModel) {
            this.speedModel = speedModel;
        }

        @Override
        public double getSpeedMph(double timeSec) {
            return this.speedModel.getSpeedMph(timeSec, weightPounds, horsePower);
        }

        @Override
        public int getWeightPounds() {
            return weightPounds;
        }

        @Override
        public int getHorsePower() {
            return horsePower;
        }
    }

}
