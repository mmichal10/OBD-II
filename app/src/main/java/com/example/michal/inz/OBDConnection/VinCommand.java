package com.example.michal.inz.OBDConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VinCommand extends OBDCommand {
    private String vin = "";

    public VinCommand() {
        super("09 02");
    }

    @Override
    protected void calculate() {
        final String result = getResult();
        // NOT WORKING
    }

    @Override
    public String getName() {
        return "Vehicle Identification Number";
    }

    @Override
    protected void decodeRawData() {
    }

    public String getVin() {
        return this.vin;
    }
}