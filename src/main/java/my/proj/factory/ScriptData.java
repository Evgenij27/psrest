package my.proj.factory;

import javax.script.ScriptContext;
import java.io.Writer;
import java.util.concurrent.Future;

public class ScriptData {

    private Writer writer;
    private Writer errorWriter;
    private Runnable runnable;
    private Future<?> future;

    public ScriptData(ScriptContext sc) {
        writer = sc.getWriter();
        errorWriter = sc.getErrorWriter();
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public Writer getErrorWriter() {
        return errorWriter;
    }

    public void setErrorWriter(Writer errorWriter) {
        this.errorWriter = errorWriter;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }
}
