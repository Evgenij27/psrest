package my.proj.wrapper;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WrappedHttpServletRequest extends HttpServletRequestWrapper {

    private String action;
    private int tid;


    public WrappedHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public int getTid() {
        return tid;
    }

}
