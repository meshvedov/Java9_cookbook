package main.java.ch2.using_interface;

public class FactoryVehicle {

    public static Car buildCar(int i, int vehicleWeight, int horsePower) {
        return new CarImpl(i, vehicleWeight, horsePower);
    }

    public static Truck buildTruck(int payloadPounds, int weightPounds, int horsePower) {
        return new TruckImpl(payloadPounds, weightPounds, horsePower);
    }

    public static Vehicle buildCrewCab(int passengerCount, int payload, int weightPounds, int horsePower) {
        return new CrewCabImpl(passengerCount, payload, weightPounds, horsePower);
    }

    private static class CarImpl extends VehicleImpl implements Car {
        private int passengerCount;

        public CarImpl(int passengerCount, int vehicleWeight, int horsePower) {
            super(vehicleWeight + passengerCount * 250, horsePower);
            this.passengerCount = passengerCount;
        }

        public int getPassengersCount() {
            return passengerCount;
        }
    }

    private static class TruckImpl extends VehicleImpl implements Truck {
        private int payloadPounds;

        public TruckImpl(int payloadPounds, int weightPounds, int horsePower) {
            super(weightPounds + payloadPounds, horsePower);
            this.payloadPounds = payloadPounds;
        }

        @Override
        public int getPayloadPounds() {
            return payloadPounds;
        }
    }

    private static abstract class VehicleImpl implements Vehicle {
        private SpeedModel speedModel;
        private int weightPounds, horsePower;

        public VehicleImpl(int weightPounds, int horsePower) {
            this.weightPounds = weightPounds;
            this.horsePower = horsePower;
        }

        @Override
        public void setSpeedModel(SpeedModel speedModel) {
            this.speedModel = speedModel;
        }

        @Override
        public double getSpeedMph(double timeSec) {
            return this.speedModel.getSpeedMph(timeSec, weightPounds, horsePower);
        }
    }

    private static class CrewCabImpl extends VehicleImpl implements Car, Truck {
        private int payloadPounds;
        private int passengersCount;

        public CrewCabImpl(int passengerCount, int payload, int weightPounds, int horsePower) {
            super(weightPounds + passengerCount * 250 + payload, horsePower);
            this.payloadPounds = payload;
            this.passengersCount = passengerCount;
        }

        @Override
        public int getPayloadPounds() {
            return payloadPounds;
        }

        public int getPassengersCount() {
            return passengersCount;
        }
    }
}
