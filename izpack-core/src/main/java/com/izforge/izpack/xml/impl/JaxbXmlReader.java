package com.izforge.izpack.xml.impl;

import com.izforge.izpack.api.adaptator.IXMLParser;
import com.izforge.izpack.api.adaptator.XMLException;
import com.izforge.izpack.api.adaptator.impl.XMLParser;
import com.izforge.izpack.xml.IXmlReader;
import com.izforge.izpack.xml.XmlSchema;
import org.izpack.xsd.icons.Icons;
import org.izpack.xsd.installation.Installation;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;

public class JaxbXmlReader implements IXmlReader
{

    @Override
    public Installation readInstallation(InputStream input) throws XMLException
    {
        return readInstallation(input, null);
    }

    @Override
    public Installation readInstallation(InputStream input, String systemId) throws XMLException
    {
        return unmarshal(input, XmlSchema.INSTALLATION, Installation.class, systemId);
    }

    @Override
    public Icons readIcons(InputStream input) throws XMLException
    {
        return unmarshal(input, XmlSchema.ICONS, Icons.class, null);
    }


    /**
     * unmarshal a xml with jaxb.
     *
     * @param input     the stream containing the xml
     * @param xmlSchema the xsd to use
     * @param type      the java class
     * @param systemId  System id of the file parsed
     * @return an unmarshalled xml
     * @throws XMLException if something is wrong with the xml
     */
    private <T> T unmarshal(InputStream input, XmlSchema xmlSchema, Class<T> type, String systemId)
            throws XMLException
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
        catch (IOException e)
        {
            throw new XMLException(e);
        }
        catch (SAXException e)
        {
            throw new XMLException(e);
        }
        catch (JAXBException e)
        {
            throw new XMLException(e);
        }
        catch (ParserConfigurationException e)
        {
            throw new XMLException(e);
        }
        catch (TransformerConfigurationException e)
        {
            throw new XMLException(e);
        }
        catch (TransformerException e)
        {
            throw new XMLException(e);
        }
    }
}
