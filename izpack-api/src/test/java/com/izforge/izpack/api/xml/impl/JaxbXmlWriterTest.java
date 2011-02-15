package com.izforge.izpack.api.xml.impl;

import com.izforge.izpack.api.adaptator.XMLException;
import org.izpack.xsd.installation.*;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.*;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JaxbXmlWriterTest
{
    private static final String MARSHAL_EXPECTED_XML = "marshalExpected.xml";
    private JaxbXmlWriter xmlWriter;

    @Before
    public void setUp()
    {
        xmlWriter = new JaxbXmlWriter();
    }

    @Test(expected = XMLException.class)
    public void marshalInvalidObject() throws IOException, JAXBException
    {
        // not enough informations to create a consistent installation.xml
        Installation installation = new Installation();
        installation.setInfo(new InfoType());
        installation.getInfo().setAppname("appname");
        installation.getInfo().setAppversion("version");
        xmlWriter.write(installation, System.out);
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

        xmlWriter.write(installation, output);


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
