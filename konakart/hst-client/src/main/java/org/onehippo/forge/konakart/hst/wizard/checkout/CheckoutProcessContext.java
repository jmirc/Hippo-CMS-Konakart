package org.onehippo.forge.konakart.hst.wizard.checkout;

import org.onehippo.forge.konakart.hst.wizard.ActivityException;
import org.onehippo.forge.konakart.hst.wizard.ProcessorContext;
import org.onehippo.forge.konakart.hst.wizard.SeedData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CheckoutProcessContext implements ProcessorContext {

    private Logger log = LoggerFactory.getLogger(CheckoutProcessContext.class);

    private CheckoutSeedData seedData;

    @Override
    public void setSeedData(SeedData seedObject) throws ActivityException {
        if (!(seedObject instanceof CheckoutSeedData)) {
            log.error("STOPPING Workflow Process, seed data instance is incorrect. " +
                    "Required class is " + CheckoutSeedData.class.getName() + " " +
                    "bug found class: " + seedObject.getClass().getName());
            throw new ActivityException("STOPPING Workflow Process");
        }
        seedData = (CheckoutSeedData) seedObject;
    }

    @Override
    public CheckoutSeedData getSeedData() {
        return seedData;
    }
}
