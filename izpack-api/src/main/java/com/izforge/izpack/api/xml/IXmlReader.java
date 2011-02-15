package com.izforge.izpack.api.xml;

import com.izforge.izpack.api.adaptator.XMLException;
import org.izpack.xsd.icons.Icons;
import org.izpack.xsd.installation.Installation;

import java.io.InputStream;

/**
 * Transform an xml into a java object
 */
public interface IXmlReader
{
    /**
     * unmarshal an xml into a Installation
     *
     * @param input the xml
     * @return an InstallationType
     * @throws XMLException if something is wrong with the xml
     */
    Installation readInstallation(InputStream input) throws XMLException;

    /**
     * unmarshal an xml into a Installation
     *
     * @param input    the xml
     * @param systemId System id of the file parsed
     * @return an Installation
     * @throws XMLException if something is wrong with the xml
     */
    Installation readInstallation(InputStream input, String systemId) throws XMLException;

    /**
     * unmarshal an xml into a list of icons
     *
     * @param input    the xml
     */
    Icons readIcons(InputStream input) throws XMLException;
}
