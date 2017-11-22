/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * https://github.com/mp911de/rest-api-test
 * 
 * Embedded InMemory REST server for RESTEasy. Usage:
 * <ul>
 * <li>InMemoryRestServer srv = InMemoryRestServer.create(...) passing your resources and provider classes</li>
 * <li>srv.baseUri() for BaseUrl</li>
 * <li>srv.newRequest("/relative/resource/path") to issue requests</li>
 * <li>srv.close() to stop</li>
 * </ul>
 *
 */
package org.onap.portalsdk.fw.test;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;


public class InMemoryRestServer implements AutoCloseable {

    private int port;

    private Set<Object> objects = new HashSet<Object>();
    @SuppressWarnings("rawtypes")
	private Set<Class> classes = new HashSet<Class>();

    private TJWSEmbeddedJaxrsServer server;
    private SecurityDomain securityDomain;
    private ResteasyClient resteasyClient;
    private String bindAddress = "localhost";

    private InMemoryRestServer(Object... objects) {
        append(objects);
    }

    /**
     * Create instance and pass given instances/classes as singletons/providers.
     *
     * @param port
     * Port number to bind
     * @param objects
     * Resource providers
     * @return running instance of {@link InMemoryRestServer}
     * @throws IOException
     * In case of error
     */
    public static InMemoryRestServer create(int port, Object... objects) throws IOException {
        InMemoryRestServer inMemoryRestServer = new InMemoryRestServer(objects);
        inMemoryRestServer.start(port);
        return inMemoryRestServer;
    }

    @SuppressWarnings("rawtypes")
	private void append(Object... objects) {
        for (Object object : objects) {
            if (object instanceof Class) {
                classes.add((Class) object);
            } else {
                this.objects.add(object);
            }
        }
    }

    private void start(int requestPort) throws IOException {
    		if (requestPort <= 0)
    			port = findFreePort();
    		else
    			port = requestPort;
        server = new TJWSEmbeddedJaxrsServer();
        server.setPort(port);
        server.setBindAddress(bindAddress);
        server.setSecurityDomain(securityDomain);

        for (Object object : objects) {
            if (object instanceof Application) {
                server.getDeployment().setApplication((Application) object);
            } else {
                server.getDeployment().getResources().add(object);
            }
        }

        for (@SuppressWarnings("rawtypes") Class resourceOrProvider : classes) {
            if (Application.class.isAssignableFrom(resourceOrProvider)) {
                server.getDeployment().setApplicationClass(resourceOrProvider.getName());
            } else {
                server.getDeployment().getProviderClasses().add(resourceOrProvider.getName());
            }
        }

        server.start();
    }

    /**
     * @return baseURI (http://localhost:PORT) to the REST server.
     */
    public String baseUri() {
        return "http://" + bindAddress + ":" + port;
    }

    /**
     * Begin a new {@link ResteasyWebTarget} with additional, relative path with leading /.
     *
     * @param uriTemplate
     * URI template
     * @return ResteasyWebTarget
     */
    public ResteasyWebTarget newRequest(String uriTemplate) {
        return resteasyClient.target(baseUri() + uriTemplate);
    }

    /**
     * Find a free server port.
     *
     * @return port number.
     * @throws IOException
     * On failure to create server socket
     */
    private static int findFreePort() throws IOException {
        ServerSocket server = new ServerSocket(0);
        int port = server.getLocalPort();
        server.close();
        return port;
    }

    /**
     * Close the server and free resources.
     */
    @Override
    public void close() {
        if (server != null) {
            server.stop();
            server = null;
        }
    }
}