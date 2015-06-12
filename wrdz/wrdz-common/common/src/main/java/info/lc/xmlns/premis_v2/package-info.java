/**
package-info.java that enforces the 'premis' prefix in the PREMIS metadata XML. 
 */

@javax.xml.bind.annotation.XmlSchema(namespace = "info:lc/xmlns/premis-v2", xmlns = {
        @XmlNs(namespaceURI = "http://www.w3.org/1999/xlink", prefix = "xlink"),
        @XmlNs(namespaceURI = "info:lc/xmlns/premis-v2", prefix = "premis") },
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package info.lc.xmlns.premis_v2;

import javax.xml.bind.annotation.XmlNs;

