package org.onehippo.cms7.hst.hippokart.gogreen.database.helper;

import com.konakartadmin.app.*;
import com.konakartadmin.bl.AdminCustomerMgr;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminCustomerMgrIf;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import com.konakartadmin.blif.AdminReviewMgrIf;
import com.konakartadmin.blif.AdminSecurityMgrIf;
import org.apache.torque.TorqueException;
import org.hippoecm.repository.PasswordHelper;
import org.onehippo.cms7.hst.hippokart.gogreen.database.utils.LanguageUtil;

import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;

/**
 *
 */
public class ReviewHelper {

    private static AdminReviewMgrIf adminReviewMgr;
    private static AdminCustomerMgrIf adminCustMgr;
    private static AdminSecurityMgrIf adminSecMgr;
    private static AdminLanguage[] adminLanguages;


    private int productId;
    private Calendar dateAdded;
    private String reviewText;
    private int rating;
    private String customerEmail;
    private String customerName;

    public static void setAdminMgrFactory(AdminMgrFactory adminMgrFactory) throws Exception {
        adminReviewMgr = adminMgrFactory.getAdminReviewMgr(false);
        adminCustMgr = adminMgrFactory.getAdminCustMgr(false);
        adminSecMgr = adminMgrFactory.getAdminSecMgr(false);

        AdminLanguageMgrIf adminLanguageMgr = adminMgrFactory.getAdminLanguageMgr(true);
        adminLanguages = adminLanguageMgr.getAllLanguages();

    }

    public void process() {

        // Check if the user exists. If not a customer will be added.
        try {
            int customerId = -1;

            if (adminCustMgr.doesCustomerExistForEmail(customerEmail)) {
                customerId = adminCustMgr.getCustomerForEmail(customerEmail).getId();
            } else {
                customerId = createNewCustomer();
            }

            AdminReview adminReview = new AdminReview();

            adminReview.setCustomerId(customerId);
            adminReview.setProductId(productId);
            adminReview.setDateAdded(dateAdded.getTime());
            adminReview.setReviewText(reviewText);
            adminReview.setRating(rating);
            adminReview.setStatus(0); // 0 makes the review visible
            adminReview.setLanguageId(LanguageUtil.getLanguageId("en_US", adminLanguages));

            adminReviewMgr.insertReview(adminReview);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private int createNewCustomer() throws Exception {

        AdminCustomerRegistration adminCustomer = new AdminCustomerRegistration();

        String[] fullname = customerName.split(" ");

        if (fullname.length > 1) {
            adminCustomer.setFirstName(fullname[0]);
            adminCustomer.setLastName(fullname[1]);
        } else {
            adminCustomer.setFirstName(customerName);
            adminCustomer.setLastName(".");
        }
        adminCustomer.setTelephoneNumber("555-555-5555");
        adminCustomer.setPassword("password");

        adminCustomer.setGender("m");
        adminCustomer.setEmailAddr(customerEmail);
        adminCustomer.setBirthDate(new Date());
        adminCustomer.setStreetAddress("TBD");
        adminCustomer.setPostcode("TBD");
        adminCustomer.setCity("Amsterdam");
        adminCustomer.setCountryId(150);
        adminCustomer.setState("ZE");
        adminCustomer.setInvisible(false);
        adminCustomer.setNewsletter("1");

        return adminCustMgr.registerCustomer(adminCustomer);
    }


    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setDateAdded(Calendar dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
