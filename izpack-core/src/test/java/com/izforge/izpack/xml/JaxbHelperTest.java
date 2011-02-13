package com.izforge.izpack.xml;

import org.izpack.xsd.installation.*;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.*;

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
    private static final String MARSHAL_EXPECTED_XML = "marshalExpected.xml";
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
    public void marshalInvalidObject() throws IOException, JAXBException
    {
        // not enough informations to create a consistent installation.xml
        Installation installation = new Installation();
        installation.setInfo(new InfoType());
        installation.getInfo().setAppname("appname");
        installation.getInfo().setAppversion("version");
        jaxbHelper.marshal(installation, System.out);
    }

    @Test
    public void marshalValidObject() throws IOException, JAXBException
    {
        Installation installation = new Installation();
        installation.setInfo(new InfoType());
        installation.getInfo().setAppname("appname");
        installation.getInfo().setAppversion("version");
        installation.setLocale(new LocaleType());
        LangpackType lang = new LangpackType();
        lang.setIso3("fr");
        installation.getLocale().getLangpack().add(lang);
        installation.setPacks(new PacksType());
        PackType pack = new PackType();
        pack.setDescription("description");
        pack.setName("name");
        installation.getPacks().getPack().add(pack);
        installation.setPanels(new PanelsType());

        OutputStream output = new OutputStream()
        {
            private StringBuilder string = new StringBuilder();

            @Override
            public void write(int b) throws IOException
            {
                this.string.append((char) b);
            }

            @Override
            public String toString()
            {
                return this.string.toString();
            }
        };

        jaxbHelper.marshal(installation, output);


        InputStream expectedStream = this.getClass().getResourceAsStream(MARSHAL_EXPECTED_XML);
        final char[] buffer = new char[0x10000];
        StringBuilder expected = new StringBuilder();
        Reader in = new InputStreamReader(expectedStream, "UTF-8");
        int read;
        do
        {
            read = in.read(buffer, 0, buffer.length);
            if (read > 0)
            {
                expected.append(buffer, 0, read);
            }
        }
        while (read >= 0);

        assertThat(output.toString(), is(expected.toString()));
    }
}
