package my.proj;

import javax.print.attribute.PrintRequestAttribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class Manager {

    private static final Manager INSTANCE = new Manager();

    public static synchronized Manager getInstance() {
        return INSTANCE;
    }

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

    public synchronized void post(HttpServletRequest request, HttpServletResponse response)
            throws IOException {



    }
}
