package my.proj;

import my.proj.factory.RunnableFactory;
import my.proj.factory.ScriptData;
import my.proj.writer.BuffWriter;
import my.proj.wrapper.WrappedHttpServletRequest;

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

    private static final String SCRIPT_COMPLETE = "Script %d is completed";

    private static final String SCRIPT_WORKING = "Script %d is stil working";

    private static ConcurrentSkipListMap<Integer, ScriptData> scripts =
            new ConcurrentSkipListMap<>();


    private static AtomicInteger tid = new AtomicInteger(0);

    private static final ExecutorService POOL = Executors.newFixedThreadPool(10);

    private static RunnableFactory rsf = RunnableFactory.INSTANCE;

    void get(WrappedHttpServletRequest wrappedRequest, HttpServletResponse response) 
        throws IOException {

        char[] data = null;
        int tid = wrappedRequest.getTid();
        String action = wrappedRequest.getAction();
        
        if (tid < 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }

        if (action == null && tid == 0) {
            data = get();
        }

        if (action == null && tid != 0) {
            data = get(tid);
        }
        
        if (action != null && tid != 0) {
            data = get(tid, action);
        }

        try (final PrintWriter writer = response.getWriter()) {
           /*
            System.out.println("GET");
            System.out.print("Request URI ==> " + request.getRequestURI() + "\n");
            System.out.print("Request URL ==> "  + request.getRequestURL() + "\n");
            System.out.print("Context path ==> " + request.getContextPath());
           */
                if (!writer.checkError()) {  
                    writer.write(data);
                } 
        }
    }

    void post(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        BuffWriter buff = new BuffWriter();
        BuffWriter errBuff = new BuffWriter();
        ScriptContext context = new SimpleScriptContext();
        context.setErrorWriter(errBuff);
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

    private char[] get() {
        
        return scripts.toString().toCharArray();
    }

    private char[] get(int tid) {
        Writer w = scripts.get(tid).getWriter();
        BuffWriter bw = (BuffWriter) w;
        return bw.getBuff().toCharArray();         
    }

    private char[] get(int tid, String action) {
        char[] result = null;
        if (action.equals("error") && tid !=0) {
            Writer w = scripts.get(tid).getErrorWriter();
            BuffWriter bw = (BuffWriter) w;
            result =  bw.getBuff().toCharArray();
        }

        if (action.equals("status") && tid != 0) {
            result = getStatus(tid);
        }        
        return result;
    }

    private char[] getStatus(int tid) {
        Future<?> future = scripts.get(tid).getFuture();
        if (future.isDone()) {
            return String.format(SCRIPT_COMPLETE, tid).toCharArray();
        } else {
            return String.format(SCRIPT_WORKING, tid).toCharArray();
        }
    }
}
