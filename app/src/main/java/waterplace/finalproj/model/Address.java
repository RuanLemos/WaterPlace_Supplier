package waterplace.finalproj.model;

public class Address {
    private String avenue;
    private int num;
    private double latitude;
    private double longitude;

    private int CEP;

    public int getCEP() { return CEP; }

    public void setCEP(int CEP) { this.CEP = CEP; }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Address() {
    }

    public String getAvenue() {
        return avenue;
    }

    public void setAvenue(String avenue) {
        this.avenue = avenue;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}