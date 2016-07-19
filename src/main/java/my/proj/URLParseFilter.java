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

    private static final Pattern PATTERN = Pattern.compile("/\\w+/t/*(?<tid>[0-9]*)/*(?<action>([status|error])*)");

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        parseURL(httpRequest, httpResponse);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

    private void parseURL(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        Matcher matcher = PATTERN.matcher(uri);
        if (matcher.find()) {
            System.out.println("tid ==> " + matcher.group("tid"));
            System.out.println("action ==> " + matcher.group("action"));
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}

