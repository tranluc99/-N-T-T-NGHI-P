package com.example.iotapplication.Model;

public class Device {
    private int image ;
    private String unit ;
    private String nameDevice ;
    private String values;
    private int threshold;
    private int statusDevices;
    private String time ;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getStatusDevices() {
        return statusDevices;
    }

    public void setStatusDevices(int statusDevices) {
        this.statusDevices = statusDevices;
    }

    public Device() {
    }

    public int getThreshold() {
        return threshold;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public Device(int image, String nameDevice, String values, String unit) {
        this.image = image;
        this.unit = unit;
        this.nameDevice = nameDevice;
        this.values = values;
    }

    public Device(String nameDevice, String values) {
        this.nameDevice = nameDevice;
        this.values = values;
    }

    public String getNameDevice() {
        return nameDevice;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setNameDevice(String nameDevice) {
        this.nameDevice = nameDevice;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return image+" " + nameDevice+" "+ values;
    }
}
