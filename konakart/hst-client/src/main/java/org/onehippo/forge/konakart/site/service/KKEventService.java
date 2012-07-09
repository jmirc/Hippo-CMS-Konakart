package org.onehippo.forge.konakart.site.service;

import com.konakart.appif.CustomerEventIf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface KKEventService {

    /**
     * Returns a customer event object with the action and customer id attributes populated. If
     * events aren't enabled, then null is returned.
     *
     * @param request http Request
     * @param action  Event action
     * @return Returns a customer event object or null if events aren't enabled
     */
    @Nullable
    CustomerEventIf getCustomerEvent(@Nonnull HttpServletRequest request, int action);

    /**
     * Inserts a customer event where all of the available parameters are passed
     *
     * @param request http Request
     * @param action  Event action
     * @param str1    a string
     * @param str2    a string
     * @param int1    an integer
     * @param int2    an integer
     * @param dec1    a decimal
     * @param dec2    a decimal
     */
    void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, String str1, String str2,
                             int int1, int int2, BigDecimal dec1, BigDecimal dec2);

    /**
     * Shortcut method for inserting a customer event passing no custom event data
     *
     * @param request Hst Request
     * @param action  the action to save
     */
    void insertCustomerEvent(@Nonnull HttpServletRequest request, int action);

    /**
     * Shortcut method for inserting a customer event passing an integer as event data
     *
     * @param request http Request
     * @param action  the action to save
     * @param int1    an integer
     */
    void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, int int1);

    /**
     * Shortcut method for inserting a customer event passing a string as event data
     *
     * @param request http Request
     * @param action  the action to save
     * @param str1    a string
     */
    void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, String str1);

    /**
     * Shortcut method for inserting a customer event passing a decimal as event data
     *
     * @param request http Request
     * @param action  the action to save
     * @param dec1    a decinal
     */
    void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, BigDecimal dec1);


}
