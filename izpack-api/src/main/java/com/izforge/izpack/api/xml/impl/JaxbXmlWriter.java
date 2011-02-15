package com.izforge.izpack.api.xml.impl;

import com.izforge.izpack.api.adaptator.XMLException;
import com.izforge.izpack.api.xml.IXmlWriter;
import com.izforge.izpack.api.xml.XmlSchema;
import org.izpack.xsd.installation.Installation;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JaxbXmlWriter implements IXmlWriter
{
    @Override
    public void write(Installation installation, File destination) throws XMLException
    {
        try
        {
            FileOutputStream os = new FileOutputStream(destination);
            write(installation, os);
            os.close();
        }
        catch (IOException e)
        {
            throw new XMLException(e);
        }
    }

    @Override
    public void write(Installation installation, OutputStream os) throws XMLException
    {
        JAXBContext context;
        try
        {
            context = JAXBContext.newInstance(Installation.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://izpack.org/xsd/installation ../../install.xsd");

            // validate that the result xml will be valid
            marshaller.setSchema(XmlSchema.INSTALLATION.getSchema());
            marshaller.marshal(installation, os);
        }
        catch (JAXBException e)
        {
            throw new XMLException(e);
        }
    }
}