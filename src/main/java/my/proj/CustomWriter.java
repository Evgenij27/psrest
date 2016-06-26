package my.proj;

import java.io.*;

public class CustomWriter extends Writer {

    private PrintWriter out;
    private int count;

    private void isDisconnected() throws IOException {
        if (out.checkError()) {
            System.out.println("DISCONNECTED count = " + count++);
            throw new IOException("Client disconnected");
        }
    }

    public void setWriter(PrintWriter out) {
        this.out = out;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        isDisconnected();
        System.out.println("WRITE TO CLIENT");
        out.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
