package org.onehippo.forge.konakart.gogreen.database.loader;

import com.konakart.bl.KKCriteria;
import com.konakart.om.BaseOrdersStatusPeer;
import com.konakartadmin.app.AdminLanguage;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import org.apache.torque.util.BasePeer;
import org.onehippo.forge.konakart.gogreen.database.utils.LanguageUtil;

public class OrderStatusLoader extends BaseLoader {
  public OrderStatusLoader(final AdminMgrFactory adminMgrFactory, final String filePath) throws Exception {
    super(adminMgrFactory, filePath);
  }

  @Override
  protected void processRow(String[] csvLine) throws Exception {


    AdminLanguageMgrIf adminLanguageMgr = adminMgrFactory.getAdminLanguageMgr(true);
    AdminLanguage[] adminLanguages = adminLanguageMgr.getAllLanguages();

    Integer statusId = Integer.parseInt(csvLine[0]);

    KKCriteria selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[1]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("en_US", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);

    selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[2]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("fr_FR", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);

    selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[3]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("nl_NL", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);

    selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[4]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("it_IT", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);

    selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[5]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("cn_ZH", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);

    selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[6]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("es_ES", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);

    selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[7]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("ru_RU", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);

    selectC = new KKCriteria();
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_ID, statusId);
    selectC.add(BaseOrdersStatusPeer.ORDERS_STATUS_NAME, csvLine[8]);
    selectC.add(BaseOrdersStatusPeer.LANGUAGE_ID, LanguageUtil.getLanguageId("de_DE", adminLanguages));
    selectC.add(BaseOrdersStatusPeer.NOTIFY_CUSTOMER, 0);
    BasePeer.doInsert(selectC);


    System.out.println("Order Status - " + statusId + "- has been added");

  }
}
