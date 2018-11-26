package com.example.michal.inz.networking;


import android.util.JsonToken;
import android.util.JsonWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class Stats {
    private String vin;
    private float temperature;
    private float speed;
    private float voltage;
    private float fuelUsage;
    private int engineSpeed;

    public Stats(String vin, float temperature, float speed, float voltage,
                 float fuelUsage, int engineSpeed) {
        this.vin = vin;
        this.temperature = temperature;
        this.speed = speed;
        this.voltage = voltage;
        this.fuelUsage = fuelUsage;
        this.engineSpeed = engineSpeed;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "vin='" + vin + "'" +
                ", temperature=" + temperature +
                ", speed=" + speed +
                ", voltage=" + voltage +
                ", fuelUsage=" + fuelUsage +
                ", engineSpeed=" + engineSpeed +
                '}';
    }

    public String toJson() {
        JSONObject object = new JSONObject();
        try {
            object.put("vin", vin);
            object.put("temperature" , temperature);
            object.put("speed" , speed);
            object.put("voltage" , voltage);
            object.put("fuelUsage" , fuelUsage);
            object.put("engineSpeed" , engineSpeed);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString().replace("\\", "");
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getVoltage() {
        return voltage;
    }

    public void setVoltage(float voltage) {
        this.voltage = voltage;
    }

    public float getFuelUage() {
        return fuelUsage;
    }

    public void setFuelUage(float fuelUsage) {
        this.fuelUsage = fuelUsage;
    }

    public int getEngineSpeed() {
        return engineSpeed;
    }

    public void setEngineSpeed(int engineSpeed) {
        this.engineSpeed = engineSpeed;
    }
}
