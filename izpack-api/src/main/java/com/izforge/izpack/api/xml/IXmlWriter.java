package com.izforge.izpack.api.xml;

import com.izforge.izpack.api.adaptator.XMLException;
import org.izpack.xsd.installation.Installation;

import java.io.File;
import java.io.OutputStream;

/**
 * Transform a java object into an xml resource
 */
public interface IXmlWriter
{
    /**
     * Marshal an object into an xml
     *
     * @param installation the object to write
     * @param destination  the file to put the xml into
     * @throws XMLException if something went wrong
     */
    void write(Installation installation, File destination) throws XMLException;

    /**
     * Marshal an object into an xml
     *
     * @param installation the object to write
     * @param os           the stream to put the xml into
     * @throws XMLException if something went wrong
     */
    void write(Installation installation, OutputStream os) throws XMLException;
}
