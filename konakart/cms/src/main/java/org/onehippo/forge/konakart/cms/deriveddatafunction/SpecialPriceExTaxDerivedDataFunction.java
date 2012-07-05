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

package org.onehippo.forge.konakart.cms.deriveddatafunction;

import com.konakartadmin.app.AdminProduct;
import org.hippoecm.repository.ext.DerivedDataFunction;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import java.util.Map;

public class SpecialPriceExTaxDerivedDataFunction extends DerivedDataFunction {

    @Override
    public Map<String, Value[]> compute(Map<String, Value[]> parameters) {
        if(!parameters.containsKey("ppid")) {
            parameters.clear();
            return parameters;
        }

        try {
            int ppid = (int) parameters.get("ppid")[0].getLong();

            KKAdminEngine kkAdminEngine = KKAdminEngine.getInstance();

            AdminProduct adminProduct = kkAdminEngine.getEngine().getProduct(kkAdminEngine.getSession(), ppid);

            parameters.put("specialprice", new Value[] {getValueFactory().createValue(adminProduct.getSpecialPriceExTax())});
        } catch (RepositoryException e) {
            parameters.clear();
        } catch (Exception e) {
            parameters.clear();
        }


        return parameters;
    }
}
