package org.tolven.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.tolven.locale.TolvenResourceBundle;
import org.tolven.web.security.GeneralSecurityFilter;
import org.tolven.web.util.FileInfo;
import org.tolven.web.util.JSMin;

public class ResourceServlet extends TolvenServlet {

	private static final long serialVersionUID = 1L;
	private static final String JAVASCRIPT_CACHE = "JAVASCRIPT_CACHE";
	
//    protected static final SimpleDateFormat format =
//   	 new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US); 

//    public String getLastModifiedHttp( Date modifiedDate ) {
//    	String lastModifiedHttp = null;
//    	synchronized (format) {
//             lastModifiedHttp = format.format(modifiedDate);
//         }
//         return lastModifiedHttp;
//    }
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String resourceName = request.getServletPath();
    	String localAddr = request.getLocalAddr();
        InputStream inputStream = null;
    	try {
			String path = getPropertiesBean().getProperty(RESOURCE_OVERRIDE + "." + localAddr);
			if (path !=null ) {
				// Make sure path ends with "/"
				if (!path.endsWith("/")) {
					path = path + "/";
				}
				inputStream = tryURL( path, resourceName);
			}
			if (inputStream==null) {
				path = getPropertiesBean().getProperty(RESOURCE_OVERRIDE );
				if (path !=null ) {
					// Make sure path ends with "/"
			    	if (!path.endsWith("/")) {
			    		path = path + "/";
			    	}
					inputStream = tryURL( path, resourceName);
				}
			}
			if (inputStream==null) {
				path = resourceName;
				if (path !=null ) {
					inputStream = request.getSession().getServletContext().getResourceAsStream(path);

					String mimeType = getServletContext().getMimeType(path);
					if(null != mimeType){
						response.setContentType(mimeType);
					}
				}
			}
	    	boolean compressed = false;
			// Check if a javascript file needs to be compressed or not. 
			// Compress if "tolven.web.javascript.debug" is set to false.
			if( !isJSDebugEnabled(request) && "text/javascript".equalsIgnoreCase(response.getContentType())){
				if(null != inputStream ){
					try {
						String js = getCompressedJSFile(path, request, inputStream);

						if(js != null){
							response.getWriter().write(js);
							compressed = true;
							inputStream.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
 			// Now copy the stream to output
    	if(!compressed){
			if (inputStream!=null) {
//    		TolvenLogger.info( "ResourceServlet: " + path, ResourceServlet.class);
				copyStream( inputStream, response.getOutputStream());
				inputStream.close();
			}
		}
		} catch (Exception e) {
			throw new ServletException( "Error processing resource request for " + resourceName , e );
		}
	}
	/*
	 * 
	 */
	protected Map<String, FileInfo> getCache(HttpServletRequest request){
        HttpSession session = request.getSession();
        Map<String, FileInfo> cache = (Map<String, FileInfo>) session.getAttribute(JAVASCRIPT_CACHE);
        if( null == cache){
        	cache = new HashMap<String, FileInfo>();
        	session.setAttribute(JAVASCRIPT_CACHE, cache);
        }
        return cache;
	}
	/*
	 * use JSMin utility to compress js file. 
	 * Original code will not return anything. modified jsmin to return outcome of the compression as string.
	 */
	protected static String compressedFile(InputStream in, String path) throws Exception{
		String out;
		
		JSMin jsmin = new JSMin( in, new ByteArrayOutputStream());
		try {
			out = jsmin.jsmin();
		} catch (Exception e) {
			throw e;
		}
		return out;
	}

	/*
	 * To cache in memory, check for updates when someone requests a js: 
	 * This method checks if the date of the file on disk is newer than (or just different from) the date in the file 
	 * cached and if so, then remove that cached copy and compress from disk again.
	 */
	protected String getCompressedJSFile(String path, HttpServletRequest request, InputStream in) throws Exception{
		Map<String, FileInfo> cache = getCache(request);
		String js;
		long lastModified = -1;
		
		String realpath = request.getSession().getServletContext().getRealPath(path);
		File jsFile = new File( realpath);
		if(jsFile.exists()){
			lastModified = jsFile.lastModified();
		}
		
		if(cache.containsKey( path)){
			FileInfo fi = cache.get(path);
			if(fi.getTimestamp() != lastModified && lastModified > 0){
				fi.setScript( compressedFile(in, path));
				fi.setTimestamp( lastModified);
			}
			js = fi.getScript();
			
		}else{// no hit so create new
			js = compressedFile(in, path);
			cache.put(path, new FileInfo(js, lastModified));
		}
		//InputStream is = new FileInputStream(js);
		return js;
	}
	/*
	 * Based on the system property, we determine if the file should be compressed or not.
	 */
    protected boolean isJSDebugEnabled(HttpServletRequest request) {
        TolvenResourceBundle resourceBundle = (TolvenResourceBundle) request.getSession().getAttribute(GeneralSecurityFilter.TOLVEN_RESOURCEBUNDLE);
        String jsDebug = (String) resourceBundle.getString("tolven.web.javascript.debug");
        return "true".equals(jsDebug);
    }
	
}
