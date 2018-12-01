package com.example.michal.inz.OBDConnection;

public class ModuleVoltageCommand extends OBDCommand {

    private double voltage = 0.00;

    public ModuleVoltageCommand() {
        super("01 42");
    }

    public ModuleVoltageCommand(ModuleVoltageCommand other) {
        super(other);
    }

    @Override
    protected void performCalculations() {
        // ignore first two bytes [hh hh] of the response
        int a = buffer.get(2);
        int b = buffer.get(3);
        voltage = (a * 256 + b) / 1000;
    }

    public double getVoltage() {
        return voltage;
    }

    @Override
    public String getName() {
        return "Control Module Power Supply";
    }

}