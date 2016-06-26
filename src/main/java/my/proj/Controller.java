package my.proj;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.script.*;
import java.io.*;
import javax.servlet.annotation.*;
import java.util.concurrent.*;
import java.util.*;

@WebServlet(urlPatterns={"/t"})
public class Controller extends HttpServlet {

    private ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    private CustomWriter writer = new CustomWriter();

    private static synchronized String getCode(HttpServletRequest req) {
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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

        /*
        response.setContentType("text/plain");
        try (final PrintWriter writer = response.getWriter()) {
            for (int i = 0; i < 100;) {
                if (writer.checkError()) {
                    System.out.println("Disconnected " + i);
                    throw new IOException("Client disconnected");
                }
                writer.printf("Line %4d\n", i++);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new ServletException(e);
                }
            }
        }
        */

        try(final PrintWriter servletWriter = response.getWriter()) {
            servletWriter.println("START");
            writer.setWriter(servletWriter);
            try {
                engine.getContext().setWriter(writer);
                engine.eval("var x=1000; while(x--) {print('Hello JS! x = ' + x);}");
            } catch (ScriptException e) {
                writer.write(e.getMessage());
            }
            servletWriter.println("END");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new ServletException(e);
        }
    }
           
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {

        try(final PrintWriter servletWriter = response.getWriter()) {
            servletWriter.println("START");
            writer.setWriter(servletWriter);
            try {
                engine.getContext().setWriter(writer);
                engine.eval(getCode(request));
            } catch (ScriptException e) {
                writer.write(e.getMessage());
            }

            servletWriter.println("END");
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {

    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {
    }
}
