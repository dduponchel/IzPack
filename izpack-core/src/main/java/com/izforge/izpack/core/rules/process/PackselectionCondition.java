/*
 * IzPack - Copyright 2001-2008 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2007-2009 Dennis Reil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.izforge.izpack.core.rules.process;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.api.data.Pack;
import com.izforge.izpack.api.rules.Condition;
import org.izpack.xsd.conditions.ConditionType;

import java.util.List;

/**
 * @author Dennis Reil, <izpack@reil-online.de>
 * @version $Id: PackselectionCondition.java,v 1.1 2006/11/03 13:03:26 dennis Exp $
 */
public class PackselectionCondition extends Condition
{
    private static final long serialVersionUID = 9193011814966195963L;
    protected String packid;

    /*
     * (non-Javadoc)
     *
     * @see de.reddot.installer.rules.Condition#readFromXML(com.izforge.izpack.api.adaptator.IXMLElement)
     */

    @Override
    public void readFromXML(ConditionType xmlcondition) throws Exception
    {
        this.packid = xmlcondition.getPackid();
        if(packid == null)
        {
            throw new Exception("Missing nested element in condition \"" + getId() + "\"");
        }
    }

    private boolean isTrue(List<Pack> selectedpacks)
    {
        if (selectedpacks != null)
        {
            for (Pack selectedpack : selectedpacks)
            {
                if (packid.equals(selectedpack.id))
                {
                    // pack is selected
                    return true;
                }
            }
        }
        // pack is not selected
        return false;
    }

    @Override
    public boolean isTrue()
    {
        return this.isTrue(getInstallData().getSelectedPacks());
    }

    @Override
    public String getDependenciesDetails()
    {
        StringBuffer details = new StringBuffer();
        details.append(this.getId());
        details.append("depends on the selection of pack <b>");
        details.append(this.packid);
        details.append("</b><br/>");
        return details.toString();
    }

    @Override
    public void makeXMLData(IXMLElement conditionRoot)
    {
        XMLElementImpl packel = new XMLElementImpl("packid", conditionRoot);
        packel.setContent(this.packid);
        conditionRoot.addChild(packel);
    }

    public void setPackid(String packid)
    {
        this.packid = packid;
    }
}
