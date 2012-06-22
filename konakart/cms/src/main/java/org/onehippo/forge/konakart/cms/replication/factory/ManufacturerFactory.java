package org.onehippo.forge.konakart.cms.replication.factory;

import com.konakart.al.KKAppEng;
import com.konakart.appif.ManufacturerIf;
import org.onehippo.forge.konakart.cms.replication.utils.NodeHelper;
import org.onehippo.forge.konakart.cms.replication.utils.NodeImagesHelper;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

public class ManufacturerFactory {

    protected javax.jcr.Session session;
    private NodeHelper nodeHelper;
    private NodeImagesHelper nodeImagesHelper;

    private String contentRoot = "/content/konakart/ecommerce/manufacturers";
    private String galleryRoot = "/content/gallery/ecommerce/manufacturers";


    /**
     * @param jcrSession set the jcr session
     * @throws javax.jcr.RepositoryException .
     */
    public void setSession(Session jcrSession) throws RepositoryException {
        this.session = jcrSession;
        nodeHelper = new NodeHelper(session);
        nodeHelper.setFolderNodeTypeName(KKCndConstants.ECOMMERCE_DOC_TYPE);
        nodeHelper.setFolderAdditionWorkflowCategory(KKCndConstants.NEW_MANUFACTURERS_FOLDER_TEMPLATE);
        nodeHelper.setDocumentAdditionWorkflowCategory(KKCndConstants.NEW_MANUFACTURER_DOCUMENT_TEMPLATE);

        nodeImagesHelper = new NodeImagesHelper(session);
    }


    public void sync(KKStoreConfig kkStoreConfig) throws Exception {

        // Create or retrieve the root folder
        Node rootFolder = null;

        if (session.itemExists(contentRoot)) {
            rootFolder = session.getNode(contentRoot);
        }

        if (rootFolder == null) {
            nodeHelper.createMissingFolders(contentRoot);
        }

        String storeId = kkStoreConfig.getStoreId();

        // Initialize the KKEngine
        KKAppEng kkengine = KKEngine.get(storeId);

        ManufacturerIf[] manufacturers = kkengine.getEng().getAllManufacturers();

        for (ManufacturerIf manufacturer : manufacturers) {

            // Create or retrieve the product node
            Node manufacturerNode = nodeHelper.createOrRetrieveDocument(rootFolder, manufacturer,
                    KKCndConstants.MANUFACTURER_DOC_TYPE, session.getUserID(), "document-type-locale");

            boolean addNewManufacturer = !manufacturerNode.hasNode(KKCndConstants.PRODUCT_DESCRIPTION);
            boolean hasCheckout = false;

            // Check if the node is check-in
            if (!manufacturerNode.isCheckedOut()) {
                nodeHelper.checkout(manufacturerNode.getPath());
                hasCheckout = true;
            }

            // Create the konakart manufacturer
            createOrUpdateKonakartManufacturer(storeId, manufacturer, manufacturerNode);
        }
    }

    private void createOrUpdateKonakartManufacturer(String storeId, ManufacturerIf manufacturer, Node manufacturerNode) throws RepositoryException {
        manufacturerNode.setProperty(KKCndConstants.MANUFACTURER_ID, manufacturer.getId());
        manufacturerNode.setProperty(KKCndConstants.MANUFACTURER_NAME, manufacturer.getName());
        manufacturerNode.setProperty(KKCndConstants.MANUFACTURER_URL, manufacturer.getUrl());
    }

}
