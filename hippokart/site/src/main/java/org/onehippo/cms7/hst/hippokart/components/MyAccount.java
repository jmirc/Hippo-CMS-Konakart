package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.app.KKException;
import com.konakart.appif.CustomerRegistrationIf;
import com.konakart.appif.ZoneIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKMyAccount;
import org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import java.util.ArrayList;
import java.util.List;

import static org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils.COUNTRIES;
import static org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils.COUNTRY;
import static org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils.PROVINCES;

public class MyAccount extends KKMyAccount {

    public static final String CUSTOM_CUSTOMER_1 = "customCustomer1";
    public static final String CUSTOM_ADDRESS_1 = "customAddress1";

    @Override
    protected List<String> getCreateFormMapFields() {

        List<String> formMapFields = KKRegisterFormUtils.PARAMS;
        formMapFields.add(CUSTOM_CUSTOMER_1);
        formMapFields.add(CUSTOM_ADDRESS_1);

        return formMapFields;
    }

    @Override
    protected void doBeforeRenderNotLoginAction(HstRequest request, HstResponse response, FormMap formMap) {
        if (isGuestCustomer(request)) {
            response.setRenderPath("jsp/myaccount/main/login.jsp");
        }
    }

    @Override
    protected void deBeforeRenderRegisterAction(HstRequest request, HstResponse response, FormMap formMap) {
        try {
            request.setAttribute(COUNTRIES, KKServiceHelper.getKKEngineService().getKKAppEng(request).getAllCountries());

            // Set the province if has been selected
            FormField countryField = formMap.getField(COUNTRY);

            if (countryField != null) {

                int country = StringUtils.isNotBlank(countryField.getValue()) ? Integer.valueOf(countryField.getValue()) : -1;
                ZoneIf[] zones = KKServiceHelper.getKKEngineService().getKKAppEng(request).getEng().getZonesPerCountry(country);

                List<String> stateProvinces = new ArrayList<String>();

                for (ZoneIf zone : zones) {
                    stateProvinces.add(zone.getZoneName());
                }

                request.setAttribute(PROVINCES, stateProvinces);
            }
        } catch (KKException e) {
            log.error("Failed to retrieve the list of provinces - {} ", e.toString());
        }

        response.setRenderPath("jsp/myaccount/main/register.jsp");
    }

    @Override
    protected void doBeforeRenderCreateAction(HstRequest request, HstResponse response, FormMap formMap) {

    }

    @Override
    protected void doCallBeforeRegisterCustomer(CustomerRegistrationIf customerRegistration, FormMap formMap) {
        FormField formField = formMap.getField(CUSTOM_CUSTOMER_1);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCustomerCustom1(formField.getValue());
        }

        formField = formMap.getField(CUSTOM_ADDRESS_1);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setAddressCustom1(formField.getValue());
        }
    }

    @Override
    protected void doCallAfterRegisterCustomer(HstRequest request, HstResponse response, int customerId) {

    }

    @Override
    protected void doCallAfterLoginAction(HstRequest request, HstResponse response) {

    }
}
