/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppEng;
import com.konakart.appif.BasketIf;
import com.konakart.appif.CustomerIf;
import com.konakart.bl.ConfigConstants;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.util.HstResponseUtils;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.Processor;
import org.onehippo.forge.konakart.hst.wizard.checkout.CheckoutSeedData;

/**
 * This component is used to manage the checkout process.
 */
public class KKCheckout extends KKHstActionComponent {

    public static final String ONE_PAGE_CHECKOUT = "onePageCheckout";
    public static final String ALLOW_CHECKOUT_WITHOUT_REGISTRATION = "allowCheckoutWithoutRegistration";
    public static final String CHECKOUT_ORDER = "checkoutOrder";

    @Override
    public final void doBeforeRender(HstRequest request, HstResponse response) throws HstComponentException {
        super.doBeforeRender(request, response);

        KKAppEng kkAppEng = getKKAppEng(request);

        try {
            CheckoutSeedData seedData = new CheckoutSeedData();
            seedData.setKkBaseHstComponent(this);
            seedData.setRequest(request);
            seedData.setResponse(response);

            Processor processor = getProcessor();

            processor.doBeforeRender(seedData);
            processor.doAdditionalData(seedData);


            request.getRequestContext().setAttribute(CHECKOUT_ORDER, kkAppEng.getOrderMgr().getCheckoutOrder());
            request.setAttribute(CHECKOUT_ORDER, kkAppEng.getOrderMgr().getCheckoutOrder());

            request.setAttribute(ONE_PAGE_CHECKOUT, isOnePageCheckout(kkAppEng));
            request.setAttribute(ALLOW_CHECKOUT_WITHOUT_REGISTRATION, kkAppEng.getConfigAsBoolean(ConfigConstants.ALLOW_CHECKOUT_WITHOUT_REGISTRATION, false));

        } catch (Exception e) {
            log.warn("Failed to initialize the checkout page", e);
            throw new HstComponentException("Failed to initialize the checkout page ",  e);
        }
    }

    @Override
    public final void doAction(String action, HstRequest request, HstResponse response) {

        CheckoutSeedData seedData = new CheckoutSeedData();
        seedData.setKkBaseHstComponent(this);
        seedData.setRequest(request);
        seedData.setResponse(response);
        seedData.setAction(action);

        Processor processor = getProcessor();

        try {
            FormMap formMap = processor.doAction(seedData);
            FormUtils.persistFormMap(request, response, formMap, null);
        } catch (ActivityException e) {
            log.error("Unable to call doAction on the processor", e);
        }
    }


    /**
     * Returns true if configured for one page checkout
     *
     * @return Returns true if configured for one page checkout
     */
    protected boolean isOnePageCheckout(KKAppEng kkAppEng) {
        // Check to see whether one page checkout is configured
        boolean onePageCheckout = kkAppEng.getConfigAsBoolean(ConfigConstants.ONE_PAGE_CHECKOUT, false);

        // Check if the customer can checkout as a guest
        boolean allowCheckoutWithoutRegistration = kkAppEng.getConfigAsBoolean(ConfigConstants.ALLOW_CHECKOUT_WITHOUT_REGISTRATION, false);

        return onePageCheckout && allowCheckoutWithoutRegistration;
    }

    /**
     * Retrieve the processor associated with the checkout
     * @return the processor
     */
    private Processor getProcessor() {

        String processorClass = HippoModuleConfig.getConfig().getCheckoutConfig().getProcessorClass();

        if (StringUtils.isNotBlank(processorClass)) {
            try {
                return (Processor) Class.forName(processorClass).newInstance();

            } catch (InstantiationException e) {
                log.error("Unable to find the extension class: " + e.toString());
            } catch (IllegalAccessException e) {
                log.error("Unable to find the extension class: " + e.toString());
            } catch (ClassNotFoundException e) {
                log.error("Unable to find the extension class: " + e.toString());
            }
        }

        throw new InstantiationError("Unable to create an instance of the class : " + processorClass);
    }


}
