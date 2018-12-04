package com.example.michal.inz.OBDConnection;

public class FuelLevelCommand extends OBDCommand {
    private float fuelLevel = 0.0f;

    public FuelLevelCommand() {
        super("01 2F");
    }

    public FuelLevelCommand(FuelLevelCommand other) {
        super(other);
    }

    @Override
    protected void calculate() {
        // ignore first two bytes [hh hh] of the response
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
