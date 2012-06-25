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
