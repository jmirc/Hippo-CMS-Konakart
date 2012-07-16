package org.onehippo.forge.konakart.hst.wizard.checkout;

import org.hippoecm.hst.component.support.forms.FormMap;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
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

        String currentState = getCurrentState(seedObject.getRequest());
        String nextState = getNextState(seedObject.getRequest());

        // Set state
        ((CheckoutSeedData) seedObject).setState(currentState);
        ((CheckoutSeedData) seedObject).setAction(getCurrentAction(seedObject.getRequest()));

        if (log.isDebugEnabled()) {
            log.debug("doBeforeRender - " + getClass().getSimpleName() + " processor is running..");
        }

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        // Create the processor context
        CheckoutProcessContext context = new CheckoutProcessContext();
        context.setSeedData(seedObject);

        for (Activity activity : activities) {
            if (log.isDebugEnabled()) {
                log.debug("running activity:" + activity.getClass().getSimpleName() + " using arguments:" + context);
            }

            activity.initialize(context);

            if (activity.acceptState(nextState)) {

                // Check if a form associated with the activity contains errors.
                // If so, the current page should be refreashed.
                if (activity.hasErrors()) {
                    activity.doBeforeRender();
                } else {
                    // If an activity accept the initial state, this is means that this activity has been already
                    // executed so the real activity to execute is the next one.
                    if (activity.acceptState(currentState) && !isEditAction(seedObject.getRequest())) {
                        nextState = activity.computeNextState();
                    } else {
                        activity.doBeforeRender();
                    }

                    activity.doApplyTemplateRenderPath();
                }

                seedObject.getRequest().setAttribute("form", activity.getFormMap());
            }
        }

        ((CheckoutSeedData) seedObject).setState(nextState);
        seedObject.getRequest().setAttribute(KKActionsConstants.STATE, nextState);

    }


    @Override
    public FormMap doAction(SeedData seedObject) throws ActivityException {
        FormMap formMap = null;

        String currentState = KKUtil.getActionRequestParameter(seedObject.getRequest(), KKActionsConstants.STATE);

        if (log.isDebugEnabled()) {
            log.debug("doAction - " + getClass().getSimpleName() + " processor is running..");
        }

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        // Create the processor context
        CheckoutProcessContext context = new CheckoutProcessContext();
        context.setProcessor(this);
        context.setSeedData(seedObject);
        ((CheckoutSeedData) seedObject).setState(currentState);



        for (Activity activity : activities) {

            if (log.isDebugEnabled()) {
                log.debug("running activity:" + activity.getClass().getSimpleName() + " using arguments:" + context);
            }

            if (activity.acceptState(currentState)) {
                activity.initialize(context);

                if (activity.doValidForm()) {
                    activity.doAction();
                }

                formMap = activity.getFormMap();
            }
        }

        seedObject.getResponse().setRenderParameter(KKActionsConstants.STATE, currentState);
        seedObject.getResponse().setRenderParameter(KKActionsConstants.ACTION, ((CheckoutSeedData) seedObject).getAction());


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
                log.debug("running activity:" + activity.getClass().getSimpleName() + " using arguments:" + context);
            }

            activity.initialize(context);
            activity.doAdditionalData();
        }
    }
}
