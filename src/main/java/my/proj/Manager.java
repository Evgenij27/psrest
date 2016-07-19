package my.proj;

import my.proj.factory.RunnableFactory;
import my.proj.factory.ScriptData;
import my.proj.writer.BuffWriter;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Manager {

    private static ConcurrentSkipListMap<Integer, ScriptData> scripts =
            new ConcurrentSkipListMap<>();


    private static AtomicInteger tid = new AtomicInteger(0);

    private static final ExecutorService POOL = Executors.newFixedThreadPool(10);

    private static RunnableFactory rsf = RunnableFactory.INSTANCE;

    void get(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try (final PrintWriter writer = response.getWriter()) {
            System.out.println("GET");
            System.out.print("Request URI ==> " + request.getRequestURI() + "\n");
            System.out.print("Request URL ==> "  + request.getRequestURL() + "\n");
            System.out.print("Context path ==> " + request.getContextPath());

            parseURI(request, writer);
        }
    }

    void post(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        BuffWriter buff = new BuffWriter();
        ScriptContext context = new SimpleScriptContext();
        context.setErrorWriter(buff);
        context.setWriter(buff);
        ScriptData scriptData = new ScriptData(context);

        response.setContentType("text/plain");

        try {
            Runnable r = rsf.getRunnable(scriptData, request);

            scriptData.setRunnable(r);
            scriptData.setFuture(POOL.submit(r));

            scripts.put(tid.incrementAndGet(), scriptData);

            response.setStatus(HttpServletResponse.SC_ACCEPTED);

            StringBuffer requestURL = request.getRequestURL();
            requestURL.append("/").append(tid.get());
            response.setHeader("Location", requestURL.toString());

        } catch (ScriptException ex) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            scriptData.getErrorWriter().append(ex.getMessage());
        }
    }

    private void get(PrintWriter w) {
        w.println("LIST");
        w.print(scripts.toString());
    }

    private void get(int tid, PrintWriter w) throws IOException {
        ScriptData sd = scripts.get(tid);
        BuffWriter writer = (BuffWriter) sd.getWriter();
        writer.writeTo(w);
    }

    private void get(int tid, String action, PrintWriter w) throws IOException {
        ScriptData sd = scripts.get(tid);
        if (action.equals("errors")) {
            BuffWriter writer = (BuffWriter) sd.getErrorWriter();
            writer.writeTo(w);
        } else {
             w.print(sd.getFuture().isDone());
        }
    }

    private void parseURI(HttpServletRequest request, PrintWriter w) throws IOException {

        String result = null;

        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();

        Pattern listPattern = Pattern.compile(
                new StringBuffer().append(contextPath).append("/t").toString());

        Pattern itemPattern = Pattern.compile(
                new StringBuffer().append(contextPath).
                        append("/t").append("/(?<tid>[0-9])").toString());

        Pattern actionPattern = Pattern.compile(
                new StringBuffer().append(contextPath).append("/t").
                        append("/(?<tid>[0-9])").append("/(?<action>status)").toString());

        Matcher matcher = listPattern.matcher(request.getRequestURI());
        if (matcher.matches()) {
            get(w);
        }
        matcher = itemPattern.matcher(requestUri);
        if (matcher.matches()) {
            int tid = Integer.parseInt(matcher.group("tid"));
            //System.out.println(tid);
            //System.out.println(get(tid));
            get(tid, w);
        }
        matcher = actionPattern.matcher(requestUri);
        if (matcher.matches()) {
            int tid = Integer.parseInt(matcher.group("tid"));

            String action = matcher.group("action");
            get(tid, action, w);
        }
    }
}
