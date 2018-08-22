package ch7_concurrency.api;

import java.time.DayOfWeek;
import java.time.Month;

public class DateLocation {
    private Month month;
    private DayOfWeek dayOfWeek;
    private int hour;
    private String country, city, trafficLight;

    public DateLocation(Month month, DayOfWeek dayOfWeek, int hour, String country, String city, String trafficLight) {
        this.month = month;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.country = country;
        this.city = city;
        this.trafficLight = trafficLight;
    }

    public Month getMonth() {
        return month;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public int getHour() {
        return hour;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public String getTrafficLight() {
        return trafficLight;
    }
}
