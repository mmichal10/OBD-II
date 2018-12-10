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
            WrongCommandException.class,
            NoDataException.class,
            StoppedException.class,
            ErrorException.class,
    };

    public OBDCommand(String command) {
        this.buffer = new ArrayList<Integer>();
        this.cmd = command;
        this.rawData = "";
    }

    public void run(InputStream in, OutputStream out) throws IOException {
        synchronized (OBDCommand.class) {
            sendCommand(out);
            readResult(in);
        }
    }

    protected void sendCommand(OutputStream out) throws IOException {
        out.write((cmd.concat("\r")).getBytes());
        out.flush();
    }

    protected void readResult(InputStream in) throws IOException {
        readRawData(in);
        validateResponse();
        decodeRawData();
        calculate();
    }

    protected void readRawData(InputStream in) throws IOException {
        byte singleByte = 0;

        rawData = "";
        char c;
        while (((singleByte = (byte)in.read()) != -1)) {
            c = (char) singleByte;
            if (c == '>')
                break;

            rawData += c;
        }

        rawData = rawData.replaceAll("\\s", "")
                .replaceAll("CANInitial-FAIL", "");
    }

    protected void decodeRawData() {
        buffer.clear();
        rawData = rawData.replaceAll("\\.", "")
                .replaceAll("\\s", "")
                .replaceAll("BUSINIT", "");

        int begin = 0, end = 2;
        while (end <= rawData.length()) {
            buffer.add(Integer.decode("0x" + rawData.substring(begin, end)));
            begin = end;
            end += 2;
        }
    }

    void validateResponse() {
        BaseException error;
        for (Class<? extends BaseException> errorClass : NOTICED_ERRORS) {

            try {
                error = errorClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            if (error.isError(rawData)) {
                throw error;
            }
        }
    }

    protected abstract void calculate();

    public abstract String getName();

    public String getResult() {
        return rawData;
    }

    protected ArrayList<Integer> getBuffer() {
        return buffer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return cmd == null ? ((OBDCommand)o).cmd == null : cmd.equals(((OBDCommand)o).cmd);
    }

    @Override
    public int hashCode() {
        return cmd == null ? 0 : cmd.hashCode();
    }

}

