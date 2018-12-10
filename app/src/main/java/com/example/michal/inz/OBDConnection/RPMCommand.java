package com.example.michal.inz.OBDConnection;

public class RPMCommand extends OBDCommand {
    private int rpm = -1;

    public RPMCommand() {
        super("01 0C");
    }

    @Override
    protected void calculate() {
        rpm = (buffer.get(2) * 256 + buffer.get(3)) / 4;
    }

    @Override
    public String getName() {
        return "Engine RPM";
    }

    public int getRPM() {
        return rpm;
    }

}