/**
package-info.java that enforces the 'mets' prefix in the METS metadata XML. 
 */

@javax.xml.bind.annotation.XmlSchema(namespace = "http://www.loc.gov/METS/", xmlns = {
        @XmlNs(namespaceURI = "http://www.w3.org/1999/xlink", prefix = "xlink"),
        @XmlNs(namespaceURI = "info:lc/xmlns/premis-v2", prefix = "premis"),
        @XmlNs(namespaceURI = "http://www.loc.gov/METS/", prefix = "mets") },
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
package gov.loc.mets;

import javax.xml.bind.annotation.XmlNs;

