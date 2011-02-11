package com.izforge.izpack.xml;

import org.izpack.xsd.installation.InstallationType;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.FileNotFoundException;

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
        InstallationType result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(NORMAL_XML));
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
        InstallationType result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(OLD_XML));
        assertThat(result, is(notNullValue()));
        assertThat(result.getInfo().getAppname(), is("name"));
        assertThat(result.getInfo().getAppversion(), is("version"));
        assertThat(result.getPacks().getPack().size(), is(1));
        assertThat(result.getLocale().getLangpack().size(), is(1));
    }

    @Test(expected = JAXBException.class)
    public void invalidXml() throws JAXBException
    {
        InstallationType result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(NOT_A_XML));
    }

    @Test(expected = UnmarshalException.class)
    public void xsdValidation() throws JAXBException
    {
        InstallationType result = jaxbHelper.unmarshalInstallation(this.getClass().getResourceAsStream(INVALID_XML));
    }

    @Test
    public void substitution()
    {
        throw new UnsupportedOperationException("not implemented yet");
    }
}
