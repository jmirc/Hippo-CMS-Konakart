package org.onehippo.forge.konakart.hst.components;

import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

/**
 * This detail product component should be used to retrieve a product
 *
 */
public abstract class KKProductDetail extends KKHstComponent {

    public static final String TYPE_PARAMETER = "type";

    /**
     * This action is used to add a product to the baskket
     */
    public static final String ADD_TO_BASKET_ACTION = "addToBasket";

    /**
     * This action is used to remove a product to the baskket
     */
    public static final String REMOVE_TO_BASKET_ACTION = "removeToBasket";


    @Override
    public void doAction(HstRequest request, HstResponse response) throws HstComponentException {
        super.doAction(request, response);




    }
}
