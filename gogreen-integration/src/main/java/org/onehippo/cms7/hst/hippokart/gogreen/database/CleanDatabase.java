package org.onehippo.cms7.hst.hippokart.gogreen.database;

import com.konakart.bl.KKCriteria;
import com.konakart.om.*;
import com.konakartadmin.app.AdminLanguage;
import com.konakartadmin.bl.AdminMgrFactory;
import com.konakartadmin.blif.AdminLanguageMgrIf;
import org.apache.commons.lang.StringUtils;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Transaction;

import java.sql.Connection;

/**
 * This class is used to clean the database in removing the demo data
 */
public class CleanDatabase {



    public static void execute(AdminMgrFactory adminMgrFactory) throws Exception {

        // Delete products
        deleteAllProducts();

        // Delete all products options
        deleteAllProductsOptions();

        // Delete all reviews
        deleteAllReviews();

        // Delete all manufacturers
        deleteAllManufacturers();

        // Delete all categories
        deleteAllCategories();

        // Delete all Tags Groups
        deleteAllTagsGroups();

        // Update language
        deleteLanguages(adminMgrFactory);
    }


    public static void deleteAllProducts() throws Exception {

        KKCriteria kkCriteria = new KKCriteria();
        Connection connection = Transaction.begin(kkCriteria.getDbName());
        try {
            BasePeer.doDelete(kkCriteria, BaseProductsAttributesDownloadPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseProductsToCategoriesPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseProductsDescriptionPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseKkProductPricesPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseProductsPeer.TABLE_NAME, connection);

            Transaction.commit(connection);

            System.out.println("All products have been deleted.");

        } catch (Exception e) {
            System.out.println("Problem deleting products : " + e.getMessage());
            Transaction.safeRollback(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch(Exception ex) {
                    System.out.println("Problem closing the connection : " + ex.getMessage());
                }
            }
        }
    }

    public static void deleteAllProductsOptions() throws Exception {

        KKCriteria kkCriteria = new KKCriteria();
        Connection connection = Transaction.begin(kkCriteria.getDbName());
        try {
            BasePeer.doDelete(kkCriteria, BaseProductsOptionsValuesToProductsOptionsPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseProductsOptionsValuesPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseProductsOptionsPeer.TABLE_NAME, connection);

            Transaction.commit(connection);

            System.out.println("All products options have been deleted.");

        } catch (Exception e) {
            System.out.println("Problem deleting ProductOptions : " + e.getMessage());
            Transaction.safeRollback(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch(Exception ex) {
                    System.out.println("Problem closing the connection : " + ex.getMessage());
                }
            }
        }

    }

    private static void deleteAllReviews() throws Exception {
        KKCriteria kkCriteria = new KKCriteria();
        Connection connection = Transaction.begin(kkCriteria.getDbName());
        try {
            BasePeer.doDelete(kkCriteria, BaseReviewsDescriptionPeer.TABLE_NAME);
            BasePeer.doDelete(kkCriteria, BaseReviewsPeer.TABLE_NAME);

            Transaction.commit(connection);

            System.out.println("All reviews have been deleted.");

        } catch (Exception e) {
            System.out.println("Problem deleting reviews : " + e.getMessage());
            Transaction.safeRollback(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch(Exception ex) {
                    System.out.println("Problem closing the connection : " + ex.getMessage());
                }
            }
        }
    }

    public static void deleteAllManufacturers() throws Exception {

        KKCriteria kkCriteria = new KKCriteria();
        Connection connection = Transaction.begin(kkCriteria.getDbName());
        try {
            BasePeer.doDelete(kkCriteria, BaseManufacturersInfoPeer.TABLE_NAME);
            BasePeer.doDelete(kkCriteria, BaseManufacturersPeer.TABLE_NAME);

            Transaction.commit(connection);

            System.out.println("All manufacturers have been deleted.");

        } catch (Exception e) {
            System.out.println("Problem deleting manufacturers : " + e.getMessage());
            Transaction.safeRollback(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch(Exception ex) {
                    System.out.println("Problem closing the connection : " + ex.getMessage());
                }
            }
        }
    }

    private static void deleteAllCategories() throws Exception {
        KKCriteria kkCriteria = new KKCriteria();
        Connection connection = Transaction.begin(kkCriteria.getDbName());
        try {
            BasePeer.doDelete(kkCriteria, BaseCategoryToTagGroupPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseCategoriesDescriptionPeer.TABLE_NAME, connection);
            BasePeer.doDelete(kkCriteria, BaseCategoriesPeer.TABLE_NAME, connection);

            Transaction.commit(connection);

            System.out.println("All categories have been deleted.");

        } catch (Exception e) {
            System.out.println("Problem deleting categories : " + e.getMessage());
            Transaction.safeRollback(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch(Exception ex) {
                    System.out.println("Problem closing the connection : " + ex.getMessage());
                }
            }
        }
    }

    public static void deleteAllTagsGroups() throws Exception {

        KKCriteria kkCriteria = new KKCriteria();
        Connection connection = Transaction.begin(kkCriteria.getDbName());
        try {
            BasePeer.doDelete(kkCriteria, BaseTagGroupToTagPeer.TABLE_NAME);
            BasePeer.doDelete(kkCriteria, BaseTagGroupPeer.TABLE_NAME);
            BasePeer.doDelete(kkCriteria, BaseTagPeer.TABLE_NAME);

            Transaction.commit(connection);

            System.out.println("All tag groups have been deleted.");

        } catch (Exception e) {
            System.out.println("Problem deleting tag groups : " + e.getMessage());
            Transaction.safeRollback(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch(Exception ex) {
                    System.out.println("Problem closing the connection : " + ex.getMessage());
                }
            }
        }
    }

    public static void deleteLanguages(AdminMgrFactory adminMgrFactory) throws Exception {
        AdminLanguageMgrIf languageMgr = adminMgrFactory.getAdminLanguageMgr(false);

        AdminLanguage[] languages = languageMgr.getAllLanguages();

        for (AdminLanguage language : languages) {
            if (!StringUtils.equalsIgnoreCase("en", language.getCode())) {
                languageMgr.deleteLanguage(language.getId());
                System.out.println(language.getCode() + " language has been deleted.");
            }
        }
    }
}
