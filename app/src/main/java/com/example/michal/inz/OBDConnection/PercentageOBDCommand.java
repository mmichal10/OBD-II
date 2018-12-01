package com.example.michal.inz.OBDConnection;

public abstract class PercentageOBDCommand extends OBDCommand {
    protected float percentage = 0f;

    public PercentageOBDCommand(String command) {
        super(command);
    }

    public PercentageOBDCommand(PercentageOBDCommand other) {
        super(other);
    }

    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        percentage = (buffer.get(2) * 100.0f) / 255.0f;
    }

    public float getPercentage() {
        return percentage;
    }

}
