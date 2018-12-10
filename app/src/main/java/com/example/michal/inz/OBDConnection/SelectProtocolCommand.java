package com.example.michal.inz.OBDConnection;


public class SelectProtocolCommand extends OBDCommand {

    public SelectProtocolCommand(String protocol) {
        super("AT SP " + protocol);
    }

    @Override
    public String getName() {
        return "Select Protocol";
    }

    public void calculate() {
        // ignore
    }

    public void decodeRawData() {
        // settings commands don't return a value appropriate to place into the buffer, so do nothing
    }
}

