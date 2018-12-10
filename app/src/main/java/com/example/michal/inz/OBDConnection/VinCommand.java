package com.example.michal.inz.OBDConnection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VinCommand extends OBDCommand {
    private String vin = "";

    public VinCommand() {
        super("09 02");
    }

    public VinCommand(VinCommand other) {
        super(other);
    }

    @Override
    protected void calculate() {
        final String result = getResult();
        String workingData;
        if (result.contains(":")) {//CAN(ISO-15765) protocol.
            workingData = result.replaceAll(".:", "").substring(9);//9 is xxx490201, xxx is bytes of information to follow.
            Matcher m = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE).matcher(convertHexToString(workingData));
            if(m.find()) workingData = result.replaceAll("0:49", "").replaceAll(".:", "");
        } else {//ISO9141-2, KWP2000 Fast and KWP2000 5Kbps (ISO15031) protocols.
            workingData = result.replaceAll("49020.", "");
        }
        vin = convertHexToString(workingData).replaceAll("[\u0000-\u001f]", "");
    }

    @Override
    public String getName() {
        return "Vehicle Identification Number";
    }

    @Override
    protected void decodeRawData() {
    }

    public String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);
        }
        return sb.toString();
    }

    public String getVin() {
        return this.vin;
    }
}