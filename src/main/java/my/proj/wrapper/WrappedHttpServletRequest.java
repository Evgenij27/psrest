package my.proj.wrapper;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WrappedHttpServletRequest extends HttpServletRequestWrapper {

    private String action;
    private int tid;


    public WrappedHttpServletRequest(HttpServletRequest request) {
        super(request);
    }


}
