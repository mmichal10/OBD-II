package com.example.michal.inz.OBDConnection;

public class ConsumptionRateCommand extends OBDCommand {
    private float fuelConsumption = 0.0f;

    public ConsumptionRateCommand() {
        super("01 5E");
    }

    public ConsumptionRateCommand(ConsumptionRateCommand other) {
        super(other);
    }

    @Override
    protected void calculate() {
        // ignore first two bytes [hh hh] of the response
        fuelConsumption = (buffer.get(2) * 256 + buffer.get(3)) * 0.05f;
    }

    public float getLitersPerHour() {
        return fuelConsumption;
    }

    @Override
    public String getName() {
        return "Fuel Consumption Rate";
    }

}