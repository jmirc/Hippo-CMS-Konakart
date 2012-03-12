package org.onehippo.forge.konakart.hst.components;

import com.konakart.al.KKAppException;
import com.konakart.al.ProdOption;
import com.konakart.al.ProdOptionContainer;
import com.konakart.app.Basket;
import com.konakart.app.KKException;
import com.konakart.app.Option;
import com.konakart.app.WishListItem;
import com.konakart.appif.BasketIf;
import com.konakart.appif.OptionIf;
import com.konakart.appif.ProductIf;
import com.konakart.appif.WishListItemIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKUtil;

import java.util.ArrayList;
import java.util.List;

public abstract class KKHstActionComponent extends KKHstComponent {

    protected static final String ACTION = "action";

    @Override
    final public void doAction(HstRequest request, HstResponse response) {

        String type = KKUtil.getEscapedParameter(request, ACTION);

        doAction(type, request, response);
    }

    /**
     * Call with the prepopulated action. The value of the action is defined when the hst:url is created.
     * <p/>
     * i.e.
     * <hst:actionURL var="addToBasket">
     * <hst:param name="action" value="addToBasket"/>
     * <hst:param name="prodId" value="${document.productId}"/>
     * </hst:actionURL>
     *
     * @param action   the action value
     * @param request  the Hst Request
     * @param response the Hst Response
     */
    public abstract void doAction(String action, HstRequest request, HstResponse response);


    /**
     * Add the product to the basket
     *
     * @param request the hst request
     * @param prodId  id of the product to add
     * @return true if the product has been added, false otherwise
     */
    protected boolean addProductToBasket(HstRequest request, int prodId) {

        // Get the product from its Id
        try {
            kkAppEng.getProductMgr().fetchSelectedProduct(prodId);
            ProductIf selectedProd = kkAppEng.getProductMgr().getSelectedProduct();

            if (selectedProd == null) {
                return false;
            }

            // Get the selected options if exists
            OptionIf[] optionIfs = retrieveSelectedProductOptions(request);

            /*
            * Create a basket item. Only the product id is required to save the basket item. Note
            * that the array of options may be null.
            */
            BasketIf b = new Basket();
            b.setQuantity(1);
            b.setOpts(optionIfs);
            b.setProductId(selectedProd.getId());

            // Set the product
            b.setCustom1(generateHstLink(request, selectedProd.getId()));

            kkAppEng.getBasketMgr().addToBasket(b, /* refresh */true);

            return true;

        } catch (KKException e) {
            log.warn("Failed to add the product with the id {} to the basket - {} ", prodId, e.toString());
        } catch (KKAppException e) {
            log.warn("Failed to add the product with the id {} to the basket - {} ", prodId, e.toString());
        }

        return false;
    }


    /**
     * Add a product to a wish list
     *
     * @param request    Hst Request
     * @param wishListId id of the wishlist to use
     * @param productId  id of the product
     */
    public void addProductToWishList(HstRequest request, Integer wishListId, Integer productId) {

        // Get the selected options if exists
        OptionIf[] optionIfs = retrieveSelectedProductOptions(request);

        try {
            // Add an item to the customer's wish list
            if (wishListEnabled()) {
                WishListItemIf wli = new WishListItem();
                wli.setOpts(optionIfs);
                wli.setProductId(productId);

                // WishListId defaults to -1 to pick up default wish list or create a new one
                wli.setWishListId(wishListId);
                // Medium priority
                wli.setPriority(3);
                // Quantity = 1
                wli.setQuantityDesired(1);
                // Add the item
                kkAppEng.getWishListMgr().addToWishList(wli);
                // Refresh the customer's wish list
                kkAppEng.getWishListMgr().fetchCustomersWishLists();
            }
        } catch (KKException e) {
            log.warn("Failed to add the product with the id {} to the wishlist - {} ", productId, e.toString());
        } catch (KKAppException e) {
            log.warn("Failed to add the product with the id {} to the wishlist - {} ", productId, e.toString());
        }

    }


    /**
     * Used to retrieve for a product the option that has been selected by the customer.
     *
     * @param request the Hst Request
     * @return a list of options.
     */
    protected OptionIf[] retrieveSelectedProductOptions(HstRequest request) {

        List<String> fieldsName = new ArrayList<String>();

        // Retrieve selected options
        List<ProdOptionContainer> opts = kkAppEng.getProductMgr().getSelectedProductOptions();

        for (ProdOptionContainer opt : opts) {
            fieldsName.add(opt.getId());
        }


        FormMap formMap = new FormMap(request, fieldsName);
        FormUtils.populate(request, formMap);

        OptionIf[] results = new OptionIf[opts.size()];

        int i = 0;

        // Retrieve selected options
        for (ProdOptionContainer opt : opts) {
            FormField field = formMap.getField(opt.getId());

            if (field != null) {
                String value = field.getValue();

                if (StringUtils.isNotEmpty(value)) {
                    int selectedValue = Integer.parseInt(value);

                    List<ProdOption> optValues = opt.getOptValues();

                    for (ProdOption optValue : optValues) {
                        if (optValue.getId() == selectedValue) {
                            OptionIf optionIf = new Option();
                            optionIf.setId(Integer.parseInt(opt.getId()));
                            optionIf.setValueId(optValue.getId());
                            optionIf.setType(Integer.parseInt(opt.getType()));

                            results[i++] = optionIf;
                        }
                    }

                }
            }

        }

        return results;
    }

}
