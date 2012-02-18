package org.onehippo.forge.konakart.replication.config;

import java.util.HashMap;
import java.util.Map;

public class HippoRepoConfig {

    /**
     * This map contains for each locale defined into Konakart, some Hippo attributes
     *
     *  Example:
     *    <bean class="org.onehippo.forge.konakart.replication.config.HippoRepoConfig">
     *      <property name="localeContentRootAssociation">
     *          <map>
     *              <entry key="en_US" value-ref="englishUsStore"/>
     *          </map>
     *      </property>
     *    </bean>
     *
     *    <bean id="englishUsStore" class="org.onehippo.forge.konakart.replication.config.HippoKonakartMapping">
     *       <property name="hippoContentRoot" value="/content/documents/myhippoproject/Products"/>
     *    </bean>
     */
    private Map<String, HippoKonakartMapping> localeContentRootAssociation = new HashMap<String, HippoKonakartMapping>();

    public void setLocaleContentRootAssociation(Map<String, HippoKonakartMapping> localeContentRootAssociation) {
        this.localeContentRootAssociation = localeContentRootAssociation;
    }

    public String getStoreId(String localeName) {
        if (localeContentRootAssociation.containsKey(localeName)) {
            return localeContentRootAssociation.get(localeName).getStoreId();
        }

        return null;
    }

    public String getContentRoot(String localeName) {
        if (localeContentRootAssociation.containsKey(localeName)) {
            return localeContentRootAssociation.get(localeName).getHippoContentRoot();
        }

        return null;
    }

    public String getContentId(String localeName) {
        if (localeContentRootAssociation.containsKey(localeName)) {
            return localeContentRootAssociation.get(localeName).getCatalogId();
        }

        return null;
    }
}
