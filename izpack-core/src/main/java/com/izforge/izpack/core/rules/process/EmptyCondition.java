/*
 * IzPack - Copyright 2001-2009 Julien Ponge, All Rights Reserved.
 *
 * http://izpack.org/
 * http://izpack.codehaus.org/
 *
 * Copyright 2009 Dennis Reil
 * Copyright 2010 Rene Krell
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

import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.adaptator.impl.XMLElementImpl;
import com.izforge.izpack.api.rules.Condition;
import com.izforge.izpack.core.substitutor.VariableSubstitutorBase;
import com.izforge.izpack.core.substitutor.VariableSubstitutorImpl;
import com.izforge.izpack.util.Debug;
import org.izpack.xsd.conditions.ConditionType;

/**
 * This condition checks if a certain type is empty
 */
public class EmptyCondition extends Condition
{
    private static final long serialVersionUID = -4626906695822723409L;

    private ContentType contentType;
    private String content;

    public EmptyCondition() {}

    @Override
    public boolean isTrue()
    {
        boolean result = false;
        VariableSubstitutorBase subst = new VariableSubstitutorImpl(this.getInstallData().getVariables());
        switch (contentType)
        {
            case STRING:
                if (this.content == null)
                {
                    return true;
                }
                String s = subst.substitute(this.content);
                if (s != null && s.length()==0)
                {
                    result = true;
                }
                break;

            case VARIABLE:
                if (this.content != null)
                {
                    String value = this.getInstallData().getVariable(this.content);
                    if (value != null && value.length()==0)
                    {
                        result = true;
                    }
                }
                break;

            case FILE:
                if (this.content != null)
                {
                    File file = new File(subst.substitute(this.content));
                    if (!file.exists() && file.length()==0)
                    {
                        result = true;
                    }
                }
                break;

            case DIR:
                if (this.content != null)
                {
                    File file = new File(subst.substitute(this.content));
                    if (!file.exists() || file.isDirectory() && file.listFiles().length==0)
                    {
                        result = true;
                    }
                }
                break;

            default:
                Debug.error("Illegal content type '"+contentType.getAttribute()+"' of ExistsCondition");
                break;
        }
        return result;
    }

    @Override
    public void readFromXML(ConditionType xmlcondition) throws Exception
    {
        if (xmlcondition != null)
        {
            int count = 0;
            if (xmlcondition.getString() != null)
            {
                count++;
                content = xmlcondition.getString();
                contentType = ContentType.STRING;
            }
            if (xmlcondition.getVariable() != null)
            {
                count++;
                content = xmlcondition.getVariable();
                contentType = ContentType.VARIABLE;
            }
            if (xmlcondition.getFile() != null)
            {
                count++;
                content = xmlcondition.getFile();
                contentType = ContentType.FILE;
            }
            if (xmlcondition.getDir() != null)
            {
                count++;
                content = xmlcondition.getDir();
                contentType = ContentType.DIR;
            }
            if (count != 1)
            {
                throw new Exception("Condition \"" + getId() + "\" needs exactly one nested element");
            }
            if (this.content == null || this.content.length() == 0)
            {
                throw new Exception("Condition \"" + getId() + "\" has a nested element without valid contents");
            }
        }
    }

    public ContentType getContentType()
    {
        return contentType;
    }


    public void setContentType(ContentType contentType)
    {
        this.contentType = contentType;
    }


    public String getContent()
    {
        return content;
    }


    public void setContent(String content)
    {
        this.content = content;
    }

    @Override
    public void makeXMLData(IXMLElement conditionRoot)
    {
        XMLElementImpl el = new XMLElementImpl(this.contentType.getAttribute(), conditionRoot);
        el.setContent(this.content);
        conditionRoot.addChild(el);
    }

    public enum ContentType
    {
        VARIABLE("variable"), STRING("string"), FILE("file"), DIR("dir");

        private static Map<String, ContentType> lookup;

        private String attribute;

        ContentType(String attribute)
        {
            this.attribute = attribute;
        }

        static
        {
            lookup = new HashMap<String, ContentType>();
            for (ContentType operation : EnumSet.allOf(ContentType.class))
            {
                lookup.put(operation.getAttribute(), operation);
            }
        }

        public String getAttribute()
        {
            return attribute;
        }

        public static ContentType getFromAttribute(String attribute)
        {
            if (attribute != null && lookup.containsKey(attribute))
            {
                return lookup.get(attribute);
            }
            return null;
        }
    }
}
