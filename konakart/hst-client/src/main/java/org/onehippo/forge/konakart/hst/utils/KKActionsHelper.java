package org.onehippo.forge.konakart.hst.utils;

import com.konakart.app.KKException;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;

public class KKActionsHelper {

  /**
   * Used to set a new currency. You need to create an action URL using the following format
   * <p/>
   * <hst:actionURL var="currencyLink">
   * <hst:param name="action" value="SELECT_CURRENCY"/>
   * <hst:param name="currencyCode" value="${currency.code}"/>
   * </hst:actionURL>
   *
   * @param request     the hst request
   * @param hstResponse the hst response
   */
  public static void doAction(HstRequest request, HstResponse hstResponse) throws HstComponentException {

    String action = KKUtil.getActionRequestParameter(request, KKActionsConstants.ACTION);

    if (StringUtils.equals(action, KKActionsConstants.ACTIONS.SELECT_CURRENCY.name())) {
      String code = KKUtil.getActionRequestParameter(request, KKActionsConstants.CURRENCY_CODE);

      try {
        KKComponentUtils.getKKAppEng(request).setUserCurrency(code);
      } catch (KKException e) {
        throw new HstComponentException("Unable to set the currency to " + code, e);
      }

    }

  }

}
