
package org.tolven.ws.trim;

import java.util.ArrayList;
import java.util.List;

public class WebServiceTrim {

    protected long accountId;
    protected List<WebServiceField> fields;
    protected String name;
    
    public WebServiceTrim(){
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long value) {
        this.accountId = value;
    }

    public List<WebServiceField> getFields() {
        if (fields == null) {
            fields = new ArrayList<WebServiceField>();
        }
        return this.fields;
    }

    public void setFields(List<WebServiceField> fields) {
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

}
