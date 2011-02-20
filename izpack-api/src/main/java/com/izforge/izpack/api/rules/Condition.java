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

package com.izforge.izpack.api.rules;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.data.AutomatedInstallData;
import org.izpack.xsd.installation.ConditionType;

import java.io.Serializable;

/**
 * Abstract base class for all conditions. Implementations of custom conditions
 * have to derive from this class.
 *
 * @author Dennis Reil, <izpack@reil-online.de>
 */
/**
 * @author rkrell
 *
 */
public abstract class Condition implements Serializable
{

    private static final long serialVersionUID = 507592103321711123L;

    private String id;

    private AutomatedInstallData installdata;

    public Condition()
    {
        this.setId("UNKNOWN");
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return this.id;
    }


    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Parse and initialize this condition from parsed values. An exception
     * should be thrown if the condition description has not the expected
     * XML format, something missing or obviously bad values.
     * @param xmlCondition the condition to parse
     * @throws Exception on a parse error
     */
    public abstract void readFromXML(ConditionType xmlCondition) throws Exception;

    public abstract boolean isTrue();

    public AutomatedInstallData getInstallData()
    {
        return installdata;
    }


    public void setInstalldata(AutomatedInstallData installdata)
    {
        this.installdata = installdata;
    }

    public String getDependenciesDetails()
    {
        return "No dependencies for this condition.";
    }

    /**
     * This element will be called by the RulesEngine to serialize the configuration
     * of a condition into XML.
     *
     * @param conditionRoot the root element for this condition
     */
    public abstract void makeXMLData(IXMLElement conditionRoot);
}
