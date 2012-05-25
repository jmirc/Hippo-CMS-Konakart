package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.al.KKAppException;
import com.konakart.al.ProdOption;
import com.konakart.al.ProdOptionContainer;
import com.konakart.app.Basket;
import com.konakart.app.KKException;
import com.konakart.app.Option;
import com.konakart.appif.BasketIf;
import com.konakart.appif.OptionIf;
import com.konakart.appif.ProductIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class KKBaseServiceImpl {

    protected Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Retrieve the Konakart client from the HstRequest.
     * The client has been set by the Konakart Valve.
     *
     * @param request the hst request
     * @return the Konakart client.
     */
    @Nonnull
    public KKAppEng getKKAppEng(HstRequest request) {
        KKAppEng kkAppEng =(KKAppEng) request.getAttribute(KKAppEng.KONAKART_KEY);

        return checkNotNull(kkAppEng);
    }



}
