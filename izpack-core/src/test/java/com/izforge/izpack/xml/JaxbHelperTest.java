package com.izforge.izpack.xml;

import generated.Customer;
import generated.PhoneNumber;
import org.izpack.xsd.installation.InfoType;
import org.izpack.xsd.installation.Installation;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * To replace XMLParser, this jaxb helper will have to pass all of those tests.
 */
public class JaxbHelperTest
{
    private static final String NORMAL_XML = "normal.xml";
    private static final String NOT_A_XML = "not-a-xml.txt";
    private static final String INVALID_XML = "invalid.xml";
    private static final String OLD_XML = "old.xml";
    private JaxbHelper jaxbHelper;

    @Before
    public void setUp()
    {
        jaxbHelper = new JaxbHelper();
    }

    @Test
    public void normalXml() throws FileNotFoundException, JAXBException
    {
        Installation result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(NORMAL_XML));
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
        Installation result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(OLD_XML));
        assertThat(result, is(notNullValue()));
        assertThat(result.getInfo().getAppname(), is("name"));
        assertThat(result.getInfo().getAppversion(), is("version"));
        assertThat(result.getPacks().getPack().size(), is(1));
        assertThat(result.getLocale().getLangpack().size(), is(1));
    }

    @Test(expected = JAXBException.class)
    public void invalidXml() throws JAXBException
    {
        Installation result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(NOT_A_XML));
    }

    @Test(expected = UnmarshalException.class)
    public void xsdValidation() throws JAXBException
    {
        Installation result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(INVALID_XML));
    }

    @Test
    public void substitution()
    {
        throw new UnsupportedOperationException("not implemented yet");
    }

    @Test(expected = JAXBException.class)
    public void testMarshal() throws IOException, JAXBException
    {
        Installation installation = new Installation();
        installation.setInfo(new InfoType());
        installation.getInfo().setAppname("appname");
        installation.getInfo().setAppversion("version");
        jaxbHelper.marshal(installation, System.out);
    }
}
