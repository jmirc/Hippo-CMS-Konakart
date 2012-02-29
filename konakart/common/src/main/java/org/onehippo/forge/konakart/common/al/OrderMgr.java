package org.onehippo.forge.konakart.common.al;

import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKEngine;

import static com.konakart.bl.ConfigConstants.*;

public class OrderMgr extends BaseMgr {

    private int maxRows;
    private int maxPageLinks;
    private boolean sendEmails = false;
    private boolean sendOrderConfEmails = false;

    private String couponCode;
    private int rewardPoints;
    private String giftCertCode;


    /**
     * Default constuctor
     *
     * @param kkEngine the Konakart Engine
     */
    public OrderMgr(KKEngine kkEngine) {
        super(kkEngine);

        loadConfigs();
    }


    /**
     * Populates the currentCustomer object with the latest orders made.
     */
    public void populateCustomerOrders() {

    }


    public int getMaxRows() {
        return maxRows;
    }

    public int getMaxPageLinks() {
        return maxPageLinks;
    }

    public boolean isSendEmails() {
        return sendEmails;
    }

    public boolean isSendOrderConfEmails() {
        return sendOrderConfEmails;
    }


    public void loadConfigs() {

        if (kkEngine.getConfig(MAX_DISPLAY_PAGE_LINKS) != null) {
            maxPageLinks = new Integer(kkEngine.getConfig(MAX_DISPLAY_PAGE_LINKS));
        } else {
            maxPageLinks = 5;
        }

        if (kkEngine.getConfig(MAX_DISPLAY_ORDER_HISTORY) != null) {
            maxRows = new Integer(kkEngine.getConfig(MAX_DISPLAY_ORDER_HISTORY));
        } else {
            maxRows = 10;
        }

        String cSendEmails = kkEngine.getConfig(SEND_EMAILS);
        if (!StringUtils.isEmpty(cSendEmails) && (cSendEmails.equalsIgnoreCase("true"))) {
            sendEmails = true;
        }

        String cSendOrderConfEmail = kkEngine.getConfig(SEND_ORDER_CONF_EMAIL);
        if (!StringUtils.isEmpty(cSendOrderConfEmail) && (cSendOrderConfEmail.equalsIgnoreCase("true"))) {
            sendOrderConfEmails = true;
        }

    }

    /**
     * Latest coupon code entered by the customer
     *
     * @return the coupon code
     */
    public String getCouponCode() {
        return couponCode;
    }

    /**
     * Latest coupon code entered by the customer
     *
     * @param couponCode the couponCode to set
     */
    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    /**
     * Reward points entered by customer
     *
     * @return  the rewardPoints
     */
    public int getRewardPoints() {
        return this.rewardPoints;
    }

    /**
     * Reward points entered by customer
     *
     * @param rewardPoints the rewardPoints to set
     */
    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }

    /**
     * @return the giftCertCode
     */
    public String getGiftCertCode() {
        return this.giftCertCode;
    }

    /**
     * @param giftCertCode the giftCertCode to set
     */
    public void setGiftCertCode(String giftCertCode) {
        this.giftCertCode = giftCertCode;
    }
}
