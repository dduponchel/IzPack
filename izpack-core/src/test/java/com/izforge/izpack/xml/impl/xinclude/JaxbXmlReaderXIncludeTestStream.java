package com.izforge.izpack.xml.impl.xinclude;

import com.izforge.izpack.xml.impl.JaxbXmlReader;
import org.izpack.xsd.installation.Installation;

import java.net.URL;

/**
 * Test the XInclude style functionality.
 * Use the inputStream method.
 */
public class JaxbXmlReaderXIncludeTestStream extends JaxbXmlReaderXincludeTestBase
{
    @Override
    public void doTest(String fileBase) throws Exception
    {
        URL inputURL = getClass().getResource(fileBase + "-input.xml");
        URL expectURL = getClass().getResource(fileBase + "-expect.xml");
        JaxbXmlReader xmlReader = new JaxbXmlReader();
        Installation inputInstallation = xmlReader.readInstallation(inputURL.openStream(), inputURL.toExternalForm());
        Installation expectedInstallation = xmlReader.readInstallation(expectURL.openStream(), inputURL.toExternalForm());
        deepEqual(expectedInstallation, inputInstallation);
    }
}