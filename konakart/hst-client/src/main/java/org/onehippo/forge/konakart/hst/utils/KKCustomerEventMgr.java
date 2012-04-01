package org.onehippo.forge.konakart.hst.utils;

import com.konakart.al.KKAppEng;
import com.konakart.app.CustomerEvent;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerEventIf;
import com.konakart.appif.CustomerIf;
import com.konakart.bl.ConfigConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

public class KKCustomerEventMgr {

    public static final Logger log = LoggerFactory.getLogger(KKCustomerEventMgr.class);

    /*
    * Event actions
    */
    public static final int ACTION_NEW_CUSTOMER_VISIT = 1;

    public static final int ACTION_CUSTOMER_LOGIN = 2;

    public static final int ACTION_ENTER_CHECKOUT = 3;

    public static final int ACTION_CONFIRM_ORDER = 4;

    public static final int ACTION_PAYMENT_METHOD_SELECTED = 5;

    public static final int ACTION_REMOVE_FROM_CART = 6;

    public static final int ACTION_PRODUCT_VIEWED = 7;

    /**
     * Returns a customer event object with the action and customer id attributes populated. If
     * events aren't enabled, then null is returned.
     *
     * @param kkAppEng
     *            App eng instance
     * @param action
     *            Event action
     * @return Returns a customer event object or null if events aren't enabled
     */
    public CustomerEventIf getCustomerEvent(KKAppEng kkAppEng, int action)
    {
        String enabled = kkAppEng.getConfig(ConfigConstants.ENABLE_CUSTOMER_EVENTS);
        if (enabled != null && enabled.equalsIgnoreCase("true"))
        {
            CustomerEventIf event = new CustomerEvent();
            event.setAction(action);
            CustomerIf currentCust = kkAppEng.getCustomerMgr().getCurrentCustomer();
            if (currentCust != null)
            {
                event.setCustomerId(currentCust.getId());
            }
            return event;
        }
        return null;
    }

    /**
     * Inserts a customer event where all of the available parameters are passed
     *
     * @param kkAppEng App eng instance
     * @param action   Event action
     * @param str1 a string
     * @param str2 a string
     * @param int1 an integer
     * @param int2 an integer
     * @param dec1 a decimal
     * @param dec2 a decimal
     */
    public void insertCustomerEvent(KKAppEng kkAppEng, int action, String str1, String str2,
                                       int int1, int int2, BigDecimal dec1, BigDecimal dec2) {
        CustomerEventIf event = getCustomerEvent(kkAppEng, action);
        if (event != null) {
            event.setData1Str(str1);
            event.setData2Str(str2);
            event.setData1Int(int1);
            event.setData2Int(int2);
            event.setData1Dec(dec1);
            event.setData2Dec(dec2);
            try {
                kkAppEng.getEng().insertCustomerEvent(event);
            } catch (KKException e) {
                // unable to insert the event
                log.error("Failed to insert the event", e);
            }
        }
    }

    /**
     * Shortcut method for inserting a customer event passing no custom event data
     *
     * @param kkAppEng the konakart engine
     * @param action the action to save
     */
    public void insertCustomerEvent(KKAppEng kkAppEng, int action) {
        insertCustomerEvent(kkAppEng, action, null, null, 0, 0, null, null);
    }

    /**
     * Shortcut method for inserting a customer event passing an integer as event data
     *
     * @param kkAppEng the konakart engine
     * @param action the action to save
     * @param int1 an integer
     */
    public void insertCustomerEvent(KKAppEng kkAppEng, int action, int int1) {
        insertCustomerEvent(kkAppEng, action, null, null, int1, 0, null, null);
    }

    /**
     * Shortcut method for inserting a customer event passing a string as event data
     *
     * @param kkAppEng the konakart engine
     * @param action the action to save
     * @param str1 a string
     */
    public void insertCustomerEvent(KKAppEng kkAppEng, int action, String str1) {
        insertCustomerEvent(kkAppEng, action, str1, null, 0, 0, null, null);
    }

    /**
     * Shortcut method for inserting a customer event passing a decimal as event data
     *
     * @param kkAppEng the konakart engine
     * @param action the action to save
     * @param dec1 a decinal
     */
    public void insertCustomerEvent(KKAppEng kkAppEng, int action, BigDecimal dec1) {
        insertCustomerEvent(kkAppEng, action, null, null, 0, 0, dec1, null);
    }

}
