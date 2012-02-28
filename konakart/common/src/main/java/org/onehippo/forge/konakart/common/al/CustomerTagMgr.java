package org.onehippo.forge.konakart.common.al;

import com.konakart.app.CustomerTag;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerTagIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKEngine;

public class CustomerTagMgr extends BaseMgr {

    private boolean enabled;

    /**
     * Default constuctor
     *
     * @param kkEngine the Konakart Engine
     */
    public CustomerTagMgr(KKEngine kkEngine) {
        super(kkEngine);
    }


    /**
     * A CustomerTag object is returned containing the value of the customer tag referenced by the parameter tagName
     * for the logged in customer or guest customer.
     *
     * @param tagName The name of the customer tag
     *
     * @return Returns the CustomerTag object
     * @throws com.konakart.app.KKException .
     */
    public CustomerTagIf getCustomerTag(String tagName) throws KKException {

        if (!isEnabled() || StringUtils.isEmpty(tagName)) {
            return null;
        }

        if (kkEngine.getCustomerMgr().isGuestCustomer()) {
            return kkEng.getCustomerTagForGuest(kkEngine.getCustomerMgr().getCurrentCustomer().getId(), tagName);
        }

        return kkEng.getCustomerTag(kkEngine.getSessionId(), tagName);
    }


    /**
     * Sets the tag for the logged in customer or guest customer.
     *
     * @param tag The customer tag with populated tagName and tagValue attributes
     * @throws com.konakart.app.KKException .
     */
    public void insertCustomerTag(CustomerTag tag) throws KKException {
        if (!isEnabled()) {
            return;
        }

        if (kkEngine.getCustomerMgr().isGuestCustomer()) {
            kkEng.insertCustomerTagForGuest(kkEngine.getCustomerMgr().getCurrentCustomer().getId(), tag);
        }


        kkEng.insertCustomerTag(kkEngine.getSessionId(), tag);
    }

    /**
     * Sets the tagValue for the tag called tagName for the logged in customer or guest customer.
     *
     * @param tagName The name of the customer tag
     * @param tagValue The value of the customer tag for this customer
     * @throws com.konakart.app.KKException .
     */
    public void insertCustomerTag(String tagName, String tagValue) throws KKException {
        if (!isEnabled()) {
            return;
        }

        if (!StringUtils.isEmpty(tagName)) {
            CustomerTag customerTag = new CustomerTag();
            customerTag.setName(tagName);
            customerTag.setValue(tagValue);

            insertCustomerTag(customerTag);
        }
    }



    /**
     *
     * @return
     */
    public boolean isEnabled() {
        String config =  kkEngine.getConfig("ENABLE_CUSTOMER_TAGS");

        return (!StringUtils.isEmpty(config) && StringUtils.equals(config, "true"));
    }

}
