package org.onehippo.forge.konakart.cms.replication.synchronization.job;

import com.konakartadmin.app.AdminCustomer;
import com.konakartadmin.app.AdminCustomerSearch;
import com.konakartadmin.app.AdminCustomerSearchResult;
import com.konakartadmin.appif.KKAdminIf;
import org.onehippo.forge.konakart.cms.replication.utils.NodeHelper;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.utilities.commons.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class KonakartSyncCustomers {

    public static final Logger log = LoggerFactory.getLogger(KonakartSyncCustomers.class);

    public static final String KONAKART_USER_PROVIDER_PATH = "/hippo:configuration/hippo:security/konakart/hipposys:userprovider";
    public static final Long DEFAULT_DIR_LEVELS = 2L;

    /**
     * Synchronize the customers
     */
    public static synchronized void updateRepositoryToKonakart(Session session) {

        try {
            int dirLevels = retrieveDirLevelsFromRepo(session);

            KKAdminIf kkAdminIf = KKAdminEngine.getInstance().init(session);

            //customer type. 0 = customer, 1 = Admin App user, 2 = non registered customer
            AdminCustomerSearch adminCustomerSearch = new AdminCustomerSearch();
            adminCustomerSearch.setType(0);

            AdminCustomerSearchResult result = kkAdminIf.getCustomers(KKAdminEngine.getInstance().getSession(), adminCustomerSearch, 0, Integer.MAX_VALUE);

            if (result == null) {
                log.warn("No customers have been found. The synchronization will be skipped.");
                return;
            }

            AdminCustomer[] customers = result.getCustomers();

            if (customers == null || customers.length == 0) {
                log.warn("No customers have been found. The synchronization will be skipped.");
                return;
            }

            NodeHelper nodeHelper = new NodeHelper(session);

            for (AdminCustomer customer : customers) {
                nodeHelper.createOrRetrieveCustomer(customer, dirLevels);
            }
        } catch (Exception e) {
            log.error("Failed to synchronize the customers", e);
        }


    }

    private static int retrieveDirLevelsFromRepo(Session session) {
        try {
            Node node = session.getNode(KONAKART_USER_PROVIDER_PATH);
            return NodeUtils.getLong(node, "hipposys:dirlevels", DEFAULT_DIR_LEVELS).intValue();

        } catch (RepositoryException e) {

            log.error("Failed to retrieve the property dirlevels from the node defined at " + KONAKART_USER_PROVIDER_PATH);
        }

        return DEFAULT_DIR_LEVELS.intValue();
    }


}
