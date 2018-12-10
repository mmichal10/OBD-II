package com.example.michal.inz.OBDConnection;

public class ResetCommand extends OBDCommand {
    public ResetCommand() {
        super("AT Z");
    }

    @Override
    public String getName() {
        return "Reset OBD";
    }

    public void calculate() {

    }

    public void decodeRawData() {

    }

}
