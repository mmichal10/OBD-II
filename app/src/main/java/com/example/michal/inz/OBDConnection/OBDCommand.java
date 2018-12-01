package com.example.michal.inz.OBDConnection; 

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import com.example.michal.inz.OBDConnection.Exceptions.*;


public abstract class OBDCommand {

    private final Class[] ERROR_CLASSES = {
            UnableToConnectException.class,
            BusInitException.class,
            MisunderstoodCommandException.class,
            NoDataException.class,
            StoppedException.class,
            UnknownErrorException.class,
            UnsupportedCommandException.class
    };
    protected ArrayList<Integer> buffer = null;
    protected String cmd = null;
    protected String rawData = null;
    protected Long responseDelayInMs = null;
    private long start;
    private long end;


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
            start = System.currentTimeMillis();
            sendCommand(out);
            readResult(in);
            end = System.currentTimeMillis();
        }
    }

    protected void sendCommand(OutputStream out) throws IOException, InterruptedException {
        // write to OutputStream (i.e.: a BluetoothSocket) with an added
        // Carriage return
        out.write((cmd + "\r").getBytes());
        out.flush();
        if (responseDelayInMs != null && responseDelayInMs > 0) {
            Thread.sleep(responseDelayInMs);
        }
    }

    protected void resendCommand(OutputStream out) throws IOException, InterruptedException {
        out.write("\r".getBytes());
        out.flush();
        if (responseDelayInMs != null && responseDelayInMs > 0) {
            Thread.sleep(responseDelayInMs);
        }
    }

    protected void readResult(InputStream in) throws IOException {
        readRawData(in);
        checkForErrors();
        fillBuffer();
        performCalculations();
    }

    protected abstract void performCalculations();

    protected void fillBuffer() {
        rawData = rawData.replaceAll("\\s", ""); //removes all [ \t\n\x0B\f\r]
        rawData = rawData.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");

        if (!rawData.matches("([0-9A-F])+")) {
            throw new NonNumericResponseException(rawData);
        }

        // read string each two chars
        buffer.clear();
        int begin = 0;
        int end = 2;
        while (end <= rawData.length()) {
            buffer.add(Integer.decode("0x" + rawData.substring(begin, end)));
            begin = end;
            end += 2;
        }
    }

    protected void readRawData(InputStream in) throws IOException {
        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives OR end of stream reached
        char c;
        // -1 if the end of the stream is reached
        while (((b = (byte) in.read()) > -1)) {
            c = (char) b;
            if (c == '>') // read until '>' arrives
            {
                break;
            }
            res.append(c);
        }

        /*
         * Imagine the following response 41 0c 00 0d.
         *
         * ELM sends strings!! So, ELM puts spaces between each "byte". And pay
         * attention to the fact that I've put the word byte in quotes, because 41
         * is actually TWO bytes (two chars) in the socket. So, we must do some more
         * processing..
         */
        rawData = res.toString().replaceAll("SEARCHING", "");

        /*
         * Data may have echo or informative text like "INIT BUS..." or similar.
         * The response ends with two carriage return characters. So we need to take
         * everything from the last carriage return before those two (trimmed above).
         */
        rawData = rawData.replaceAll("\\s", "");//removes all [ \t\n\x0B\f\r]
    }

    void checkForErrors() {
        for (Class<? extends ResponseException> errorClass : ERROR_CLASSES) {
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

    public Long getResponseTimeDelay() {
        return responseDelayInMs;
    }

    public void setResponseTimeDelay(Long responseDelayInMs) {
        this.responseDelayInMs = responseDelayInMs;
    }

    public long getStart() {
        return this.start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return this.end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public final String getCommandPID() {
        return cmd.substring(3);
    }

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

