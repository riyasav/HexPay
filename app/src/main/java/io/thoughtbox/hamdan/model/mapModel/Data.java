package io.thoughtbox.hamdan.model.mapModel;

public class Data {
    private String name;
    private double lat;
    private double lon;
    private String address;
    private String contact;

    public Data(String name, double lat, double lon, String address, String contact) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}