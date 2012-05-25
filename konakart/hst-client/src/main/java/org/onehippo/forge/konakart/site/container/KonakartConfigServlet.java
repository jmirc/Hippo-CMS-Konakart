package org.onehippo.forge.konakart.site.container;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;
import org.onehippo.forge.konakart.site.service.KKServiceHelper;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Konakart Config Servlet
 * <p/>
 * This servlet should retrieve the konakart engine configurations.
 * <p/>
 */
public class KonakartConfigServlet extends HttpServlet {

    /**
     * Enterprise Feature
     * When in multi-store single database mode, the customers can be shared between stores
     */
    public static final String KONAKART_CUSTOMERS_SHARED_PARAM = "customers-shared";

    /**
     * Enterprise Feature
     * When in multi-store single database mode, the products can be shared between stores
     */
    public static final String KONAKART_PRODUCTS_SHARED_PARAM = "products-shared";

    /**
     * Enterprise Feature
     * Engine mode that the web services engine will use
     * 0 = Single Store (default)
     * 1 = Multi-Store Multiple-Databases (add konakart.databases.used above as well)
     * 2 = Multi-Store Single Database
     */
    public static final String KONAKART_ENGINE_MODE_PARAM = "engine-mode";

    /**
     * If true we attempt to fetch the prices from the external table
     */
    public static final String KONAKART_USE_EXTERNAL_PRICE_PARAM = "use-external-price";

    /**
     * If true we attempt to fetch the quantities from the external table
     */
    public static final String KONAKART_USE_EXTERNAL_QUANTITY_PARAM = "use-external-quantity";

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        KKServiceHelper.customersShared = BooleanUtils.toBoolean(config.getInitParameter(KONAKART_CUSTOMERS_SHARED_PARAM));
        KKServiceHelper.productsShared = BooleanUtils.toBoolean(config.getInitParameter(KONAKART_PRODUCTS_SHARED_PARAM));
        KKServiceHelper.engineMode = NumberUtils.toInt(config.getInitParameter(KONAKART_ENGINE_MODE_PARAM));
        KKServiceHelper.useExternalPrice = BooleanUtils.toBoolean(config.getInitParameter(KONAKART_USE_EXTERNAL_PRICE_PARAM));
        KKServiceHelper.useExternalQuantity = BooleanUtils.toBoolean(config.getInitParameter(KONAKART_USE_EXTERNAL_QUANTITY_PARAM));
    }

}
