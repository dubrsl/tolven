
package org.tolven.ws.trim;


public class WebServiceField {

    protected String name;
    protected Object value;
    
    public WebServiceField() {
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
