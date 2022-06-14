package com.example.iotapplication.Model;

public class History {
    private String time ;
    private String values ;

    public History() {
    }

    public History(String time, String values) {
        this.time = time;
        this.values = values;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }
}
