package my.proj.factory;

import javax.script.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;


public class RunnableFactory {

    private static final String ENGINE_NAME = "nashorn";

    public static final RunnableFactory INSTANCE = new RunnableFactory();

    private RunnableFactory() {}

    public Runnable getRunnable(ScriptData sd, HttpServletRequest request) throws ScriptException {


        ScriptEngine engine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);
        String code = prepareForRunnable(request);

        engine.getContext().setWriter(sd.getWriter());
        engine.getContext().setErrorWriter(sd.getErrorWriter());

        Compilable compilableEngine = (Compilable) engine;
        CompiledScript compiledScript = compilableEngine.compile(code);

        compiledScript.eval();

        Invocable inv = (Invocable) compiledScript.getEngine();

        return inv.getInterface(Runnable.class);
    }

    private String getCode(HttpServletRequest req) {
        BufferedReader br = null;
        StringBuffer sb = new StringBuffer();
        String data = null;
        try {
            br = req.getReader();
            while ((data = br.readLine()) != null) {
                sb.append(data);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

    private String prepareForRunnable(HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        sb.append("function run() {");
        sb.append(getCode(request));
        sb.append("};");
        return sb.toString();
    }
}
