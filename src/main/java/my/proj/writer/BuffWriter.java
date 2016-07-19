package my.proj.writer;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class BuffWriter extends Writer {

    private CharArrayWriter buf = new CharArrayWriter();

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        buf.write(cbuf, off, len);
    }

    @Override
    public void flush() throws IOException {
        buf.flush();
    }

    @Override
    public void close() throws IOException {
        System.out.println("CLOSE BUF");
        buf.close();
    }

    public void writeTo(Writer out) throws IOException {
        buf.writeTo(out);
        buf.reset();
    }

    public CharArrayWriter getBuff() {
        return buf;
    }
}
