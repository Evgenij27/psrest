package my.proj.writer;

import java.io.*;
import java.util.Arrays;

public class CustomWriter extends Writer {

    private PrintWriter out;

    public CustomWriter(PrintWriter out) {
        this.out = out;
    }

    private void isDisconnected() throws IOException {
        System.out.println("Is disconnected start of method");
        if (out.checkError()) {
            System.out.println("DISCONNECTED");
            throw new IOException("Client disconnected");
        }
        return ;
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        System.out.println("WANT TO WRITE --> " + new String(cbuf));
        //isDisconnected();
        System.out.println("WRITE TO CLIENT --> " + new String(cbuf));
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
