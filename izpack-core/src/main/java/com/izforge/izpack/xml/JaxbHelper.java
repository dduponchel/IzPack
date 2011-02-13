package com.izforge.izpack.xml;

import com.izforge.izpack.api.adaptator.IXMLParser;
import com.izforge.izpack.api.adaptator.XMLException;
import com.izforge.izpack.api.adaptator.impl.XMLParser;
import org.izpack.xsd.installation.Installation;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import javax.xml.bind.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.net.URL;

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
        SchemaFactory schemaFactory;
        {
            schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        }
        private final String xsd;
        private final String namespace;
        private Schema schema;

        private XmlSchema(String xsd, String namespace)
        {
            this.xsd = xsd;
            this.namespace = namespace;
            try
            {
                this.schema = schemaFactory.newSchema(this.getClass().getResource(xsd));
            }
            catch (SAXException e)
            {
                throw new RuntimeException(e);
            }
        }

        public String getNamespace()
        {
            return namespace;
        }

        public String getXsd()
        {
            return xsd;
        }

        public Schema getSchema()
        {
            return schema;
        }
    }

    private static final XmlSchema INSTALLATION = new XmlSchema("/xsd/installation.xsd", "http://izpack.org/xsd/installation");

    /**
     * unmarshal an xml into a InstallationType
     *
     * @param input the xml
     * @return an InstallationType
     * @throws JAXBException if something is wrong with the xml
     */
    public Installation unmarshalInstallation(InputStream input) throws JAXBException
    {
        return unmarshalInstallation(input, null);
    }

    /**
     * unmarshal an xml into a Installation
     *
     * @param input the xml
     * @param systemId System id of the file parsed
     * @return an Installation
     * @throws JAXBException if something is wrong with the xml
     */
    public Installation unmarshalInstallation(InputStream input, String systemId) throws JAXBException
    {
        return unmarshal(input, INSTALLATION, Installation.class, systemId);
    }

    /**
     * Marshal an object into an xml
     * @param installation the object to marshal
     * @param destination the file to put the xml into
     * @throws JAXBException if something went wrong
     */
    public void marshal(Installation installation, File destination) throws JAXBException
    {
        try
        {
            FileOutputStream os = new FileOutputStream(destination);
            marshal(installation, os);
            os.close();
        }
        catch (IOException e)
        {
            throw new JAXBException(e);
        }
    }

    /**
     * Marshal an object into an xml
     * @param installation the object to marshal
     * @param os the stream to put the xml into
     * @throws JAXBException if something went wrong
     */
    public void marshal(Installation installation, OutputStream os) throws JAXBException
    {
        JAXBContext context = JAXBContext.newInstance(Installation.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://izpack.org/xsd/installation ../../install.xsd");

        // validate that the result xml will be valid 
        marshaller.setSchema(INSTALLATION.getSchema());
        marshaller.marshal(installation, os);
    }

    /**
     * unmarshal a xml with jaxb.
     *
     *
     * @param input     the stream containing the xml
     * @param xmlSchema the xsd to use
     * @param type      the java class
     * @param systemId  System id of the file parsed
     * @return an unmarshalled xml
     * @throws JAXBException if something is wrong with the xml
     */
    private <T> T unmarshal(InputStream input, XmlSchema xmlSchema, Class<T> type, String systemId)
            throws JAXBException
    {
        try
        {
            JAXBContext context = JAXBContext.newInstance(type);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // validate the xml with an xsd
            unmarshaller.setSchema(xmlSchema.getSchema());


            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            saxParserFactory.setNamespaceAware(true);
            saxParserFactory.setXIncludeAware(true);
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();


            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);


            // add a default namespace to support older xml
            XMLFilter inFilter = new NamespaceFilter(xmlSchema.getNamespace(), true);
            inFilter.setParent(xmlReader);

            InputSource inputSource = new InputSource(input);
            inputSource.setSystemId(systemId);
            SAXSource source = new SAXSource(inFilter, inputSource);
            source.setXMLReader(inFilter);
            URL xslResourceUrl = IXMLParser.class.getResource(XMLParser.XSL_FILE_NAME);
            if (xslResourceUrl == null)
            {
                throw new XMLException("Can't find IzPack internal file \"" + XMLParser.XSL_FILE_NAME + "\"");
            }
            Source xsltSource = new StreamSource(xslResourceUrl.openStream());
            Transformer xformer;
            xformer = TransformerFactory.newInstance().newTransformer(xsltSource);
            xformer.transform(source, result);

            // TODO find a better to do Result -> Source

            // unmarshal !
            String stringResult = result.getWriter().toString();
            JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(stringResult.getBytes())), type);
            return jaxbElement.getValue();
        }
        catch (SAXException e)
        {
            // not technically a jaxb exception, but close enough
            throw new JAXBException(e);
        }
        catch (ParserConfigurationException e)
        {
            // not technically a jaxb exception, but close enough
            throw new JAXBException(e);
        }
        catch (TransformerConfigurationException e)
        {
            // not technically a jaxb exception, but close enough
            throw new JAXBException(e);
        }
        catch (TransformerException e)
        {
            // not technically a jaxb exception, but close enough
            throw new JAXBException(e);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
