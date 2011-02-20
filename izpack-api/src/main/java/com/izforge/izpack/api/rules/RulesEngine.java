package com.izforge.izpack.api.rules;

import com.izforge.izpack.api.adaptator.IXMLElement;
import org.izpack.xsd.installation.ConditionType;
import org.izpack.xsd.installation.Conditions;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Properties;

/**
 * Interface of rulesEngine
 */
public interface RulesEngine extends Serializable
{
    String[] getKnownConditionIds();

    boolean isConditionTrue(String id, Properties variables);

    boolean isConditionTrue(Condition cond, Properties variables);

    boolean isConditionTrue(String id);

    boolean isConditionTrue(Condition cond);

    boolean canShowPanel(String panelid, Properties variables);

    boolean canInstallPack(String packid, Properties variables);

    boolean canInstallPackOptional(String packid, Properties variables);

    void addCondition(Condition condition);

    void writeRulesXML(OutputStream out);

    Condition getCondition(String id);

    void readConditionMap(Map<String, Condition> rules);

    void analyzeXml(Conditions conditionsspec);

    Condition instanciateCondition(ConditionType condition);

    IXMLElement createConditionElement(Condition condition, IXMLElement root);
}
