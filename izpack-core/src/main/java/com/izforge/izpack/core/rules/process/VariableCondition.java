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
import com.izforge.izpack.api.rules.Condition;
import com.izforge.izpack.api.substitutor.VariableSubstitutor;
import com.izforge.izpack.core.substitutor.VariableSubstitutorImpl;
import org.izpack.xsd.conditions.ConditionType;

/**
 * @author Dennis Reil, <izpack@reil-online.de>
 */
public class VariableCondition extends Condition
{

    private static final long serialVersionUID = 2881336115632480575L;

    protected String variablename;

    protected String value;

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getVariablename()
    {
        return variablename;
    }

    public void setVariablename(String variablename)
    {
        this.variablename = variablename;
    }

    @Override
    public void readFromXML(ConditionType xmlcondition) throws Exception
    {
        try
        {
            this.variablename = xmlcondition.getName();
            this.value = xmlcondition.getValue();
        }
        catch (Exception e)
        {
            throw new Exception("Missing attribute in condition \"" +  getId() + "\"");
        }

    }

    @Override
    public boolean isTrue()
    {
        if (this.getInstallData() != null)
        {
            String val = this.getInstallData().getVariable(variablename);
            if (val == null)
            {
                return false;
            }
            else
            {
                VariableSubstitutor subst = new VariableSubstitutorImpl(this.getInstallData().getVariables());
                return val.equals(subst.substitute(value));
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getDependenciesDetails()
    {
        StringBuffer details = new StringBuffer();
        details.append(this.getId());
        details.append(" depends on a value of <b>");
        details.append(this.value);
        details.append("</b> on variable <b>");
        details.append(this.variablename);
        details.append(" (current value: ");
        details.append(this.getInstallData().getVariable(variablename));
        details.append(")");
        details.append("</b><br/>");
        return details.toString();
    }

    @Override
    public void makeXMLData(IXMLElement conditionRoot)
    {
        XMLElementImpl nameEl = new XMLElementImpl("name", conditionRoot);
        nameEl.setContent(this.variablename);
        conditionRoot.addChild(nameEl);

        XMLElementImpl valueEl = new XMLElementImpl("value", conditionRoot);
        valueEl.setContent(this.value);
        conditionRoot.addChild(valueEl);
    }
}