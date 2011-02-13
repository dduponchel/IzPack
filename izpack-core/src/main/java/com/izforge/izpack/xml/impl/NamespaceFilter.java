package com.izforge.izpack.xml.impl;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Add a default namespace to avoid problems with old xml.
 * Thanks stackoverflow :D
 * http://stackoverflow.com/questions/277502/jaxb-how-to-ignore-namespace-during-unmarshalling-xml-document
 */
public class NamespaceFilter extends XMLFilterImpl
{

    private String usedNamespaceUri;
    private boolean addNamespace;

    //State variable
    private boolean addedNamespace = false;

    public NamespaceFilter(String namespaceUri,
                           boolean addNamespace)
    {
        if (addNamespace)
        {
            this.usedNamespaceUri = namespaceUri;
        }
        else
        {
            this.usedNamespaceUri = "";
        }
        this.addNamespace = addNamespace;
    }


    @Override
    public void startDocument() throws SAXException
    {
        super.startDocument();
        if (addNamespace)
        {
            startControlledPrefixMapping();
        }
    }

    @Override
    public void startPrefixMapping(String prefix, String url)
            throws SAXException
    {


        if (addNamespace)
        {
            this.startControlledPrefixMapping();
        }
        else
        {
            //Remove the namespace, i.e. don´t call startPrefixMapping for parent!
        }

    }

    private void startControlledPrefixMapping() throws SAXException
    {

        if (this.addNamespace && !this.addedNamespace)
        {
            //We should add namespace since it is set and has not yet been done.
            super.startPrefixMapping("", this.usedNamespaceUri);

            //Make sure we dont do it twice
            this.addedNamespace = true;
        }
    }

}
