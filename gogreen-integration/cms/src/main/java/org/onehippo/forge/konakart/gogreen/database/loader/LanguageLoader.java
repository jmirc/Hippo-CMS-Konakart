package org.onehippo.forge.konakart.gogreen.database.loader;

import com.konakart.bl.KKCriteria;
import com.konakart.om.BaseLanguagesPeer;
import com.konakartadmin.app.AdminLanguage;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import org.apache.torque.util.BasePeer;

public class LanguageLoader extends org.onehippo.forge.konakart.gogreen.database.loader.BaseLoader {
  public LanguageLoader(final AdminMgrFactory adminMgrFactory, final String filePath) throws Exception {
    super(adminMgrFactory, filePath);
  }

  @Override
  protected void processRow(String[] csvLine) throws Exception {

    AdminLanguageMgrIf languageMgrIf = adminMgrFactory.getAdminLanguageMgr(true);

    AdminLanguage adminLanguage = new AdminLanguage();

    adminLanguage.setName(csvLine[0]);
    adminLanguage.setCode(csvLine[1]);
    adminLanguage.setImage(csvLine[2]);
    adminLanguage.setDirectory(csvLine[3]);
    adminLanguage.setSortOrder(Integer.parseInt(csvLine[4]));
    adminLanguage.setLocale(csvLine[5]);
    adminLanguage.setDisplayOnly(Boolean.parseBoolean(csvLine[6]));

    int languageId = languageMgrIf.insertLanguage(adminLanguage);

    KKCriteria selectC = new KKCriteria();
    selectC.add(BaseLanguagesPeer.LANGUAGES_ID, languageId);

    KKCriteria updateC = new KKCriteria();
    updateC.add(BaseLanguagesPeer.STORE_ID, csvLine[7]);
    BasePeer.doUpdate(selectC, updateC);

    System.out.println("Language -" + adminLanguage.getName() + "- has been added");

  }
}
