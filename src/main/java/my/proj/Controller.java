package my.proj;

import my.proj.Manager;
import my.proj.wrapper.WrappedHttpServletRequest;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.script.*;
import java.io.*;
import javax.servlet.annotation.*;
import java.util.concurrent.*;
import java.util.*;


@WebServlet(urlPatterns={"/t", "/t/*"})
public class Controller extends HttpServlet {

    private Manager manager;

    public void init(ServletConfig conf) throws ServletException {
        manager = new Manager();
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
        WrappedHttpServletRequest wrappedRequest = (WrappedHttpServletRequest) request;
       
        manager.get(wrappedRequest, response);
    }
           
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
        throws IOException, ServletException {

        System.out.println("I am alive!!!");
        manager.post(request, response);
        System.out.println("Done");
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
