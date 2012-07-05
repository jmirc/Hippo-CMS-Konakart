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

package org.onehippo.forge.konakart.hst.wizard;

import com.konakart.al.KKAppEng;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Base class for all activities.
 */
public abstract class BaseActivity implements Activity {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private String acceptState;
    private boolean acceptEmtpyState = false;
    private String nextLoggedState;
    private String nextNonLoggedState;
    private String templateRenderPath;

    protected ProcessorContext processorContext;
    protected FormMap formMap;
    protected KKAppEng kkAppEng;
    protected HstRequest hstRequest;
    protected HstResponse hstResponse;


    protected BaseActivity() {
    }

    @Override
    public void initialize(ProcessorContext processorContext) {
        this.processorContext = processorContext;
        this.hstRequest = processorContext.getSeedData().getRequest();
        this.hstResponse = processorContext.getSeedData().getResponse();

        formMap = new FormMap(hstRequest, getCheckoutFormMapFields());
        FormUtils.populate(hstRequest, formMap);
        kkAppEng = processorContext.getSeedData().getKkBaseHstComponent().
                getKKAppEng(processorContext.getSeedData().getRequest());
    }

    @Override
    public FormMap getFormMap() {
        return formMap;
    }

    @Override
    public boolean hasErrors() {
        Map<String,List<String>> messages = formMap.getMessages();

        boolean hasError = false;

        if (messages != null) {
            for (List<String> values : messages.values()) {
                if (values.size() > 0) {
                    hasError = true;
                    break;
                }
            }
        }

        return hasError;
    }

    @Override
    public boolean acceptState(String state) {
        return (acceptEmtpyState && (state == null)) || ((state != null) && acceptState.equals(state));

    }

    /**
     * Set the step accepted by this activity
     * @param acceptState the step to set
     */
    public void setAcceptState(String acceptState) {
        this.acceptState = acceptState;
    }

    public String getAcceptState() {
        return acceptState;
    }

    @Override
    public void setAcceptEmptyState(boolean acceptEmtpyState) {
        this.acceptEmtpyState = acceptEmtpyState;
    }

    @Override
    public String computeNextState() {
        if (processorContext.getSeedData().getKkBaseHstComponent().isGuestCustomer(hstRequest) &&
                !isCheckoutAsGuest() && !isCheckoutAsRegister()) {
            return nextNonLoggedState;
        }

        return nextLoggedState;
    }

    public String getNextLoggedState() {
        return nextLoggedState;
    }

    @Override
    public void setNextLoggedState(String nextLoggedState) {
        this.nextLoggedState = nextLoggedState;
    }

    @Override
    public void setNextNonLoggedState(String nextNonLoggedState) {
        this.nextNonLoggedState = nextNonLoggedState;
    }

    @Override
    public void setTemplateRenderPath(String templateRenderPath) {
        this.templateRenderPath = templateRenderPath;
    }

    /**
     * @return the template that will be rendered.
     */
    public String getTemplateRenderPath() {
        return templateRenderPath;
    }

    @Override
    public boolean doValidForm() {
        hstResponse.setRenderParameter(KKCheckoutConstants.DONT_HAVE_ACCOUNT, getDontHaveAccountValue());

        // By default the form is valid.
        return true;
    }

    @Override
    public void doAction() throws ActivityException {
        doApplyTemplateRenderPath();
        hstResponse.setRenderParameter(KKCheckoutConstants.DONT_HAVE_ACCOUNT, getDontHaveAccountValue());
    }

    public void doApplyTemplateRenderPath() {
        hstResponse.setRenderPath(getTemplateRenderPath());
    }

    @Override
    public void doAdditionalData() {
        hstResponse.setRenderParameter(KKCheckoutConstants.DONT_HAVE_ACCOUNT, getDontHaveAccountValue());
    }

    @Override
    public String[] getCheckoutFormMapFields() {
        return new String[0];
    }

    /**
     * Add a message to the formMap
     * @param name the name
     * @param message the message
     */
    protected void addMessage(String name, String message) {
        formMap.addMessage(name, message);
    }

    /**
     * @return the dontHaveAccount value if is has been set.
     */
    @Nullable
    protected String getDontHaveAccountValue() {
        String dontHaveAccount = hstRequest.getParameter(KKCheckoutConstants.DONT_HAVE_ACCOUNT);

        if (StringUtils.isEmpty(dontHaveAccount)) {
            dontHaveAccount = (String) hstRequest.getAttribute(KKCheckoutConstants.DONT_HAVE_ACCOUNT);
        }

        return dontHaveAccount;
    }

    /**
     * Check if the customer asked to checkout as a guest
     *
     * @return true if the customer asked to checkout as a guest, false otherwise
     */
    protected boolean isCheckoutAsGuest() {
        String dontHaveAccount = getDontHaveAccountValue();

        return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKCheckoutConstants.CHECKOUT_AS_GUEST);
    }

    /**
     * Check if the customer asked to checkout as a register
     *
     * @return true if the customer asked to checkout as a register, false otherwise
     */
    protected boolean isCheckoutAsRegister() {
        String dontHaveAccount = getDontHaveAccountValue();

        return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKCheckoutConstants.CHECKOUT_ASK_REGISTER);
    }
}
