package org.onehippo.forge.konakart.hst.wizard.checkout;

import org.hippoecm.hst.component.support.forms.FormMap;
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

            if (activity.acceptState(context.getSeedData().getState())) {
                activity.initialize(context);
                activity.doBeforeRender();
            }
        }
    }

    @Override
    public FormMap doAction(SeedData seedObject) throws ActivityException {
        FormMap formMap = null;

        if (log.isDebugEnabled()) {
            log.debug("doAction - " + getBeanName() + " processor is running..");
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

            if (activity.acceptState(context.getSeedData().getState())) {
                activity.initialize(context);

                if (activity.doValidForm()) {
                    activity.doAction();
                    formMap = activity.getFormMap();
                }
            }
        }

        return formMap;
    }

    @Override
    public String computeNextState(SeedData seedObject) throws ActivityException {
        if (log.isDebugEnabled()) {
            log.debug("doAction - " + getBeanName() + " processor is running..");
        }

        String nextState = null;

        //retrieve injected by Spring
        List<Activity> activities = getActivities();

        // Create the processor context
        CheckoutProcessContext context = new CheckoutProcessContext();
        context.setSeedData(seedObject);

        for (Activity activity : activities) {

            if (log.isDebugEnabled()) {
                log.debug("running activity:" + activity.getBeanName() + " using arguments:" + context);
            }

            if (activity.acceptState(context.getSeedData().getState())) {
                activity.initialize(context);

                nextState = activity.computeNextState();
            }
        }

        return nextState;
    }
}
