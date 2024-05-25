package com.example.bottomnavi;

public class TouristPlaceDataHolder {
    private static TouristPlaceDataHolder instance;
    private TouristPlace place;

    private TouristPlaceDataHolder() {}

    public static TouristPlaceDataHolder getInstance() {
        if (instance == null) {
            instance = new TouristPlaceDataHolder();
        }
        return instance;
    }

    public TouristPlace getPlace() {
        return place;
    }

    public void setPlace(TouristPlace place) {
        this.place = place;
    }
}
