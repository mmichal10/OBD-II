package com.example.michal.inz.OBDConnection;

public class EngineCoolantTemperatureCommand extends OBDCommand {
    private float temperature = 0.0f;

    public EngineCoolantTemperatureCommand() {
        super("01 05");
    }

    public EngineCoolantTemperatureCommand(EngineCoolantTemperatureCommand other) {
        super(other);
    }

    @Override
    protected void calculate() {
        // ignore first two bytes [hh hh] of the response
        temperature = buffer.get(2) - 40;
    }

    @Override
    public String getName() {
        return "Engine Coolant Temperature";
    }

    public float getTemperature() { return  temperature; }
}
