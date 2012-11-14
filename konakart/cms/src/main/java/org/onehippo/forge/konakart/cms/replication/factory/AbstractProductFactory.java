package org.onehippo.forge.konakart.cms.replication.factory;

import com.konakart.app.Product;
import com.konakart.appif.LanguageIf;
import eu.medsea.mimeutil.MimeUtil2;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingGalleryProcessor;
import org.hippoecm.frontend.plugins.gallery.processor.ScalingParameters;
import org.joda.time.DateTime;
import org.onehippo.forge.konakart.cms.replication.jcr.GalleryProcesssorConfig;
import org.onehippo.forge.konakart.cms.replication.utils.Codecs;
import org.onehippo.forge.konakart.cms.replication.utils.NodeHelper;
import org.onehippo.forge.konakart.cms.replication.utils.NodeImagesHelper;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.onehippo.forge.konakart.common.engine.KKStoreConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * To use the replication synchronization, you need to add a class that will extend this class to define
 * the following information :
 * - ProductDocType : Define the name of the document type which defines a product.
 * - KonakartProductPropertyName : Define the name of the node which contains the konakart product
 * <p/>
 */
public abstract class AbstractProductFactory implements ProductFactory {

    private static Logger log = LoggerFactory.getLogger(AbstractProductFactory.class);

    protected  MimeUtil2 mimeUtil = new MimeUtil2();

    protected Session session;
    private NodeHelper nodeHelper;
    private NodeImagesHelper nodeImagesHelper;


    private String contentRoot;
    private String galleryRoot;
    private String productFolder;

    protected AbstractProductFactory() {
        // Register the mime detector
        mimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
    }

    /**
     * This is an helper method that could be used to set others information defined into Konakart but not
     * already integrated within the konakart:konakart document.
     *
     * @param storeId  the store id - Useful to retrieve the client engine
     * @param product the konakart's product
     * @param node    the product's node
     * @param language the product's language
     */
    protected abstract void updateProperties(String storeId, Product product, Node node, LanguageIf language);


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
    }


    @Override
    public void add(String storeId, Product product, LanguageIf language, String baseImagePath) throws Exception {


        KKCndConstants.PRODUCT_TYPE product_type = KKCndConstants.PRODUCT_TYPE.findByType(product.getType());

        String productDocType = HippoModuleConfig.getConfig().getClientEngineConfig().getProductNodeTypeMapping().get(product_type.getNamespace());


        if (StringUtils.isEmpty(productDocType)) {
            log.error("No product namespace has been associated for the product namespace : "
                    + product_type.getNamespace() + ". Please set the it within the pluginconfig located " +
                    "at " + HippoModuleConfig.KONAKART_PRODUCT_TYPE_NAMESPACES_PATH);
            return;
        }

        String absPath = contentRoot + Codecs.encodeNode(productFolder) + "/" + createProductNodeRoot(product);

        // Create or retrieve the root folder
        Node rootFolder;

        if (session.getRootNode().hasNode(StringUtils.removeStart(absPath, "/"))) {
            rootFolder = session.getNode(absPath);
        } else {
            absPath = contentRoot + Codecs.encodeNode(productFolder) + "/" + "/" + createProductNodeRoot(product);

            rootFolder = nodeHelper.createMissingFolders(absPath);
        }

        // Create or retrieve the product node
        Node productNode = nodeHelper.createOrRetrieveDocument(rootFolder, product, productDocType,
                session.getUserID(), language.getLocale());

        boolean addNewProduct = !productNode.hasNode(KKCndConstants.PRODUCT_ID);
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
        updateProperties(storeId, product, productNode, language);

        // Create the konakart ref product
        createOrUpdateKonakartProduct(product, productNode);

        // Upload images
        uploadImages(language, productNode, baseImagePath, product);

        // Save the session
        productNode.getSession().save();

        // Save the node
        if (hasCheckout) {
            nodeHelper.checkin(productNode.getPath());
        }

        if (addNewProduct) {
            if (log.isDebugEnabled()) {
                log.debug("The konakart product with id : {} has been added", product.getId());
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("The konakart product with id : {} has been updated", product.getId());
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
        absPath += "/" + dateTime.getYear() + "/" + dateTime.getMonthOfYear() +  "/" + product.getName().substring(0, 1);

        return absPath;
    }

    /**
     * Create or update the konakart node.
     *
     * @param product     the konakart product
     * @param productNode the hippo product's node
     * @throws javax.jcr.RepositoryException if any exception occurs
     */
    private void createOrUpdateKonakartProduct(Product product, Node productNode) throws RepositoryException {

        productNode.setProperty(KKCndConstants.PRODUCT_ID, product.getId());
    }

    /**
     * Upload the images from Konakart to Hippo CMS
     *
     * @param language      the current language
     * @param productNode   product node
     * @param baseImagePath path where the konakart's images are located
     * @param product       the konakart product
     */
    private void uploadImages(LanguageIf language, Node productNode, String baseImagePath, Product product) {
        try {
            // Retrieve the gallery root node
            // add the product node root
            String galleryRootNode = galleryRoot + "/" + Codecs.encodeNode(productFolder) + "/" +
                    createProductNodeRoot(product) + "/" + product.getName() + "/" + product.getId();

            // Get the root folder
            Node productGalleryNode = nodeImagesHelper.createMissingFolders(galleryRootNode);

            // Get the list of images to updload
            Collection<String> images = getImagesByLanguage(product, language);

            uploadImages(productNode, productGalleryNode, baseImagePath, images);

        } catch (Exception e) {
            log.error("Failed to add images", e);
        }
    }

    @Override
    public Collection<String> getImagesByLanguage(Product product, LanguageIf language) {
        List<String> images = new ArrayList<String>();

        if (StringUtils.isNotEmpty(product.getImage())) {
            images.add(product.getImage());
        }

        if (StringUtils.isNotEmpty(product.getImage2())) {
            images.add(product.getImage2());
        }

        if (StringUtils.isNotEmpty(product.getImage3())) {
            images.add(product.getImage3());
        }

        if (StringUtils.isNotEmpty(product.getImage4())) {
            images.add(product.getImage4());
        }

        return images;
    }

    /**
     * Upload the image into the content's gallery
     *
     * @param productNode        the product node. Used to associate the image with the product's node
     * @param productGalleryNode the product gallery node
     * @param baseImagePath      the Konakart images' folder where the images are localed.
     * @param productImages      list of images to upload
     * @throws javax.jcr.RepositoryException .
     */
    private void uploadImages(Node productNode, Node productGalleryNode, String baseImagePath,
                              Collection<String> productImages) throws RepositoryException {

        for (String productImage : productImages) {
            String image = baseImagePath + "/" + productImage;

            String productImageName = productImage;

            if (StringUtils.contains(productImage, "/")) {
                productImageName = StringUtils.substringAfterLast(productImage, "/");
            }

            File file = new File(image);

            if (!file.exists()) {
                if (!StringUtils.equals(file.getName(), "none.png")) {
                    log.warn("Failed to import image. The image at the path {} has not been found. ", image);
                }
                return;
            }

            Node rootImageNode = null;

            try {
                String contentType = MimeUtil2.getMostSpecificMimeType(mimeUtil.getMimeTypes(file)).toString();

                // Create the image name
                rootImageNode = nodeImagesHelper.createGalleryItem(productGalleryNode, getImageNodeTypeName(), productImageName);

                final ScalingGalleryProcessor processor = new ScalingGalleryProcessor();

                // for each version, create a new image.
                for (String imagesVersionName : GalleryProcesssorConfig.getConfig().getImagesVersionSet()) {

                    // Create the image's node if not exist
                    if (rootImageNode.hasNode(imagesVersionName)) {
                        rootImageNode.getNode(imagesVersionName).remove();
                    }

                    if (!rootImageNode.hasNode(imagesVersionName)) {
                        InputStream isStream = new FileInputStream(file);

                        GalleryProcesssorConfig.ImageConfig imageConfig =
                                GalleryProcesssorConfig.getConfig().getImageConfigMap(imagesVersionName);

                        ScalingParameters parameters = new ScalingParameters(imageConfig.getWidth(),
                                imageConfig.getHeight(), imageConfig.getUpscaling(),
                                imageConfig.getScalingStrategy(), imageConfig.getCompression());

                        processor.addScalingParameters(imagesVersionName, parameters);

                        Node node = rootImageNode.addNode(imagesVersionName, "hippogallery:image");
                        processor.initGalleryResource(node, isStream, contentType, productImageName, Calendar.getInstance());
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
     * @return The name of the primary node type of the new image.
     */
    public String getImageNodeTypeName() {
        return "hippogallery:imageset";
    }
}
