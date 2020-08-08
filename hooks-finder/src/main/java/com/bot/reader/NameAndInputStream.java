package com.bot.reader;


import java.io.IOException;
import java.io.InputStream;

public class NameAndInputStream {

    private final String fileName;

    private final InputStream inputStream;

    public NameAndInputStream(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public String toString() {
        return "NameAndInputStream{" +
                "fileName='" + fileName + '\'' +
                ", inputStream=" + inputStream +
                '}';
    }
}
