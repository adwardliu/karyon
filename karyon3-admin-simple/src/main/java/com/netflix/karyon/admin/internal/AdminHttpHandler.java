package com.netflix.karyon.admin.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.karyon.admin.Admin;
import com.netflix.karyon.admin.rest.ControllerRegistry;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@Singleton
public class AdminHttpHandler implements HttpHandler {
    private static final Logger LOG = LoggerFactory.getLogger(AdminHttpHandler.class);
    
    private final Provider<ControllerRegistry> controllers;
    private final ObjectMapper mapper;

    @Inject
    public AdminHttpHandler(
            @SimpleAdmin ObjectMapper mapper,
            @Admin Provider<ControllerRegistry> controllers) {
        this.controllers = controllers;
        this.mapper = mapper;
    }
    
    @Override
    public void handle(HttpExchange arg0) throws IOException {
        LOG.info("'{}'", arg0.getRequestURI());
        
        String path = arg0.getRequestURI().getPath();
        
        try {
            if (path.equals("/")) {
                writeResponse(arg0, 404, "NotFound");
            }
            else {
                String parts[] = path.substring(1).split("/");
                String controller = parts[0];
                List<String> p = new ArrayList<>();
                for (int i = 1; i < parts.length; i++) {
                    p.add(parts[i]);
                }
                writeResponse(arg0, 404, mapper.writeValueAsString(controllers.get().invoke(controller, p)));
            }
        }
        catch (Exception e) {
//            e.printStackTrace();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            writeResponse(arg0, 500, sw.toString());
        }
    }
    
    private void writeResponse(HttpExchange arg0, int code, String content) throws IOException {
        OutputStream os = arg0.getResponseBody();
        arg0.sendResponseHeaders(500, content.length());
        os.write(content.getBytes());
        os.close();
    }
}
