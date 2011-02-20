package com.izforge.izpack.installer.container.provider;

import com.izforge.izpack.api.data.ResourceManager;
import com.izforge.izpack.api.xml.IXmlReader;
import com.izforge.izpack.api.xml.impl.JaxbXmlReader;
import com.izforge.izpack.gui.IconsDatabase;
import com.izforge.izpack.installer.base.InstallerFrame;
import com.izforge.izpack.util.Debug;
import org.izpack.xsd.icons.IconType;
import org.izpack.xsd.icons.Icons;
import org.picocontainer.injectors.Provider;

import javax.swing.ImageIcon;
import javax.swing.UIManager;
import java.io.InputStream;
import java.net.URL;

/**
 * Provide icons database
 */
public class IconsProvider implements Provider
{

    /**
     * Resource name for custom icons
     */
    private static final String CUSTOM_ICONS_RESOURCEFILE = "customicons.xml";

    public IconsDatabase provide(ResourceManager resourceManager) throws Exception
    {
        IconsDatabase icons = new IconsDatabase();
        loadIcons(icons);
        loadCustomIcons(icons, resourceManager);
        return icons;
    }

    /**
     * Loads the icons.
     *
     * @param iconsDatabase
     * @throws Exception Description of the Exception
     */
    private void loadIcons(IconsDatabase iconsDatabase) throws Exception
    {
        // Initialisations
        InputStream inXML = getClass().
                getResourceAsStream("icons.xml");

        parseXML(inXML, iconsDatabase);
    }

    /**
     * Loads custom icons into the installer.
     *
     * @throws Exception
     */
    private void loadCustomIcons(IconsDatabase icons, ResourceManager resourceManager) throws Exception
    {
        // We try to load and add a custom langpack.
        InputStream inXML = null;
        try
        {
            inXML = resourceManager.getInputStream(CUSTOM_ICONS_RESOURCEFILE);
        }
        catch (Throwable exception)
        {
            Debug.trace("Resource " + CUSTOM_ICONS_RESOURCEFILE
                    + " not defined. No custom icons available.");
            return;
        }
        Debug.trace("Custom icons available.");

        parseXML(inXML, icons);
    }

    /**
     * parse the xml and fill in the db
     *
     * @param inXML
     * @param icons
     */
    private void parseXML(InputStream inXML, IconsDatabase icons)
    {
        URL url;
        ImageIcon img;// Initialises the parser

        // TODO use ioc
        IXmlReader xmlReader = new JaxbXmlReader();

        // We get the data
        Icons xmlIcons = xmlReader.readIcons(inXML);

        // We load the icons
        for (IconType icon : xmlIcons.getIcon())
        {
            url = InstallerFrame.class.getResource(icon.getRes());
            img = new ImageIcon(url);
            Debug.trace("Icon with id found: " + icon.getId());
            icons.put(icon.getId(), img);
        }

        // We load the Swing-specific icons
        for (IconType icon : xmlIcons.getSysicon())
        {
            url = InstallerFrame.class.getResource(icon.getRes());
            img = new ImageIcon(url);
            UIManager.put(icon.getId(), img);
        }
    }

}
