package org.onehippo.forge.konakart.common.al;

import com.konakart.app.Customer;
import com.konakart.app.KKException;
import com.konakart.appif.AddressIf;
import com.konakart.appif.CustomerIf;
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
}
