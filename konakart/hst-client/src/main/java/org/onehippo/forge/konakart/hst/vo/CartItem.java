package org.onehippo.forge.konakart.hst.vo;

import org.onehippo.forge.konakart.hst.beans.KKProductDocument;

import java.io.Serializable;

public class CartItem implements Serializable {

    private int quantity;
    private int quantityAvailable;
    private int basketItemId;
    private String prodName;
    private KKProductDocument kkProductDocument;
    private String[] optNameArray;
    private int prodId;
    private String totalPrice;
    private String custom1 = null;
    private String custom2 = null;
    private String custom3 = null;
    private String custom4 = null;
    private String custom5 = null;

    public CartItem(int basketItemId, int prodId, String prodName, int quantity,
                    int quantityAvailable) {
        this.basketItemId = basketItemId;
        this.prodId = prodId;
        this.prodName = prodName;
        this.quantity = quantity;
        this.quantityAvailable = quantityAvailable;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public boolean getInStock() {
        return this.quantityAvailable >= this.quantity;
    }

    public int getBasketItemId() {
        return basketItemId;
    }

    public void setBasketItemId(int basketItemId) {
        this.basketItemId = basketItemId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public KKProductDocument getProductDocument() {
        return kkProductDocument;
    }

    public void setProductDocument(KKProductDocument kkProductDocument) {
        this.kkProductDocument = kkProductDocument;
    }

    public String[] getOptNameArray() {
        return optNameArray;
    }

    public void setOptNameArray(String[] optNameArray) {

        if (optNameArray != null) {
            this.optNameArray = new String[optNameArray.length];
            System.arraycopy(optNameArray, 0, this.optNameArray, 0, optNameArray.length );
        }

    }

    public int getProdId() {
        return prodId;
    }

    public void setProdId(int prodId) {
        this.prodId = prodId;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustom1() {
        return custom1;
    }

    public void setCustom1(String custom1) {
        this.custom1 = custom1;
    }

    public String getCustom2() {
        return custom2;
    }

    public void setCustom2(String custom2) {
        this.custom2 = custom2;
    }

    public String getCustom3() {
        return custom3;
    }

    public void setCustom3(String custom3) {
        this.custom3 = custom3;
    }

    public String getCustom4() {
        return custom4;
    }

    public void setCustom4(String custom4) {
        this.custom4 = custom4;
    }

    public String getCustom5() {
        return custom5;
    }

    public void setCustom5(String custom5) {
        this.custom5 = custom5;
    }
}
