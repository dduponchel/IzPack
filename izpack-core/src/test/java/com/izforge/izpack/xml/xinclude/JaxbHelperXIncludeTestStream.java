package com.izforge.izpack.xml.xinclude;

import com.izforge.izpack.xml.JaxbHelper;
import org.izpack.xsd.installation.Installation;

import java.net.URL;

/**
 * Test the XInclude style functionality.
 * Use the inputStream method.
 */
public class JaxbHelperXIncludeTestStream extends JaxbHelperXincludeTestBase
{
    @Override
    public void doTest(String fileBase) throws Exception
    {
        URL inputURL = getClass().getResource(fileBase + "-input.xml");
        URL expectURL = getClass().getResource(fileBase + "-expect.xml");
        JaxbHelper jaxbHelper = new JaxbHelper();
        Installation inputInstallation = jaxbHelper.unmarshalInstallation(inputURL.openStream(), inputURL.toExternalForm());
        Installation expectedInstallation = jaxbHelper.unmarshalInstallation(expectURL.openStream(), inputURL.toExternalForm());
        deepEqual(expectedInstallation, inputInstallation);
    }
}