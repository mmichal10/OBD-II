package com.example.michal.inz.OBDConnection;

public class SpeedCommand extends OBDCommand {
    private int metricSpeed = 0;

    public SpeedCommand() {
        super("01 0D");
    }

    @Override
    protected void calculate() {
        metricSpeed = buffer.get(2);
    }

    public int getMetricSpeed() {
        return metricSpeed;
    }

    @Override
    public String getName() {
        return "Vehicle Speed";
    }

}
