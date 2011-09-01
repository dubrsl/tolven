package org.tolven.url.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RestConnection extends URLConnection {

	private InputStream inputStream;

	protected RestConnection(URL url, InputStream inputStream ) {
		super(url);
    	this.inputStream = inputStream;
	}
	
	@Override
	public void connect() throws IOException {
		// Nothing to do
	}
		
	@Override
	public InputStream getInputStream( ) {
		return inputStream;
	}
}
