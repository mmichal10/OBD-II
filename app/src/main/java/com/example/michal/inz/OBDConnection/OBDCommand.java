package com.example.michal.inz.OBDConnection; 

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.example.michal.inz.OBDConnection.Exceptions.*;


public abstract class OBDCommand {
    protected ArrayList<Integer> buffer = null;
    protected String cmd = null;
    protected String rawData = null;
    private final Class[] NOTICED_ERRORS = {
            UnableToConnectException.class,
            BusInitException.class,
            MisunderstoodCommandException.class,
            NoDataException.class,
            StoppedException.class,
            ErrorException.class,
    };

    public OBDCommand(String command) {
        this.cmd = command;
        this.buffer = new ArrayList<>();
    }

    private OBDCommand() {
    }

    public OBDCommand(OBDCommand other) {
        this(other.cmd);
    }

    public void run(InputStream in, OutputStream out) throws IOException, InterruptedException {
        synchronized (OBDCommand.class) {//Only one command can write and read a data in one time.
            sendCommand(out);
            readResult(in);
        }
    }

    protected void sendCommand(OutputStream out) throws IOException {
        // write to OutputStream (i.e.: a BluetoothSocket) with an added
        // Carriage return
        out.write((cmd.concat("\r")).getBytes());
        out.flush();
    }

    protected void resendCommand(OutputStream out) throws IOException {
        out.write("\r".getBytes());
        out.flush();
    }

    protected void readResult(InputStream in) throws IOException {
        readRawData(in);
        checkForErrors();
        decodeRawData();
        calculate();
    }

    protected abstract void calculate();

    protected void decodeRawData() {
        rawData = rawData.replaceAll("\\s", ""); //removes all [ \t\n\x0B\f\r]
        rawData = rawData.replaceAll("\\.", "");
        rawData = rawData.replaceAll("BUSINIT", "");

        if (!rawData.matches("([0-9A-F])+")) {
            throw new NonNumericResponseException(rawData);
        }

        // read string each two chars
        buffer.clear();
        int begin = 0, end = 2;
        while (end <= rawData.length()) {
            buffer.add(Integer.decode("0x" + rawData.substring(begin, end)));
            begin = end;
            end += 2;
        }
    }

    protected void readRawData(InputStream in) throws IOException {
        StringBuilder response = new StringBuilder();
        byte singleByte = 0;
        char c;

        // read until '>' arrives OR end of stream reached
        // -1 if the end of the stream is reached
        while (((singleByte = (byte)in.read()) != -1)) {
            c = (char) singleByte;
            if (c == '>') // read until '>' arrives
                break;

            response.append(c);
        }

        rawData = response.toString().replaceAll("SEARCHING", "");
        rawData = rawData.replaceAll("\\s", "");//removes all [ \t\n\x0B\f\r]
        rawData = rawData.replaceAll("CANInitial-FAIL", "");
    }

    void checkForErrors() {
        for (Class<? extends ResponseException> errorClass : NOTICED_ERRORS) {
            ResponseException messageError;

            try {
                messageError = errorClass.newInstance();
                messageError.setCommand(this.cmd);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (messageError.isError(rawData)) {
                throw messageError;
            }
        }
    }

    public String getResult() {
        return rawData;
    }

    protected ArrayList<Integer> getBuffer() {
        return buffer;
    }

    public abstract String getName();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OBDCommand that = (OBDCommand) o;

        return cmd != null ? cmd.equals(that.cmd) : that.cmd == null;
    }

    @Override
    public int hashCode() {
        return cmd != null ? cmd.hashCode() : 0;
    }

}

