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
