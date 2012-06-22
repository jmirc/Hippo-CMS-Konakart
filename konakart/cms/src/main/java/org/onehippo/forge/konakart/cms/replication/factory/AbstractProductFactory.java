package org.onehippo.forge.konakart.cms.replication.factory;

import com.google.common.collect.Lists;
import com.konakart.app.Product;
import com.konakart.appif.LanguageIf;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingGalleryProcessor;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingParameters;
import org.joda.time.DateTime;
import org.onehippo.forge.konakart.cms.replication.jcr.GalleryProcesssorConfig;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.cms.replication.utils.Codecs;
import org.onehippo.forge.konakart.cms.replication.utils.NodeHelper;
import org.onehippo.forge.konakart.cms.replication.utils.NodeImagesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

/**
 * To use the replication synchronization, you need to add a class that will extend this class to define
 * the following information :
 * - ProductDocType : Define the name of the document type which defines a product.
 * - KonakartProductPropertyName : Define the name of the node which contains the konakart product
 * <p/>
 * See the class org.onehippo.forge.konakart.demo.MyProductFactory within the demo project
 * - ProductDocType: myhippoproject:productdocument
 * - KonakartProductPropertyName : myhippoproject:konakart
 */
public abstract class AbstractProductFactory implements ProductFactory {

    private static Logger log = LoggerFactory.getLogger(AbstractProductFactory.class);

    protected javax.jcr.Session session;
    private NodeHelper nodeHelper;
    private NodeImagesHelper nodeImagesHelper;


    private String contentRoot;
    private String galleryRoot;
    private String productFolder;

    /**
     * This is an helper method that could be used to set others information defined into Konakart but not
     * already integrated within the konakart:konakart document.
     *
     * @param product the konakart's product
     * @param node    the product's node
     */
    protected abstract void updateProperties(Product product, Node node);


    @Override
    public void setSession(Session session) throws RepositoryException {
        this.session = session;
        nodeHelper = new NodeHelper(session);
        nodeHelper.setFolderNodeTypeName(KKCndConstants.ECOMMERCE_DOC_TYPE);
        nodeHelper.setFolderAdditionWorkflowCategory(KKCndConstants.NEW_PRODUCTS_FOLDER_TEMPLATE);
        nodeHelper.setDocumentAdditionWorkflowCategory(KKCndConstants.NEW_PRODUCT_DOCUMENT_TEMPLATE);

        nodeImagesHelper = new NodeImagesHelper(session);
    }

    @Override
    public void setKKStoreConfig(KKStoreConfig kkStoreConfig) {
        setContentRoot(kkStoreConfig.getContentRoot());
        setGalleryRoot(kkStoreConfig.getGalleryRoot());
        setProductFolder(kkStoreConfig.getProductFolder());
        // Create the reviews' folder if not exists
        createReviewFolder(kkStoreConfig.getReviewFolder());
    }



    @Override
    public void add(String storeId, Product product, LanguageIf language, String baseImagePath) throws Exception {

        KKCndConstants.PRODUCT_TYPE product_type = KKCndConstants.PRODUCT_TYPE.findByType(product.getType());

        String kkProductTypeName = product_type.getName();
        String productDocType = HippoModuleConfig.getConfig().getClientEngineConfig().getProductNodeTypeMapping().get(product_type.getNamespace());


        if (StringUtils.isEmpty(productDocType)) {
            log.error("No product namespace has been associated for the product namespace : "
                    + product_type.getNamespace() + ". Please set the it within the pluginconfig located " +
                    "at /hippo:configuration/hippo:frontend/cms/cms-services/KonakartSynchronizationService/producttypenamespaces");
            return;
        }

        String absPath = contentRoot + Codecs.encodeNode(productFolder) + "/" + Codecs.encodeNode(kkProductTypeName)
                + "/" + createProductNodeRoot(product);

        // Create or retrieve the root folder
        Node rootFolder;

        if (session.getRootNode().hasNode(StringUtils.removeStart(absPath, "/"))) {
            rootFolder = session.getNode(absPath);
        } else {
            absPath = contentRoot + productFolder + "/" + kkProductTypeName
                    + "/" + createProductNodeRoot(product);

            rootFolder = nodeHelper.createMissingFolders(absPath);
        }

        // Create or retrieve the product node
        Node productNode = nodeHelper.createOrRetrieveDocument(rootFolder, product, productDocType,
                session.getUserID(), language.getCode());

        boolean addNewProduct = !productNode.hasNode(KKCndConstants.PRODUCT_DESCRIPTION);
        boolean hasCheckout = false;

        // Check if the node is check-in
        if (!productNode.isCheckedOut()) {
            nodeHelper.checkout(productNode.getPath());
            hasCheckout = true;
        }

        // Set the state of the product
        String state = (product.getStatus() == 0) ? NodeHelper.UNPUBLISHED_STATE : NodeHelper.PUBLISHED_STATE;
        nodeHelper.updateState(productNode, state);

        // Update the node
        updateProperties(product, productNode);

        // Create the konakart ref product
        createOrUpdateKonakartProduct(storeId, product, productNode);

        // Upload images
        // Synchronize the image only during the creation of the product
        if (addNewProduct) {
            uploadImages(productNode, baseImagePath, kkProductTypeName, product);
        }

        // Save the session
        productNode.getSession().save();

        // Save the node
        if (hasCheckout) {
            nodeHelper.checkin(productNode.getPath());
        }

        if (addNewProduct) {
            if (log.isInfoEnabled()) {
                log.info("The konakart product with id : {} has been added", product.getId());
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("The konakart product with id : {} has been updated", product.getId());
            }
        }
    }

    /**
     * Create a unique page where the product will be created.
     *
     * @param product a product
     * @return a unique path where the product will be created
     */
    private String createProductNodeRoot(Product product) {

        // Get the manufacturer name
        String absPath = Codecs.encodeNode(product.getManufacturerName());

        // Get the creation time
        DateTime dateTime = new DateTime(product.getDateAdded().getTime().getTime());
        absPath += "/" + dateTime.getYear() + "/" + dateTime.getMonthOfYear();

        return absPath;
    }

    /**
     * Create or update the konakart node.
     *
     *
     * @param storeId     the store id
     * @param product     the konakart product
     * @param productNode the hippo product's node
     * @throws javax.jcr.RepositoryException if any exception occurs
     */
    private void createOrUpdateKonakartProduct(String storeId, Product product, Node productNode) throws RepositoryException {

        productNode.setProperty(KKCndConstants.PRODUCT_ID, product.getId());
        productNode.setProperty(KKCndConstants.PRODUCT_NAME, product.getName());
        productNode.setProperty(KKCndConstants.PRODUCT_SKU, product.getSku());
        productNode.setProperty(KKCndConstants.PRODUCT_MANUFACTURER, String.valueOf(product.getManufacturerId()));
        productNode.setProperty(KKCndConstants.PRODUCT_TAX_CLASS, String.valueOf(product.getTaxClassId()));


        List<Value> valueList = Lists.newArrayList();

        if (productNode.hasProperty(KKCndConstants.PRODUCT_STORE_ID)) {
            Value[] values = productNode.getProperty(KKCndConstants.PRODUCT_STORE_ID).getValues();

            boolean added = false;
            for (Value value : values) {
                if (StringUtils.equalsIgnoreCase(value.getString(), storeId)) {
                    added = true;
                }
            }

            valueList = Lists.newArrayList(values);

            if (!added) {
                valueList.add(session.getValueFactory().createValue(storeId));
            }
        } else {
            Value storeIdValue = session.getValueFactory().createValue(storeId);
            valueList.add(storeIdValue);


        }

        productNode.setProperty(KKCndConstants.PRODUCT_STORE_ID, valueList.toArray(new Value[valueList.size()]));

        if (product.getPrice0() != null) {
            productNode.setProperty(KKCndConstants.PRODUCT_PRICE_0, product.getPrice0().doubleValue());
        }

        if (product.getPrice1() != null) {
            productNode.setProperty(KKCndConstants.PRODUCT_PRICE_1, product.getPrice1().doubleValue());
        }

        if (product.getPrice2() != null) {
            productNode.setProperty(KKCndConstants.PRODUCT_PRICE_2, product.getPrice2().doubleValue());
        }

        if (product.getPrice3() != null) {
            productNode.setProperty(KKCndConstants.PRODUCT_PRICE_3, product.getPrice3().doubleValue());
        }

        productNode.setProperty(KKCndConstants.PRODUCT_QUANTITY, product.getQuantity());

        if (product.getWeight() != null) {
            productNode.setProperty(KKCndConstants.PRODUCT_WEIGHT, product.getWeight().doubleValue());
        }

        if (product.getCanOrderWhenNotInStock() != null) {
            productNode.setProperty(KKCndConstants.PRODUCT_ORDER_NOT_IN_STOCK, product.getCanOrderWhenNotInStock());
        } else {
            productNode.setProperty(KKCndConstants.PRODUCT_ORDER_NOT_IN_STOCK, Boolean.FALSE);
        }

        // Only synchronize the content from Konakart to Hippo during the creation of the product.
        if (!productNode.hasNode(KKCndConstants.PRODUCT_DESCRIPTION)) {
            Node descriptionNode = productNode.addNode(KKCndConstants.PRODUCT_DESCRIPTION, "hippostd:html");

            // Set the description property
            String description = "<html><body>";

            if (product.getDescription() != null) {
                description += product.getDescription();
            }

            description += "</body></html>";

            descriptionNode.setProperty("hippostd:content", description);
        }

        if (!productNode.hasNode(KKCndConstants.PRODUCT_ABSTRACT)) {
            Node descriptionNode = productNode.addNode(KKCndConstants.PRODUCT_ABSTRACT, "hippostd:html");

            // Set the description property
            String description = "<html><body></body></html>";

            descriptionNode.setProperty("hippostd:content", description);
        }
    }

    /**
     * Upload the images from Konakart to Hippo CMS
     *
     * @param productNode product node
     * @param baseImagePath path where the konakart's images are located
     * @param product the konakart product
     */
    private void uploadImages(Node productNode, String baseImagePath, String kkProductTypeName, Product product) {
        try {
            // Retrieve the gallery root node
            // add the product node root
            String galleryRootNode = galleryRoot + "/" + kkProductTypeName + "/" + createProductNodeRoot(product);

            // Get the root folder
            Node productGalleryNode = nodeImagesHelper.createMissingFolders(galleryRootNode);

            // upload base main image
            uploadImage(productNode, productGalleryNode, baseImagePath, product.getImage());

        } catch (Exception e) {
            log.error("Failed to add images", e);
        }
    }

    /**
     * Upload the image into the content's gallery
     *
     * @param productNode        the product node. Used to associate the image with the product's node
     * @param productGalleryNode the product gallery node
     * @param baseImagePath      the Konakart images' folder where the images are localed.
     * @param productImage       the name of the product
     * @throws javax.jcr.RepositoryException .
     */
    private void uploadImage(Node productNode, Node productGalleryNode, String baseImagePath, String productImage) throws RepositoryException {

        if (StringUtils.isEmpty(productImage)) {
            return;
        }

        String image = baseImagePath + "/" + productImage;

        File file = new File(image);

        if (!file.exists()) {
            log.warn("Failed to import image. The image at the path {} has not been found. ", image);
            return;
        }

        Node rootImageNode = null;

        try {
            String fileName = file.getName();
            String contentType = new MimetypesFileTypeMap().getContentType(image);

            // Create the image name
            rootImageNode = nodeImagesHelper.createGalleryItem(productGalleryNode, fileName);

            final ScalingGalleryProcessor processor = new ScalingGalleryProcessor();

            // for each version, create a new image.
            for (String imagesVersionName : GalleryProcesssorConfig.getConfig().getImagesVersionSet()) {

                // Create the image's node if not exist
                if (rootImageNode.hasNode(imagesVersionName)) {
                    rootImageNode.getNode(imagesVersionName).remove();
                }

                if (!rootImageNode.hasNode(imagesVersionName)) {
                    InputStream isStream = new FileInputStream(file);

                    GalleryProcesssorConfig.ImageConfig thumbnailImageConfig =
                            GalleryProcesssorConfig.getConfig().getImageConfigMap(imagesVersionName);

                    ScalingParameters parameters = new ScalingParameters(thumbnailImageConfig.getWidth(),
                            thumbnailImageConfig.getHeight(), thumbnailImageConfig.getUpscaling());

                    processor.addScalingParameters(imagesVersionName, parameters);

                    Node node = rootImageNode.addNode(imagesVersionName, "hippogallery:image");
                    processor.initGalleryResource(node, isStream, contentType, fileName, Calendar.getInstance());
                }
            }


        } catch (FileNotFoundException e) {
            // should not happends. Already verified.
        } catch (RepositoryException e) {
            log.warn("Unable to create the different versions of the original image - {} ", e);
        }

        // Save the image
        if (rootImageNode != null) {
            rootImageNode.getSession().save();
        }

        // Associate the image handle with the product
        if (rootImageNode != null) {
            Node imageLink = null;

            // Retrieve the image UUID
            String imageUUID = rootImageNode.getParent().getIdentifier();

            // Check if the image has been already added
            if (productNode.hasNode(KKCndConstants.PRODUCT_IMAGES)) {
                NodeIterator iterator = productNode.getNodes(KKCndConstants.PRODUCT_IMAGES);

                while (iterator.hasNext()) {
                    Node node = iterator.nextNode();

                    if (node.hasProperty("hippo:docbase")) {
                        String docbase = node.getProperty("hippo:docbase").getString();

                        if (docbase.contains(imageUUID)) {
                            imageLink = node;
                        }
                    }
                }
            }
            if (imageLink == null) {
                imageLink = productNode.addNode(KKCndConstants.PRODUCT_IMAGES, "hippogallerypicker:imagelink");
            }

            imageLink.setProperty("hippo:docbase", imageUUID);
            imageLink.setProperty("hippo:facets", new String[0]);
            imageLink.setProperty("hippo:values", new String[0]);
            imageLink.setProperty("hippo:modes", new String[0]);


        } else {
            log.warn("Node has not been created for the image name - " + productImage);
        }
    }


    /**
     * @param contentRoot set the content root where the document will be created.
     */
    private void setContentRoot(String contentRoot) {
        if (!contentRoot.endsWith("/")) {
            contentRoot = contentRoot + "/";
        }

        this.contentRoot = contentRoot;

    }

    /**
     * @param galleryRoot set the gallery root where the images will be saved
     */
    private void setGalleryRoot(String galleryRoot) {

        if (!galleryRoot.endsWith("/")) {
            galleryRoot = galleryRoot + "/";
        }

        this.galleryRoot = galleryRoot;

    }

    /**
     * @param productFolder set the name of the folder where the product will be created
     */
    private void setProductFolder(String productFolder) {
        this.productFolder = productFolder;
    }

    /**
     * Create a review's folder
     * @param reviewFolder the review's folder to create
     * @return the encoded review folder name
     */
    private String createReviewFolder(String reviewFolder) {

        if (StringUtils.isEmpty(reviewFolder)) {
            reviewFolder = KKCndConstants.DEFAULT_REVIEWS_FOLDER;
        }

        try {
            nodeHelper.createMissingFolders(contentRoot + reviewFolder);
        } catch (Exception e) {
            log.error("Failed to create the review folder.", e);
        }

        return Codecs.encodeNode(reviewFolder);
    }



}
