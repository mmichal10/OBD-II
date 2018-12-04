package com.example.michal.inz.OBDConnection;

public class ResetCommand extends OBDCommand {
    public ResetCommand() {
        super("AT Z");
    }

    public ResetCommand(ResetCommand other) {
        super(other);
    }

    @Override
    public String getName() {
        return "Reset OBD";
    }

    public void calculate() {
        // ignore
    }

    public void decodeRawData() {
        // settings commands don't return a value appropriate to place into the buffer, so do nothing
    }

}
