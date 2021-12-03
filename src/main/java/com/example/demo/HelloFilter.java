package com.example.demo;

import org.apache.catalina.connector.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

@Component
@Order(1) // only needed if order of filter execution must be well-defined
public class HelloFilter implements Filter {
    private static final Logger DEFAULT_LOGGER = LoggerFactory.getLogger(HelloFilter.class);

    private Logger log = DEFAULT_LOGGER;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (servletRequest instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintWriter bufferWritter = new PrintWriter(buffer);

            // Need a ServletOutputStream wrapper for our buffer
            ServletOutputStream servletOutputStream = new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener listener) {

                }

                @Override
                public void write(int b) throws IOException {
                    buffer.write(b);
                }
            };

            Response memoryResponse = new Response();
            memoryResponse.setCoyoteResponse(new org.apache.coyote.Response());

            if (httpServletRequest.getRequestURI().equals("/hi2")) {
                HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(memoryResponse) {
                    @Override
                    public PrintWriter getWriter() throws IOException {
                        return bufferWritter;
                    }

                    @Override
                    public ServletOutputStream getOutputStream() throws IOException {
                        return servletOutputStream;
                    }
                };

                httpServletRequest.getRequestDispatcher("/hi").forward(httpServletRequest, wrapper);
                wrapper.flushBuffer();

                String origBody = buffer.toString();
                String updatedBody = "UPDATED: " + origBody;

                servletResponse.getWriter().write(updatedBody);

                return;
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
