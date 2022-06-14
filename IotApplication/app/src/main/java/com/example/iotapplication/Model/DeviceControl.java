package com.example.iotapplication.Model;

public class DeviceControl {
    private int image ;
    private String nameDevice ;
    private int statusDevice ;

    public DeviceControl() {
    }

    public DeviceControl(int image, String nameDevice, int statusDevice) {
        this.image = image;
        this.nameDevice = nameDevice;
        this.statusDevice = statusDevice;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public int getStatusDevice() {
        return statusDevice;
    }

    public void setStatusDevice(int statusDevice) {
        this.statusDevice = statusDevice;
    }

    @Override
    public String toString() {
        return statusDevice + " " + nameDevice;
    }
}
