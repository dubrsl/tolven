package org.tolven.web;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.lowagie.text.DocumentException;

/**
 * This Class is used to generate PDF Report
 * 
 * @author Suja
 * added on 6/25/2010
 */
public class RendererFilter implements Filter {

	FilterConfig config;
	private DocumentBuilder documentBuilder;
	
	public void init(FilterConfig config) throws ServletException {
		try {
			this.config = config;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			documentBuilder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new ServletException(e);
		}
	}

	/**
	 * Filter the request by checking the 'RenderOutputType' parameter and generate PDf report.
	 * @author Suja
	 * added on 10/12/09
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, 
						 FilterChain filterChain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)resp;
		
		//Check to see if this filter should apply.
		String renderType = request.getParameter("RenderOutputType");
		if(renderType != null){
			//Capture the content for this request
			ContentCaptureServletResponse capContent = new ContentCaptureServletResponse(response);
			filterChain.doFilter(request,capContent);
			this.config.getServletContext().getRealPath("/");

			try {
				//Parse the XHTML content to a document that is readable by the XHTML renderer.
				String content=capContent.getContent(this.config.getServletContext().getRealPath("/"));
				StringReader contentReader = new StringReader(content);
				InputSource source = new InputSource(contentReader);
				Document xhtmlContent = documentBuilder.parse(source);
				
				if(renderType.equals("pdf")){
					ITextRenderer renderer = new ITextRenderer();
					renderer.setDocument(xhtmlContent,"");
					renderer.layout();
					
					response.setContentType("application/pdf");
					OutputStream browserStream = response.getOutputStream();
					renderer.createPDF(browserStream);
					return;
				}			
			} catch (SAXException e) {
				throw new ServletException(e);
			} catch (DocumentException e) {
				throw new ServletException(e);
			}
		}else{
			//Normal processing
			filterChain.doFilter(request,response);
		}
	}
	
	public void destroy() { }
}
