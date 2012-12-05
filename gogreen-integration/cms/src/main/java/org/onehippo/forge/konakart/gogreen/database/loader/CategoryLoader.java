package org.onehippo.forge.konakart.gogreen.database.loader;

import com.konakartadmin.app.AdminCategory;
import com.konakartadmin.app.AdminCategoryDescription;
import com.konakartadmin.app.AdminLanguage;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminCategoryMgrIf;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.gogreen.database.utils.LanguageUtil;

import java.util.HashMap;
import java.util.Map;

public class CategoryLoader extends BaseLoader {


  // Map categoryName with id of the category
  public static Map<String, Integer> categoryNameCategoryIdsMappings = new HashMap<String, Integer>();
  public static final String DEFAULT_CAT = "Default";


  private int rootCategoriesId;
  private AdminLanguage[] adminLanguages;
  private AdminCategoryMgrIf categoryMgr;

  public CategoryLoader(final AdminMgrFactory adminMgrFactory, final String filePath, int rootCategoriesId) throws Exception {
    super(adminMgrFactory, filePath);

    this.rootCategoriesId = rootCategoriesId;

    AdminLanguageMgrIf adminLanguageMgr = adminMgrFactory.getAdminLanguageMgr(true);
    adminLanguages = adminLanguageMgr.getAllLanguages();

    categoryMgr = adminMgrFactory.getAdminCatMgr(true);
  }


  @Override
  protected void processRow(String[] csvLine) throws Exception {

    createCategory(csvLine, rootCategoriesId);
  }

  private void createCategory(String[] csvLine, int rootCategoriesId) throws Exception {

    String categoryName = csvLine[0];

    AdminCategory adminCategory = new AdminCategory();
    adminCategory.setName(categoryName);
    adminCategory.setImage("none.png");
    adminCategory.setParentId(rootCategoriesId);

    AdminCategoryDescription[] descriptions = new AdminCategoryDescription[8];
    descriptions[0] = new AdminCategoryDescription();
    descriptions[0].setName(categoryName);
    descriptions[0].setLanguageId(LanguageUtil.getLanguageId("en_US", adminLanguages));
    descriptions[0].setDescription(categoryName + " Category");

    categoryName = csvLine[1];
    descriptions[1] = new AdminCategoryDescription();
    descriptions[1].setName(categoryName);
    descriptions[1].setLanguageId(LanguageUtil.getLanguageId("fr_FR", adminLanguages));
    descriptions[1].setDescription(categoryName + " Category");

    categoryName = csvLine[2];
    descriptions[2] = new AdminCategoryDescription();
    descriptions[2].setName(categoryName);
    descriptions[2].setLanguageId(LanguageUtil.getLanguageId("nl_NL", adminLanguages));
    descriptions[2].setDescription(categoryName + " Category");

    categoryName = csvLine[3];
    descriptions[3] = new AdminCategoryDescription();
    descriptions[3].setName(categoryName);
    descriptions[3].setLanguageId(LanguageUtil.getLanguageId("it_IT", adminLanguages));
    descriptions[3].setDescription(categoryName + " Category");

    categoryName = csvLine[4];
    descriptions[4] = new AdminCategoryDescription();
    descriptions[4].setName(categoryName);
    descriptions[4].setLanguageId(LanguageUtil.getLanguageId("cn_ZH", adminLanguages));
    descriptions[4].setDescription(categoryName + " Category");

    categoryName = csvLine[5];
    descriptions[5] = new AdminCategoryDescription();
    descriptions[5].setName(categoryName);
    descriptions[5].setLanguageId(LanguageUtil.getLanguageId("es_ES", adminLanguages));
    descriptions[5].setDescription(categoryName + " Category");

    categoryName = csvLine[6];
    descriptions[6] = new AdminCategoryDescription();
    descriptions[6].setName(categoryName);
    descriptions[6].setLanguageId(LanguageUtil.getLanguageId("ru_RU", adminLanguages));
    descriptions[6].setDescription(categoryName + " Category");

    categoryName = csvLine[7];
    descriptions[7] = new AdminCategoryDescription();
    descriptions[7].setName(categoryName);
    descriptions[7].setLanguageId(LanguageUtil.getLanguageId("de_DE", adminLanguages));
    descriptions[7].setDescription(categoryName + " Category");

    adminCategory.setDescriptions(descriptions);

    // Check the availability of the category
    String isVisible = csvLine[1].trim();

    adminCategory.setInvisible(StringUtils.equalsIgnoreCase(isVisible, "true"));


    int catId = categoryMgr.insertCategory(adminCategory);

    System.out.println("The category named " + csvLine[0] + " has been added.");

    // Check if sub category needs to be created
    assignProductCodeWithCatId(csvLine[0], catId);
  }

  private void assignProductCodeWithCatId(String categoryName, int catId) {
    categoryNameCategoryIdsMappings.put(categoryName, catId);
  }

  public static Integer getCategoryId(String categoryName) {
    if (categoryNameCategoryIdsMappings.containsKey(categoryName)) {
      return categoryNameCategoryIdsMappings.get(categoryName);
    } else {
      return -1;
    }
  }
}



