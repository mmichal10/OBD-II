package com.example.michal.inz.OBDConnection;

public class RPMCommand extends OBDCommand {
    private int rpm = -1;

    public RPMCommand() {
        super("01 0C");
    }

    public RPMCommand(RPMCommand other) {
        super(other);
    }

    @Override
    protected void calculate() {
        // ignore first two bytes [41 0C] of the response((A*256)+B)/4
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