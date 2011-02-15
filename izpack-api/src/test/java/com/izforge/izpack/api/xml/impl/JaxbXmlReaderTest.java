package com.izforge.izpack.api.xml.impl;

import com.izforge.izpack.api.adaptator.XMLException;
import org.izpack.xsd.installation.Installation;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class JaxbXmlReaderTest
{
    private static final String NORMAL_XML = "normal.xml";
    private static final String NOT_A_XML = "not-a-xml.txt";
    private static final String INVALID_XML = "invalid.xml";
    private static final String OLD_XML = "old.xml";

    private JaxbXmlReader xmlReader;

    @Before
    public void setUp()
    {
        xmlReader = new JaxbXmlReader();
    }

    @Test
    public void normalXml() throws FileNotFoundException, JAXBException
    {
        Installation result = xmlReader.readInstallation(this.getClass().getResourceAsStream(NORMAL_XML));
        assertThat(result, is(notNullValue()));
        assertThat(result.getInfo().getAppname(), is("name"));
        assertThat(result.getInfo().getAppversion(), is("version"));
        assertThat(result.getPacks().getPack().size(), is(1));
        assertThat(result.getLocale().getLangpack().size(), is(1));
    }

    @Test
    public void oldXml() throws FileNotFoundException, JAXBException
    {
        // without namespace or other xml complex stuff, we must understand "old" install.xml
        Installation result = xmlReader.readInstallation(this.getClass().getResourceAsStream(OLD_XML));
        assertThat(result, is(notNullValue()));
        assertThat(result.getInfo().getAppname(), is("name"));
        assertThat(result.getInfo().getAppversion(), is("version"));
        assertThat(result.getPacks().getPack().size(), is(1));
        assertThat(result.getLocale().getLangpack().size(), is(1));
    }

    @Test(expected = XMLException.class)
    public void invalidXml() throws JAXBException
    {
        xmlReader.readInstallation(this.getClass().getResourceAsStream(NOT_A_XML));
    }

    @Test(expected = XMLException.class)
    public void xsdValidation() throws JAXBException
    {
        xmlReader.readInstallation(this.getClass().getResourceAsStream(INVALID_XML));
    }

    @Test
    public void substitution()
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
