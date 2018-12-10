package com.example.michal.inz.OBDConnection;

public class FuelLevelCommand extends OBDCommand {
    private float fuelLevel = 0.0f;

    public FuelLevelCommand() {
        super("01 2F");
    }

    @Override
    protected void calculate() {
        fuelLevel = 100.0f * buffer.get(2) / 255.0f;
    }

    @Override
    public String getName() {
        return "Fuel Level";
    }

    public float getFuelLevel() {
        return fuelLevel;
    }

}
