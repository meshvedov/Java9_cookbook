package ch2.using_interface;

public interface Truck extends Vehicle {

    int getPayloadPounds();

//    default int getPayloadKg() {
//        return (int) Math.round(0.454 * getPayloadPounds());
//    }

    default int getPayloadKg(int pounds) {
        return convertPoundsToKg(pounds);
    }

    static int convertKgToPounds(int kg) {
        return (int) Math.round(2.205 * kg);
    }

    default int getWeightKg(int pounds) {
        return convertPoundsToKg(pounds);
    }

    static int convertPoundsToKg(int pounds) {
        return (int) Math.round(0.454 * pounds);
    }

}
