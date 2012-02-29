package org.onehippo.forge.konakart.common.al;

import com.konakart.app.Customer;
import com.konakart.app.KKException;
import com.konakart.appif.AddressIf;
import com.konakart.appif.CustomerIf;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKEngine;

/**
 * Contains methods to manage customer details and login / logout.
 */
public class CustomerMgr extends BaseMgr {

    private CustomerIf currentCustomer;

    /**
     * Default constuctor
     * @param kkEngine the Konakart Engine
     */
    public CustomerMgr(KKEngine kkEngine) {
        super(kkEngine);
    }

    /**
     * Login to the session
     *
     * @param username the username of the user
     * @param password the password of the user
     *
     * @throws Exception if the logged-in process failed
     * @return the session id
     */
    public String login(String username, String password) throws Exception {
        /*
        * Login with default credentials
        */
        String sessionId = kkEng.login(username, password);

        if (sessionId == null) {
            String msg = "Login of " + username + " was unsuccessful";
            throw new KKException(msg);
        }

        // Set the current customer
        currentCustomer = kkEng.getCustomer(sessionId);

        // Set the session id
        kkEngine.setSessionId(sessionId);

        return sessionId;

    }

    /**
     * Logout from Konakart
     * @throws Exception if the logout process failed
     */
    public void logout() throws Exception {
        String sessionId = kkEngine.getSessionId();

        if (sessionId != null) {
            kkEng.logout(sessionId);
            kkEng.logout(sessionId);
            kkEngine.setSessionId(null);
        }
    }

    /**
     * @return the current customer
     */
    public CustomerIf getCurrentCustomer() {
        return currentCustomer;
    }

    /**
     * @return true if the customer is a guest, false otherwise
     */
    public boolean isGuestCustomer() {
        return currentCustomer != null && currentCustomer.getId() < 0;
    }


    /**
     * create a guest customer
     * @throws com.konakart.app.KKException if the creation of a guest has failed
     */
    public void createGuest() throws KKException {
        int tempCustomerId = kkEng.getTempCustomerId();

        // Reinitialize the basket
        kkEng.removeBasketItemsPerCustomer(kkEngine.getSessionId(), tempCustomerId);

        // Create the customer
        currentCustomer = new Customer();
        currentCustomer.setId(tempCustomerId);
        currentCustomer.setGlobalProdNotifier(0);
    }

    /**
     *  Ensures that the currentCustomer object has his default address and array of addresses populated
     *
     * @param force If set to true the addresses will be refreshed even if they already exist
     * @return the customer with populated addresses
     *
     * @throws KKException .
     */

    public CustomerIf populateCurrentCustomerAddresses(boolean force) throws KKException {
        if (this.currentCustomer == null) {
            throw new KKException("The user is not logged in");
        }

        if ((force) || (currentCustomer.getAddresses() == null) || (currentCustomer.getDefaultAddr() == null)) {
            AddressIf[] listOfAddresses = kkEng.getAddressesPerCustomer(kkEngine.getSessionId());

            if ((listOfAddresses == null) || (listOfAddresses.length == 0)) {
                throw new KKException("The current user has no addresses set. All registered users should have at least one address set.");
            }

            this.currentCustomer.setDefaultAddr(listOfAddresses[0]);
            this.currentCustomer.setAddresses(listOfAddresses);
        }

        return currentCustomer;
    }

    /**
     * Normally called after a login to get and cache customer relevant data such as the customer's basket,
     * the customer's orders and the customer's order history.
     *
     * If this method isn't called, then the UI will not show updated data.
     *
     * @throws KKException .
     */
    public void refreshCustomerCachedData() throws KKException {
        kkEngine.getBasketMgr().getBasketItemsPerCustomer();
        kkEngine.getOrderMgr().populateCustomerOrders();
        kkEngine.getProductMgr().fetchOrderHistoryArray();
        String enableWhishList = kkEngine.getConfig(ConfigConstants.ENABLE_WISHLIST);
        if (!StringUtils.isEmpty(enableWhishList) && (StringUtils.equalsIgnoreCase(enableWhishList, "true"))) {
            kkEngine.getWishListMgr().fetchCustomersWishLists();
        }
        kkEngine.getOrderMgr().setCouponCode(null);
        kkEngine.getOrderMgr().setGiftCertCode(null);
        kkEngine.getOrderMgr().setRewardPoints(0);
    }
}
