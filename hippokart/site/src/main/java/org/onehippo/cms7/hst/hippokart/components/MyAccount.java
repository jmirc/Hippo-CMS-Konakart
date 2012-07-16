package org.onehippo.cms7.hst.hippokart.components;

import com.konakart.appif.CustomerRegistrationIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.components.KKMyAccount;
import org.onehippo.forge.konakart.hst.utils.KKRegisterFormUtils;

import java.util.List;

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
    protected void doBeforeRenderNotLoginAction(HstRequest request, HstResponse response) {
        if (isGuestCustomer(request)) {
            response.setRenderPath("jsp/myaccount/main/login.jsp");
        }
    }

    @Override
    protected void deBeforeRenderRegisterAction(HstRequest request, HstResponse response) {
        response.setRenderPath("jsp/myaccount/main/register.jsp");
    }

    @Override
    protected void doBeforeRenderCreateAction(HstRequest request, HstResponse response) {

    }

    @Override
    protected void addAdditionalInformationToCustomerRegistration(CustomerRegistrationIf customerRegistration,
                                                                  FormMap formMap) {

        FormField formField = formMap.getField(CUSTOM_CUSTOMER_1);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setCustomerCustom1(formField.getValue());
        }

        formField = formMap.getField(CUSTOM_ADDRESS_1);
        if ((formField != null) && StringUtils.isNotBlank(formField.getValue())) {
            customerRegistration.setAddressCustom1(formField.getValue());
        }
    }
}
