
package org.tolven.api.tolvenproperties;

import java.util.Date;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class Adapter1
    extends XmlAdapter<String, Date>
{


    public Date unmarshal(String value) {
        return (org.tolven.api.DateTimeAdapter.parseDateTime(value));
    }

    public String marshal(Date value) {
        return (org.tolven.api.DateTimeAdapter.printDateTime(value));
    }

}
