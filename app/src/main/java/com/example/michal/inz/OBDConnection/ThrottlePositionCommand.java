package com.example.michal.inz.OBDConnection;

public class ThrottlePositionCommand extends OBDCommand {
    private float throttlePosition = 0.0f;

    public ThrottlePositionCommand() {
        super("01 11");
    }

    @Override
    protected void calculate() {
        throttlePosition = 100.0f * buffer.get(2) / 255.0f;
    }

    @Override
    public String getName() {
        return "Throttle Position";
    }

    public float getThrottlePosition() {
        return throttlePosition;
    }

}
