package org.tolven.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/** 
 * This Class is used to generate PDF report content
 * 
 * @author Suja
 * added on 6/25/2010
 */
public class ContentCaptureServletResponse extends HttpServletResponseWrapper {
	
	private ByteArrayOutputStream contentBuffer;
	private PrintWriter writer;
	
	public ContentCaptureServletResponse(HttpServletResponse originalResponse) {
		super(originalResponse); 
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if(writer == null){
			contentBuffer = new ByteArrayOutputStream();
			writer = new PrintWriter(contentBuffer);
		}
		return writer;
	}
	
	/**
	 * Generate PDF Report data
	 * @author Suja
	 * added on 10/12/09
	 * @param path
	 * @return String
	 */
	public String getContent(String path) {
		if (writer!=null)
			writer.flush(); 
		else {
			contentBuffer = new ByteArrayOutputStream();
			writer = new PrintWriter(contentBuffer);
		}
		String xhtmlContent = new String(contentBuffer.toByteArray());
		xhtmlContent = xhtmlContent.replaceAll("Generate Report|Print report after saving|" +
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\" >", "");
		xhtmlContent = xhtmlContent.replaceAll("TOLVENLOGOPATH", "file://"+path.replaceAll("\\\\","/")+"/images");
		xhtmlContent = "<html> <head><link rel=\"stylesheet\" type=\"text/css\" href=\"file://"+path+"/styles/cchitReport.css\" media=\"print\"/></head>"+ 
						"<body>"+xhtmlContent+"</body></html>";
		xhtmlContent = xhtmlContent.replaceAll("<thead>|</thead>|"+ 
											   "<tbody>|</tbody>","");
//		String xhtmlContent="<html> <head></head><body>ddddddddd</body></html>";
		return xhtmlContent; 
	}
}








