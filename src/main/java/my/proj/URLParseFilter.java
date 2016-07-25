package my.proj;

import my.proj.wrapper.WrappedHttpServletRequest;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebFilter(servletNames={"Controller"}, urlPatterns={"/*"})
public class URLParseFilter implements Filter {

    private static final String ACTION_PATTERN = 
        "^/psrest/t/(?<!\\w+)(?<tid>\\d+)/(?<!status|error)(?<action>status|error)(?!\\w+)/*$"; 
    private static final String ITEM_PATTERN = 
        "^/psrest/t/(?<!\\w+)(?<tid>\\d+)(?!\\w+)/*$";

    private static final String LIST_PATTERN =
        "^/psrest/t/*$";
    
    private static Pattern PATTERN;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        WrappedHttpServletRequest wrappedHttpRequest = 
            new WrappedHttpServletRequest(httpRequest);

        parseURL(wrappedHttpRequest, httpResponse);

        filterChain.doFilter(wrappedHttpRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void parseURL(WrappedHttpServletRequest wrappedRequest, HttpServletResponse response)  throws IOException {

        String uri = wrappedRequest.getRequestURI();
        System.out.println(uri);
       
        PATTERN = Pattern.compile(ACTION_PATTERN);
        Matcher m = PATTERN.matcher(uri);
        if (m.find()) {
            System.out.println("ACTION");
            wrappedRequest.setAction(m.group("action"));
            wrappedRequest.setTid(Integer.parseInt(m.group("tid")));
            /*
            System.out.println(m.group("action"));
            System.out.println(m.group("tid"));
            */
            return;
        }
        
        PATTERN = Pattern.compile(ITEM_PATTERN);
        m = PATTERN.matcher(uri);
        if (m.find()) {
            System.out.println("ITEM");
            wrappedRequest.setTid(Integer.parseInt(m.group("tid")));
            //System.out.println(m.group("tid"));
            return;
        }
        PATTERN = Pattern.compile(LIST_PATTERN);
        m = PATTERN.matcher(uri);
        if (m.find()) {
            System.out.println("LIST");
            return;
        }
        
        response.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
}

