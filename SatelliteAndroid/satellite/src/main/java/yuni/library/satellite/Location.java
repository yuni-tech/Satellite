package yuni.library.satellite;

import java.util.Objects;

public class Location {

    private double lat;
    private double lng;
    private String cityCode;
    private String city;
    private String province;
    private String country;
    private String streat;
    private String address;
    private float accuracy;
    private long time;

    public Location() {
    }

    public Location(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStreat() {
        return streat;
    }

    public void setStreat(String streat) {
        this.streat = streat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return Double.compare(location.lat, lat) == 0 &&
                Double.compare(location.lng, lng) == 0 &&
                Float.compare(location.accuracy, accuracy) == 0 &&
                time == location.time &&
                Objects.equals(cityCode, location.cityCode) &&
                Objects.equals(city, location.city) &&
                Objects.equals(province, location.province) &&
                Objects.equals(country, location.country) &&
                Objects.equals(streat, location.streat) &&
                Objects.equals(address, location.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lng, cityCode, city, province, country, streat, address, accuracy, time);
    }

    public Location copy() {
        Location location = new Location();
        location.lat = this.lat;
        location.lng = this.lng;
        location.cityCode = this.cityCode;
        location.city = this.city;
        location.province = this.province;
        location.country = this.country;
        location.streat = this.streat;
        location.address = this.address;
        location.accuracy = this.accuracy;
        location.time = this.time;
        return location;
    }
}
