package com.usf.studentmatching.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The purpose of this class is to intrecept input and output responses coming in and going out of
 * student matching application.
 * Added this class purely for informational purpose.
 */
@Component
@Order(2)
public class RequestResponseFilter implements Filter {

    private final static Logger log = LoggerFactory.getLogger(RequestResponseFilter.class);
    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        log.info("Initializing filter :{}", this);
    }

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        log.info("Logging Request  {} : {}", req.getMethod(), req.getRequestURI());
        chain.doFilter(request, response);
        log.info("Logging Response :{}", res.getContentType());
    }

    @Override
    public void destroy() {
        log.warn("Destructing filter :{}", this);
    }
}
