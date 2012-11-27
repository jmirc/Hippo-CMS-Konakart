package org.onehippo.cms7.hst.hippokart.gogreen.database.loader;

import com.konakartadmin.app.AdminLanguage;
import com.konakartadmin.app.AdminManufacturer;
import com.konakartadmin.app.AdminManufacturerInfo;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import com.konakartadmin.blif.AdminManufacturerMgrIf;
import org.onehippo.cms7.hst.hippokart.gogreen.database.utils.LanguageUtil;

public class ManufacturerLoader extends org.onehippo.cms7.hst.hippokart.gogreen.database.loader.BaseLoader {

    public static int defaultManufacturerId;

    public ManufacturerLoader(final AdminMgrFactory adminMgrFactory, final String filePath) throws Exception {
        super(adminMgrFactory, filePath);
    }

    @Override
    protected void processRow(String[] csvLine) throws Exception {

        AdminLanguageMgrIf adminLanguageMgr = adminMgrFactory.getAdminLanguageMgr(true);
        AdminLanguage[] adminLanguages = adminLanguageMgr.getAllLanguages();

        AdminManufacturerMgrIf adminManufacturerMgr = adminMgrFactory.getAdminManuMgr(true);

        AdminManufacturer adminManufacturer = new AdminManufacturer();
        adminManufacturer.setImage("none.png");

        adminManufacturer.setName(csvLine[0]);

        AdminManufacturerInfo[] adminManufacturerInfos = new AdminManufacturerInfo[1];
        adminManufacturer.setInfos(adminManufacturerInfos);

        AdminManufacturerInfo adminManufacturerInfo1 = new AdminManufacturerInfo();
        adminManufacturerInfo1.setUrl(csvLine[1]);
        adminManufacturerInfo1.setLanguageId(LanguageUtil.getLanguageId("en_US", adminLanguages));
        adminManufacturerInfos[0] = adminManufacturerInfo1;

//        AdminManufacturerInfo adminManufacturerInfo2 = new AdminManufacturerInfo();
//        adminManufacturerInfo2.setUrl(csvLine[1]);
//        adminManufacturerInfo2.setLanguageId(LanguageUtil.getLanguageId("fr_FR", adminLanguages));
//        adminManufacturerInfos[1] = adminManufacturerInfo2;
//
//        AdminManufacturerInfo adminManufacturerInfo3 = new AdminManufacturerInfo();
//        adminManufacturerInfo3.setUrl(csvLine[1]);
//        adminManufacturerInfo3.setLanguageId(LanguageUtil.getLanguageId("nl_NL", adminLanguages));
//        adminManufacturerInfos[2] = adminManufacturerInfo3;
//
//        AdminManufacturerInfo adminManufacturerInfo4 = new AdminManufacturerInfo();
//        adminManufacturerInfo4.setUrl(csvLine[1]);
//        adminManufacturerInfo4.setLanguageId(LanguageUtil.getLanguageId("it_IT", adminLanguages));
//        adminManufacturerInfos[3] = adminManufacturerInfo4;
//
//        AdminManufacturerInfo adminManufacturerInfo5 = new AdminManufacturerInfo();
//        adminManufacturerInfo5.setUrl(csvLine[1]);
//        adminManufacturerInfo5.setLanguageId(LanguageUtil.getLanguageId("cn_ZH", adminLanguages));
//        adminManufacturerInfos[4] = adminManufacturerInfo5;
//
//        AdminManufacturerInfo adminManufacturerInfo6 = new AdminManufacturerInfo();
//        adminManufacturerInfo6.setUrl(csvLine[1]);
//        adminManufacturerInfo6.setLanguageId(LanguageUtil.getLanguageId("es_ES", adminLanguages));
//        adminManufacturerInfos[5] = adminManufacturerInfo6;
//
//        AdminManufacturerInfo adminManufacturerInfo7 = new AdminManufacturerInfo();
//        adminManufacturerInfo7.setUrl(csvLine[1]);
//        adminManufacturerInfo7.setLanguageId(LanguageUtil.getLanguageId("ru_RU", adminLanguages));
//        adminManufacturerInfos[6] = adminManufacturerInfo7;

        defaultManufacturerId = adminManufacturerMgr.insertManufacturer(adminManufacturer);

        System.out.println("The brand named - " + csvLine[0] + " has been added.");
    }


    
}
