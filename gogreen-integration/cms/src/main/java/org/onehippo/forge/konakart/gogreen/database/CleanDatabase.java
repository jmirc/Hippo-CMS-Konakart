package org.onehippo.forge.konakart.gogreen.database;

import com.konakart.bl.KKCriteria;
import com.konakart.om.*;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Transaction;

import java.sql.Connection;

/**
 * This class is used to clean the database in removing the demo data
 */
public class CleanDatabase {


  public static void execute() throws Exception {

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

    // Delete all languages
    deleteAllLanguages();

    // Delete all order status
    deleteAllOrderStatus();
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
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
        } catch (Exception ex) {
          System.out.println("Problem closing the connection : " + ex.getMessage());
        }
      }
    }
  }

  public static void deleteAllLanguages() throws Exception {
    KKCriteria kkCriteria = new KKCriteria();
    Connection connection = Transaction.begin(kkCriteria.getDbName());
    try {
      BasePeer.doDelete(kkCriteria, BaseLanguagesPeer.TABLE_NAME);

      Transaction.commit(connection);

      System.out.println("All languages have been deleted.");

    } catch (Exception e) {
      System.out.println("Problem deleting languages : " + e.getMessage());
      Transaction.safeRollback(connection);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          System.out.println("Problem closing the connection : " + ex.getMessage());
        }
      }
    }
  }

  public static void deleteAllOrderStatus() throws Exception {
    KKCriteria kkCriteria = new KKCriteria();
    Connection connection = Transaction.begin(kkCriteria.getDbName());
    try {
      BasePeer.doDelete(kkCriteria, BaseOrdersStatusPeer.TABLE_NAME);

      Transaction.commit(connection);

      System.out.println("All orders status have been deleted.");

    } catch (Exception e) {
      System.out.println("Problem deleting languages : " + e.getMessage());
      Transaction.safeRollback(connection);
    } finally {
      if (connection != null) {
        try {
          connection.close();
        } catch (Exception ex) {
          System.out.println("Problem closing the connection : " + ex.getMessage());
        }
      }
    }
  }
}
