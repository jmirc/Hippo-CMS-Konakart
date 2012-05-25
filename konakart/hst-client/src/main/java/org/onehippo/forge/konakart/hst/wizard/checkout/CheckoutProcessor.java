package org.onehippo.forge.konakart.hst.wizard.checkout;

import org.hippoecm.hst.component.support.forms.FormMap;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.onehippo.forge.konakart.hst.wizard.Activity;
import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.BaseProcessor;
import org.onehippo.forge.konakart.hst.wizard.SeedData;
import org.onehippo.forge.konakart.hst.wizard.checkout.activity.BaseCheckoutActivity;

import java.util.List;

public class CheckoutProcessor extends BaseProcessor {

    @Override
    public boolean supports(Activity activity) {
        return activity instanceof BaseCheckoutActivity;
    }

    @Override
    public void doBeforeRender(SeedData seedObject) throws ActivityException {

        String initialState = getCurrentState(seedObject.getRequest());
        String nextState = initialState;

        // Set state
        ((CheckoutSeedData) seedObject).setState(initialState);
        ((CheckoutSeedData) seedObject).setAction(getCurrentAction(seedObject.getRequest()));

        if (log.isDebugEnabled()) {
            log.debug("doBeforeRender - " + getBeanName() + " processor is running..");
        }

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        // Create the processor context
        CheckoutProcessContext context = new CheckoutProcessContext();
        context.setSeedData(seedObject);


        for (Activity activity : activities) {
            if (log.isDebugEnabled()) {
                log.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);
            }

            activity.initialize(context);

            if (activity.acceptState(nextState)) {
                activity.doBeforeRender();

                // If an activity accept the initial state, this is means that this activity has been already
                // executed so the real activity to execute is the next one.
                if (activity.acceptState(initialState) && !isEditAction(seedObject.getRequest())) {
                    nextState = activity.computeNextState();
                }
            }
        }

        ((CheckoutSeedData) seedObject).setState(nextState);
        seedObject.getRequest().setAttribute(STATE, nextState);
    }



    @Override
    public FormMap doAction(SeedData seedObject) throws ActivityException {
        FormMap formMap = null;

        String currentState = getCurrentState(seedObject.getRequest());

        if (log.isDebugEnabled()) {
            log.debug("doAction - " + getBeanName() + " processor is running..");
        }

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        // Create the processor context
        CheckoutProcessContext context = new CheckoutProcessContext();
        context.setSeedData(seedObject);
        ((CheckoutSeedData) seedObject).setState(currentState);

        for (Activity activity : activities) {

            if (log.isDebugEnabled()) {
                log.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);
            }

            if (activity.acceptState(context.getSeedData().getState())) {
                activity.initialize(context);

                if (activity.doValidForm()) {
                    activity.doAction();
                    formMap = activity.getFormMap();
                }
            }
        }

        seedObject.getResponse().setRenderParameter(STATE, currentState);
        seedObject.getResponse().setRenderParameter(KKCheckoutConstants.ACTION, ((CheckoutSeedData) seedObject).getAction());

        return formMap;
    }

    @Override
    public void doAdditionalData(SeedData seedObject) throws ActivityException {

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        // Create the processor context
        CheckoutProcessContext context = new CheckoutProcessContext();
        context.setSeedData(seedObject);

        for (Activity activity : activities) {

            if (log.isDebugEnabled()) {
                log.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);
            }

            activity.initialize(context);
            activity.doAdditionalData();
        }


    }
}
