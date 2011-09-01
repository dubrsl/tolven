
package org.tolven.ws.trim.jaxws;

import javax.jws.Oneway;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.RequestWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.4.1-hudson-346-
 * Generated source version: 2.1
 * 
 */
@WebService(name = "TrimPort", targetNamespace = "http://tolven.org/trim")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface TrimPort {


    /**
     * 
     * @param webServiceTrim
     */
    @WebMethod(action = "submitTrim")
    @Oneway
    @RequestWrapper(localName = "submitTrim", targetNamespace = "http://tolven.org/trim", className = "org.tolven.ws.trim.jaxws.SubmitTrimRequest")
    public void submitTrim(
        @WebParam(name = "webServiceTrim", targetNamespace = "")
        WebServiceTrim webServiceTrim);

}
