package org.onehippo.forge.konakart.hst.wizard.checkout.activity;

import com.konakart.app.KKException;
import com.konakart.appif.AddressIf;
import com.konakart.appif.ZoneIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.util.ArrayList;
import java.util.List;

import static org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils.*;
import static org.onehippo.forge.konakart.hst.utils.KKUtil.checkMandatoryField;

public abstract class BaseAddressActivity extends BaseCheckoutActivity {

  protected KKRegisterFormUtils registerFormUtils = new KKRegisterFormUtils();

  @Override
  public void doBeforeRender() throws ActivityException {

    if (!validateCurrentCart()) {
      return;
    }

    CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

    try {
      seedData.getRequest().setAttribute(COUNTRIES,
          KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getAllCountries());

      // Set the province if has been selected
      FormField countryField = formMap.getField(COUNTRY);

      if (countryField != null) {

        int country = StringUtils.isNotBlank(countryField.getValue()) ? Integer.valueOf(countryField.getValue()) : -1;
        ZoneIf[] zones = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getEng().getZonesPerCountry(country);

        List<String> stateProvinces = new ArrayList<String>();

        for (ZoneIf zone : zones) {
          stateProvinces.add(zone.getZoneName());
        }

        seedData.getRequest().setAttribute(PROVINCES, stateProvinces);
      }
    } catch (KKException e) {
      log.error("Failed to retrieve the list of provinces - {} ", e.toString());
    }

    // Initialize the list of addresses already created.
    if (!seedData.getKkBaseHstComponent().isGuestCustomer(hstRequest)) {
      try {
        KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().populateCurrentCustomerAddresses(/* force */ false);

        AddressIf[] addresses = KKServiceHelper.getKKEngineService().getKKAppEng(hstRequest).getCustomerMgr().getCurrentCustomer().getAddresses();

        seedData.getRequest().setAttribute(ADDRESSES, addresses);

      } catch (Exception e) {
        log.warn("Failed to load customer addresses - {}", e.toString());
      }
    }
  }


  @Override
  public boolean doValidForm() {
    super.doValidForm();

    CheckoutSeedData seedData = (CheckoutSeedData) processorContext.getSeedData();

    boolean result = true;

    String errorMessage = seedData.getBundleAsString("checkout.mandatory.field");

    if (seedData.getAction().equals(KKActionsConstants.ACTIONS.SELECT.name())) {

      String addressId = formMap.getField(ADDRESS).getValue();

      if (StringUtils.isNotEmpty(addressId) && !"-1".equals(addressId)) {
        return result;
      }

      result = checkMandatoryField(formMap, GENDER, errorMessage);
      result = result & checkMandatoryField(formMap, FIRSTNAME, errorMessage);
      result = result & checkMandatoryField(formMap, LASTNAME, errorMessage);
      result = result & checkMandatoryField(formMap, STREETADDRESS, errorMessage);
      result = result & checkMandatoryField(formMap, POSTALCODE, errorMessage);
      result = result & checkMandatoryField(formMap, CITY, errorMessage);
      result = result & checkMandatoryField(formMap, STATEPROVINCE, errorMessage);
      result = result & checkMandatoryField(formMap, COUNTRY, errorMessage);

      if (isCheckoutAsRegister() || isCheckoutAsGuest()) {
        result = result & checkMandatoryField(formMap, EMAIL, errorMessage);
        result = result & checkMandatoryField(formMap, DATEOFBIRTH, errorMessage);

        // Valid if the a customer has already this email
        String emailAddress = formMap.getField(EMAIL).getValue();

        try {
          // Check if the email already exists
          if (kkAppEng.getEng().doesCustomerExistForEmail(emailAddress)) {

            String password = formMap.getField(PASSWORD).getValue();

            // Try to logged-in
            boolean isLoggedIn = KKServiceHelper.getKKEngineService().logIn(hstRequest, hstResponse, emailAddress, password);

            if (!isLoggedIn) {
              // login has failed
              result = false;
              addMessage(GLOBALMESSAGE, seedData.getBundleAsString("checkout.email.alreadyexits.field"));
            }
          }
        } catch (KKException e) {
          log.error("Failed to validate if the email set during the checkout process already exists.", e);
        }
      }

      if (isCheckoutAsRegister()) {
        result = result & checkMandatoryField(formMap, PASSWORD, errorMessage);
        result = result & checkMandatoryField(formMap, PASSWORD_CONFIRMATION, errorMessage);
      }

    }

    return result;
  }


  /**
   * This method must be overrided if you implement a different onepagecheckout
   *
   * @return the onePageCheckout formMapFields
   */
  public List<String> getCheckoutFormMapFields() {
    return PARAMS;
  }


}
