package org.onehippo.forge.konakart.replication.factory;

import com.konakart.app.Product;
import com.konakart.appif.LanguageIf;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.value.DoubleValue;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingGalleryProcessor;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingParameters;
import org.joda.time.DateTime;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.replication.jcr.GalleryProcesssorConfig;
import org.onehippo.forge.konakart.replication.utils.Codecs;
import org.onehippo.forge.konakart.replication.utils.NodeHelper;
import org.onehippo.forge.konakart.replication.utils.NodeImagesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;

/**
 * To use the replication plugin, you need to add a class that will extend this class to define
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
    private String productDocType;
    private String kkProductTypeName;
    private String konakartProductPropertyName;

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
        nodeImagesHelper = new NodeImagesHelper(session);
    }


    @Override
    public void setContentRoot(String contentRoot) {
        if (!contentRoot.endsWith("/")) {
            contentRoot = contentRoot + "/";
        }

        this.contentRoot = contentRoot;

    }

    @Override
    public void setGalleryRoot(String galleryRoot) {

        if (!galleryRoot.endsWith("/")) {
            galleryRoot = galleryRoot + "/";
        }

        this.galleryRoot = galleryRoot;

    }

    @Override
    public void setProductFolder(String productFolder) {
        this.productFolder = productFolder;
    }

    @Override
    public String createReviewFolder(String reviewFolder) throws Exception {

        if (StringUtils.isEmpty(reviewFolder)) {
            reviewFolder = KKCndConstants.DEFAULT_REVIEWS_FOLDER;
        }

        nodeHelper.createMissingFolders(contentRoot + reviewFolder);

        return Codecs.encodeNode(reviewFolder);
    }

    @Override
    public void setKonakartProductPropertyName(String konakartProductPropertyName) {
        this.konakartProductPropertyName = konakartProductPropertyName;
    }

    @Override
    public void setProductDocType(String productDocType) {
        this.productDocType = productDocType;
    }

    @Override
    public void setKKProductTypeName(String productTypeName) {
        this.kkProductTypeName = productTypeName;
    }

    @Override
    public void add(Product product, LanguageIf language, String baseImagePath) throws Exception {

        String absPath = contentRoot + Codecs.encodeNode(productFolder) + "/" + Codecs.encodeNode(kkProductTypeName)
                + "/" + createProductNodeRoot(product);

        // Create or retrieve the root folder
        Node rootFolder;

        if (session.getRootNode().hasNode(StringUtils.removeStart(absPath, "/"))) {
            rootFolder = session.getNode(absPath);
        } else {
            rootFolder = nodeHelper.createMissingFolders(absPath);
        }

        // Create or retrieve the product node
        Node productNode = nodeHelper.createOrRetrieveDocument(rootFolder, product, productDocType,
                        session.getUserID(), language.getCode());

        boolean addNewProduct = !productNode.hasNode(konakartProductPropertyName);

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
        createOrUpdateKonakartProduct(product, productNode, language.getId());

        // Upload images
        // Synchronize the image only during the creation of the product
        if (addNewProduct) {
            uploadImages(productNode.getNode(konakartProductPropertyName), baseImagePath, product);
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
     * @param product     the konakart product
     * @param productNode the hippo product's node
     * @param languageId  language's id within konakart
     * @throws RepositoryException if any exception occurs
     */
    private void createOrUpdateKonakartProduct(Product product, Node productNode, int languageId) throws RepositoryException {

        Node konakartNode;
        Node descriptionNode;
        Node standardPriceNode;

        boolean creationMode = false;

        // Create the node
        if (!productNode.hasNode(konakartProductPropertyName)) {
            creationMode = true;

            konakartNode = productNode.addNode(konakartProductPropertyName, KKCndConstants.PRODUCT_DOC_TYPE);
            konakartNode.setProperty(KKCndConstants.PRODUCT_ID, (long) product.getId());
            descriptionNode = konakartNode.addNode(KKCndConstants.PRODUCT_DESCRIPTION, "hippostd:html");
            standardPriceNode = konakartNode.addNode(KKCndConstants.PRODUCT_STANDARD_PRICE, KKCndConstants.CP_PRICE_TYPE);
        } else {
            konakartNode = productNode.getNode(konakartProductPropertyName);
            descriptionNode = konakartNode.getNode(KKCndConstants.PRODUCT_DESCRIPTION);
            standardPriceNode = konakartNode.getNode(KKCndConstants.PRODUCT_STANDARD_PRICE);
        }

        konakartNode.setProperty(KKCndConstants.PRODUCT_NAME, product.getName());
        konakartNode.setProperty(KKCndConstants.PRODUCT_SKU, product.getSku());
        konakartNode.setProperty(KKCndConstants.PRODUCT_LANGUAGE_ID, languageId);
        konakartNode.setProperty(KKCndConstants.PRODUCT_MANUFACTURER, product.getManufacturerName());
        konakartNode.setProperty(KKCndConstants.PRODUCT_PRODUCT_TYPE, KKCndConstants.PRODUCT_TYPE.findByType(product.getType()).getName());
        konakartNode.setProperty(KKCndConstants.PRODUCT_MANUFACTURER_ID, product.getManufacturerId());
        konakartNode.setProperty(KKCndConstants.PRODUCT_STORE_ID, product.getStoreId());

        if (product.getPrice0() != null) {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_0, new DoubleValue(product.getPrice0().doubleValue()));
        } else {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_0, new DoubleValue(0));
        }

        if (product.getPrice1() != null) {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_1, new DoubleValue(product.getPrice1().doubleValue()));
        } else {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_1, new DoubleValue(0));
        }

        if (product.getPrice2() != null) {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_2, new DoubleValue(product.getPrice2().doubleValue()));
        } else {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_2, new DoubleValue(0));
        }

        if (product.getPrice3() != null) {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_3, new DoubleValue(product.getPrice3().doubleValue()));
        } else {
            standardPriceNode.setProperty(KKCndConstants.CP_PRICE_3, new DoubleValue(0));
        }

        if (product.getSpecialPriceExTax() != null) {
            konakartNode.setProperty(KKCndConstants.PRODUCT_SPECIAL_PRICE, product.getSpecialPriceExTax().doubleValue());
        }


        // Only synchronize the content from Konakart to Hippo during the creation of the product.
        if (creationMode) {
            // Set the description property
            String description = "<html><body>";

            if (product.getDescription() != null) {
                description += product.getDescription();
            }

            description += "</body></html>";

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
    private void uploadImages(Node productNode, String baseImagePath, Product product) {
        try {
            // Retrieve the gallery root node
            // add the product node root
            String galleryRootNode = galleryRoot + productFolder + "/" + kkProductTypeName + "/" + createProductNodeRoot(product);

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
     * @throws RepositoryException .
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


}
