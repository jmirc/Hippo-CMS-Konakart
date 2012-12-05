package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.app.CustomerEvent;
import com.konakart.app.KKException;
import com.konakart.appif.CustomerEventIf;
import com.konakart.appif.CustomerIf;
import com.konakart.bl.ConfigConstants;
import org.onehippo.forge.konakart.site.service.KKEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public class KKEventServiceImpl extends KKBaseServiceImpl implements KKEventService {

  public static final Logger log = LoggerFactory.getLogger(KKEventServiceImpl.class);

  /*
  * Event actions
  */
  public static final int ACTION_NEW_CUSTOMER_VISIT = 1;

  public static final int ACTION_CUSTOMER_LOGIN = 2;

  public static final int ACTION_ENTER_CHECKOUT = 3;

  public static final int ACTION_CONFIRM_ORDER = 4;

  public static final int ACTION_PAYMENT_METHOD_SELECTED = 5;

  public static final int ACTION_REMOVE_FROM_CART = 6;

  public static final int ACTION_PRODUCT_VIEWED = 7;

  @Override
  public CustomerEventIf getCustomerEvent(@Nonnull HttpServletRequest request, int action) {

    KKAppEng kkAppEng = getKKAppEng(request);

    String enabled = kkAppEng.getConfig(ConfigConstants.ENABLE_CUSTOMER_EVENTS);
    if (enabled != null && enabled.equalsIgnoreCase("true")) {
      CustomerEventIf event = new CustomerEvent();
      event.setAction(action);
      CustomerIf currentCust = kkAppEng.getCustomerMgr().getCurrentCustomer();

      if (currentCust != null) {
        event.setCustomerId(currentCust.getId());
      }
      return event;
    }
    return null;
  }

  @Override
  public void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, String str1, String str2,
                                  int int1, int int2, BigDecimal dec1, BigDecimal dec2) {

    CustomerEventIf event = getCustomerEvent(request, action);
    if (event != null) {
      event.setData1Str(str1);
      event.setData2Str(str2);
      event.setData1Int(int1);
      event.setData2Int(int2);
      event.setData1Dec(dec1);
      event.setData2Dec(dec2);
      try {
        getKKAppEng(request).getEng().insertCustomerEvent(event);
      } catch (KKException e) {
        // unable to insert the event
        log.error("Failed to insert the event", e);
      }
    }
  }

  @Override
  public void insertCustomerEvent(@Nonnull HttpServletRequest request, int action) {
    insertCustomerEvent(request, action, null, null, 0, 0, null, null);
  }

  @Override
  public void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, int int1) {
    insertCustomerEvent(request, action, null, null, int1, 0, null, null);
  }

  @Override
  public void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, String str1) {
    insertCustomerEvent(request, action, str1, null, 0, 0, null, null);
  }

  @Override
  public void insertCustomerEvent(@Nonnull HttpServletRequest request, int action, BigDecimal dec1) {
    insertCustomerEvent(request, action, null, null, 0, 0, dec1, null);
  }

}
