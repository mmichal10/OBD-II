package com.example.michal.inz.OBDConnection;

public class FuelLevelCommand extends PercentageOBDCommand {

    public FuelLevelCommand() {
        super("01 2F");
    }

    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        percentage = 100.0f * buffer.get(2) / 255.0f;
    }

    @Override
    public String getName() {
        return "Fuel Level";
    }

    public float getFuelLevel() {
        return percentage;
    }

}
