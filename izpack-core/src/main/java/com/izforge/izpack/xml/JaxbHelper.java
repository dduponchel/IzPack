package com.izforge.izpack.xml;

import org.izpack.xsd.installation.InstallationType;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.InputStream;

/**
 * Wrapper around JAXB classes, to simplify its use.
 */
public class JaxbHelper
{
    /**
     * Tie together the xsd and the namespace.
     */
    private static class XmlSchema
    {
        private final String xsd;
        private final String namespace;

        private XmlSchema(String xsd, String namespace)
        {
            this.xsd = xsd;
            this.namespace = namespace;
        }

        public String getNamespace()
        {
            return namespace;
        }

        public String getXsd()
        {
            return xsd;
        }
    }

    private static final XmlSchema INSTALLATION = new XmlSchema("/xsd/installation.xsd", "http://izpack.org/xsd/installation");

    /**
     * unmarshal an xml into a InstallationType
     * @param input the xml
     * @return an InstallationType
     * @throws JAXBException if something is wrong with the xml
     */
    public InstallationType unmarshalInstallation(InputStream input) throws JAXBException
    {
        return unmarshal(input, INSTALLATION, InstallationType.class);
    }

    /**
     * unmarshal a xml with jaxb.
     *
     * @param input the stream containing the xml
     * @param xmlSchema the xsd to use
     * @param type the java class
     * @return an unmarshalled xml
     * @throws JAXBException if something is wrong with the xml
     */
    private <T> T unmarshal(InputStream input, XmlSchema xmlSchema, Class<T> type)
            throws JAXBException
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // validate the xml with an xsd
            SchemaFactory sf = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(this.getClass().getResource(xmlSchema.getXsd()));
            unmarshaller.setSchema(schema);

            // add a default namespace to support older xml
            XMLReader reader = XMLReaderFactory.createXMLReader();
            NamespaceFilter inFilter = new NamespaceFilter(xmlSchema.getNamespace(), true);
            inFilter.setParent(reader);
            SAXSource source = new SAXSource(inFilter, new InputSource(input));
            
            // unmarshal !
            JAXBElement<T> jaxbElement = unmarshaller.unmarshal(source, type);
            return jaxbElement.getValue();
        }
        catch (SAXException e)
        {
            // not technically a jaxb exception, but close enough
            throw new JAXBException(e);
        }
    }
}
