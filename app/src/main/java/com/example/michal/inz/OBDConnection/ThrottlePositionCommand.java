package com.example.michal.inz.OBDConnection;

public class ThrottlePositionCommand extends PercentageOBDCommand {

    public ThrottlePositionCommand() {
        super("01 11");
    }

    public ThrottlePositionCommand(ThrottlePositionCommand other) {
        super(other);
    }

    @Override
    public String getName() {
        return "Throttle Position";
    }

}
