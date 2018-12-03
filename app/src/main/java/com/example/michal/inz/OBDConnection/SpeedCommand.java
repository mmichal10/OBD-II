package com.example.michal.inz.OBDConnection;

public class SpeedCommand extends OBDCommand {
    private int metricSpeed = 0;

    public SpeedCommand() {
        super("01 0D");
    }

    public SpeedCommand(SpeedCommand other) {
        super(other);
    }

    @Override
    protected void calculate() {
        // Ignore first two bytes [hh hh] of the response.
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
