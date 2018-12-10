package com.example.michal.inz.OBDConnection;

public class ModuleVoltageCommand extends OBDCommand {

    private double voltage = 0.00;

    public ModuleVoltageCommand() {
        super("01 42");
    }

    @Override
    protected void calculate() {
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