package com.izforge.izpack.xml.impl.xinclude;

import com.izforge.izpack.xml.impl.JaxbXmlReader;
import org.izpack.xsd.installation.Installation;
import org.izpack.xsd.installation.LangpackType;
import org.izpack.xsd.installation.PackType;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.net.URL;

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Base class for xinclude tests.
 */
public abstract class JaxbXmlReaderXincludeTestBase
{

    /**
     * This method takes the fileBase name and attempts to find two files
     * called &lt;fileBase&gt;-input.xml and &lt;fileBase&gt;-expected.xml
     *
     * @param fileBase the base of the test file names
     * @throws Exception
     */
    public abstract void doTest(String fileBase) throws Exception;

    /**
     * This method is used to parse only the input, expecting the parser to fail.
     * The exception is thrown, allowing junit a type check.
     *
     * @param fileBase the base name of the input file.
     * @throws Exception
     */
    public void parseInputForFailures(String fileBase) throws Exception
    {
        URL baseURL = getClass().getResource(fileBase + "-input.xml");
        // set up a new parser to parse the input xml (with includes)
        JaxbXmlReader xmlReader = new JaxbXmlReader();
        Installation inputInstallation = xmlReader.readInstallation(baseURL.openStream(), baseURL.toExternalForm());
        fail("an exception should have been thrown");
    }

    /**
     * Perform a deep equality check on the two objects.
     */
    public void deepEqual(Installation a, Installation b)
    {
        // TODO : reflection to avoid all of those assertThat ?

        // the xml only declare appname, appversion, packs, locales.
        assertThat(a.getInfo(), is(not(nullValue())));
        assertThat(b.getInfo(), is(not(nullValue())));
        assertThat(a.getInfo().getAppname(), is(b.getInfo().getAppname()));
        assertThat(a.getInfo().getAppversion(), is(b.getInfo().getAppversion()));
        assertThat(a.getPacks(), is(not(nullValue())));
        assertThat(b.getPacks(), is(not(nullValue())));
        assertThat(a.getPacks().getPack().size(), is(b.getPacks().getPack().size()));
        for (int i = 0; i < a.getPacks().getPack().size(); i++)
        {
            PackType packA = a.getPacks().getPack().get(i);
            PackType packB = b.getPacks().getPack().get(i);
            assertThat(packA.getDescription(), is(packB.getDescription()));
        }
        assertThat(a.getLocale(), is(not(nullValue())));
        assertThat(b.getLocale(), is(not(nullValue())));
        assertThat(a.getLocale().getLangpack().size(), is(b.getLocale().getLangpack().size()));
        for (int i = 0; i < a.getLocale().getLangpack().size(); i++)
        {
            LangpackType localeA = a.getLocale().getLangpack().get(i);
            LangpackType localeB = b.getLocale().getLangpack().get(i);
            assertThat(localeA.getIso3(), is(localeB.getIso3()));
        }
    }

    /**
     * Test that an empty fallback just removes the include and fallback
     * elements
     *
     * @throws Exception
     */
    @Test
    public void testEmptyFallback() throws Exception
    {
        doTest("empty-fallback");
    }

    /**
     * Ensure fallbacks work correctly
     *
     * @throws Exception
     */
    @Test
    public void testFallback() throws Exception
    {
        doTest("fallback");
    }

    /**
     * Test the default use case for xinclude
     *
     * @throws Exception
     */
    @Test
    public void testInclude() throws Exception
    {
        doTest("include");
    }

    /**
     * Test include text content
     *
     * @throws Exception
     */
    @Test
    public void testIncludeText() throws Exception
    {
        doTest("include-text");
    }

    /**
     * Ensure that the parse attribute accepts "text" and treats it like text
     *
     * @throws Exception
     */
    @Test
    public void testParseAttributeText() throws Exception
    {
        doTest("include-xml-as-text");
    }

    /**
     * Ensure that the parse attribute accepts "xml" and treats like xml
     * (most other tests do not explicitly set the parse parameter and let it
     * default to "xml"
     *
     * @throws Exception
     */
    @Test
    public void testParseAttributeXML() throws Exception
    {
        doTest("include-xml-as-xml");
    }

    /**
     * Make sure that a failure occurs for a parse valid that is not "xml"
     * or "text"
     *
     * @throws Exception
     */
    @Test(expected = JAXBException.class)
    public void testParseInvalidAttribute() throws Exception
    {
        parseInputForFailures("invalid-parse-attrib");
    }

    /**
     * Ensure that two includes in the same element both get included
     *
     * @throws Exception
     */
    @Test
    public void testMultipleIncludes() throws Exception
    {
        doTest("multiple-include");
    }
}
