package com.izforge.izpack.xml;

import org.xml.sax.SAXException;

import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * Tie together the xsd and the namespace.
 */
public enum XmlSchema
{
    INSTALLATION("/org/izpack/xsd/installation.xsd", "http://izpack.org/xsd/installation"),
    ICONS("/org/izpack/xsd/icons.xsd", "http://izpack.org/xsd/icons");

    SchemaFactory schemaFactory;

    {
        schemaFactory = SchemaFactory.newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
    }

    private final String xsd;
    private final String namespace;
    private final Schema schema;

    private XmlSchema(String xsd, String namespace)
    {
        this.xsd = xsd;
        this.namespace = namespace;
        try
        {
            this.schema = schemaFactory.newSchema(this.getClass().getResource(xsd));
        }
        catch (SAXException e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getXsd()
    {
        return xsd;
    }

    public Schema getSchema()
    {
        return schema;
    }
}
