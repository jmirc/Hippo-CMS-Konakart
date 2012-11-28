package org.onehippo.cms7.hst.hippokart.gogreen.database.helper;

import com.konakartadmin.app.AdminCategory;
import com.konakartadmin.app.AdminLanguage;
import com.konakartadmin.app.AdminProduct;
import com.konakartadmin.app.AdminProductDescription;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import com.konakartadmin.blif.AdminProductMgrIf;
import org.onehippo.cms7.hst.hippokart.gogreen.database.loader.CategoryLoader;
import org.onehippo.cms7.hst.hippokart.gogreen.database.loader.ManufacturerLoader;
import org.onehippo.cms7.hst.hippokart.gogreen.database.utils.LanguageUtil;
import org.onehippo.cms7.hst.hippokart.gogreen.database.vo.ProductDescriptionVo;

import java.math.BigDecimal;
import java.util.*;

public class ProductHelper {

    public static Map<String, Integer> productsMapping = new HashMap<String, Integer>();

    private static int defaultTaxClassId;

    private static AdminProductMgrIf productMgrIf;

    private static AdminLanguage[] adminLanguages;
    private String name;
    private List<Integer> categories;
    private double price;
    private Calendar creationDate;
    private Calendar lastModificationDate;
    private Calendar publicationDate;
    private List<String> images = new ArrayList<String>();
    private List<ProductDescriptionVo> descriptionVos = new ArrayList<ProductDescriptionVo>();
    private String summary;

    public ProductHelper() throws Exception {

    }

    public static void setAdminMgrFactory(AdminMgrFactory adminMgrFactory) throws Exception {

        defaultTaxClassId = TaxClassesHelper.defaultTaxClassId;

        productMgrIf = adminMgrFactory.getAdminProdMgr(true);

        AdminLanguageMgrIf adminLanguageMgr = adminMgrFactory.getAdminLanguageMgr(true);
        adminLanguages = adminLanguageMgr.getAllLanguages();

    }

    public int process() throws Exception {

        AdminProduct adminProduct = new AdminProduct();

        // Set mandatory fields
        adminProduct.setType(0);
        adminProduct.setQuantity(100);
        adminProduct.setTaxClassId(defaultTaxClassId);
        adminProduct.setWeight(new BigDecimal(0));
        adminProduct.setStatus((byte) 1);
        adminProduct.setImage("none.png");
        adminProduct.setDateAdded(creationDate.getTime());
        adminProduct.setDateAvailable(publicationDate.getTime());
        adminProduct.setDateLastModified(lastModificationDate.getTime());

        // Brand
        adminProduct.setManufacturerId(ManufacturerLoader.defaultManufacturerId);

        // Product Name
        adminProduct.setName(name);

        // Product code
        String uuid = UUID.randomUUID().toString();
        adminProduct.setModel(uuid);

        // Associate to the "physical product type" category
        List<AdminCategory> allAdminCategories = new ArrayList<AdminCategory>();

        for (int catId : categories) {
            AdminCategory adminCategory = new AdminCategory();
            adminCategory.setId(catId);
            allAdminCategories.add(adminCategory);
        }

        adminProduct.setCategories(allAdminCategories.toArray(new AdminCategory[allAdminCategories.size()]));

        adminProduct.setPriceExTax(new BigDecimal(price));


        AdminProductDescription[] descriptions = new AdminProductDescription[descriptionVos.size()];

        int i=0;
        for (ProductDescriptionVo descriptionVo : descriptionVos) {
            // Set the product adminProductDescription
            AdminProductDescription adminProductDescription = new AdminProductDescription();
            adminProductDescription.setLanguageId(LanguageUtil.getLanguageId(descriptionVo.getLocale(), adminLanguages));
            adminProductDescription.setName(descriptionVo.getTitle());

            // Add summary
            String description = descriptionVo.getSummary();

            // Add separator
            description += "-------";

            // Add description
            description += descriptionVo.getDescription();

            adminProductDescription.setDescription(description);
            descriptions[i] = adminProductDescription;

            i = i + 1;
        }

        adminProduct.setDescriptions(descriptions);

        Iterator<String> imagesIterator = images.iterator();

        if (imagesIterator.hasNext()) {
            adminProduct.setImage(imagesIterator.next());
        }
        if (imagesIterator.hasNext()) {
            adminProduct.setImage2(imagesIterator.next());
        }
        if (imagesIterator.hasNext()) {
            adminProduct.setImage3(imagesIterator.next());
        }
        if (imagesIterator.hasNext()) {
            adminProduct.setImage4(imagesIterator.next());
        }

        int productId = productMgrIf.insertProduct(adminProduct);

        System.out.println("The product named " + name + " has been added.");

        return productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategories(List<Integer> categories) {
        this.categories = categories;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCreationDate(Calendar creationDate) {
        this.creationDate = creationDate;
    }

    public void setLastModificationDate(Calendar lastModificationDate) {
        this.lastModificationDate = lastModificationDate;
    }

    public void setPublicationDate(Calendar publicationDate) {
        this.publicationDate = publicationDate;
    }

    public void addImage(String image) {
        images.add(image);
    }

    public void addDescription(ProductDescriptionVo descriptionVo) {
        descriptionVos.add(descriptionVo);
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
}
