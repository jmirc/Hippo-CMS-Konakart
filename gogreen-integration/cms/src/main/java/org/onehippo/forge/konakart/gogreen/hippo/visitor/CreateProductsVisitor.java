package org.onehippo.forge.konakart.gogreen.hippo.visitor;

import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.gogreen.database.helper.ProductHelper;
import org.onehippo.forge.konakart.gogreen.database.loader.CategoryLoader;
import org.onehippo.forge.konakart.gogreen.database.utils.LanguageUtil;
import org.onehippo.forge.konakart.gogreen.database.vo.ProductDescriptionVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.*;
import javax.jcr.nodetype.NodeTypeDefinition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class CreateProductsVisitor implements Visitor {

  public static final Logger log = LoggerFactory.getLogger(CreateProductsVisitor.class);


  private String imagesFolder;

  public CreateProductsVisitor(String imagesFolder) {
    this.imagesFolder = imagesFolder;
  }

  @Override
  public void visit(Node node) {
    try {
      NodeTypeDefinition nodeTypeDefinition = node.getPrimaryNodeType();

      if (nodeTypeDefinition.getName().equals("hippogogreen:product")) {

        // Get the name from the handle
        log.debug(node.getName());
        String name = node.getName();

        if (node.getParent().hasNode("hippo:translation")) {
          name = node.getParent().getNode("hippo:translation").getProperty("hippo:message").getString();
        } else {
          log.debug("No translation node");
        }

        ProductHelper productHelper = new ProductHelper();

        productHelper.setCreationDate(node.getProperty("hippostdpubwf:creationDate").getDate());
        productHelper.setLastModificationDate(node.getProperty("hippostdpubwf:lastModificationDate").getDate());
        productHelper.setPublicationDate(node.getProperty("hippostdpubwf:publicationDate").getDate());


        productHelper.setName(name);

        // Set the categories
        Property categoriesProp = node.getProperty("hippogogreen:categories");

        List<Integer> cats = new ArrayList<Integer>();

        if (categoriesProp.isMultiple()) {
          Value[] categories = categoriesProp.getValues();


          for (Value category : categories) {
            int catId = CategoryLoader.getCategoryId(category.getString());

            if (catId != -1) {
              cats.add(catId);
            }
          }

        } else {
          String cat = categoriesProp.getString();

          // try to find the category by the name of the parent folders
          if (StringUtils.isBlank(cat)) {
            Node currentNode = node;
            Node parent = node.getParent();

            while (!StringUtils.equalsIgnoreCase(parent.getName(), "products")) {
              currentNode = parent;
              parent = parent.getParent();
            }

            cat = currentNode.getNode("hippo:translation").getProperty("hippo:message").getString();
          }

          int catId = CategoryLoader.getCategoryId(cat);

          if (catId != -1) {
            cats.add(catId);
          }

        }

        if (cats.isEmpty()) {
          cats.add(CategoryLoader.getCategoryId(CategoryLoader.DEFAULT_CAT));
        }

        productHelper.setCategories(cats);


        // Set the price
        productHelper.setPrice(node.getProperty("hippogogreen:price").getDouble());

        // Set the images
        NodeIterator imagesIterator = node.getNodes("hippogogreen:image");

        boolean hasImages = false;

        while (imagesIterator.hasNext()) {
          Node imageNodeDocBase = imagesIterator.nextNode();

          String id = imageNodeDocBase.getProperty("hippo:docbase").getString();

          Node imageHandleNode = node.getSession().getNodeByIdentifier(id);

          if (imageHandleNode.hasNode(imageHandleNode.getName())) {
            hasImages = true;


            String imageName = StringUtils.substringAfter(imageHandleNode.getPath(), imagesFolder);

            productHelper.addImage(imageName);
          }
        }

        List<String> supportedTranslations = new ArrayList<String>();
        supportedTranslations.addAll(Arrays.asList("en", "fr", "nl", "it", "de", "es", "zh", "ru"));

        Node translationsNode = node.getNode("hippotranslation:translations");

        NodeIterator nodeIterator = translationsNode.getNodes();

        while (nodeIterator.hasNext()) {
          Node translationNode = nodeIterator.nextNode();

          String translationNodeName = translationNode.getName();

          if (supportedTranslations.contains(translationNodeName)) {
            supportedTranslations.remove(translationNodeName);

            productHelper.addDescription(createDescription(translationNode, LanguageUtil.getMappingTranslations(translationNodeName)));
          }
        }


        // Generate garbage information for the translation that are not available
        for (String availableTranslation : supportedTranslations) {
          productHelper.addDescription(createNotAvailableDescription(availableTranslation));
        }


        // Don't sync products without any images.
        if (hasImages) {
          int productId = productHelper.process();

          ProductHelper.productsMapping.put(node.getParent().getIdentifier(), productId);
        }

      }
    } catch (RepositoryException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private ProductDescriptionVo createNotAvailableDescription(String availableTranslation) {
    ProductDescriptionVo description = new ProductDescriptionVo();

    description.setLocale(LanguageUtil.getMappingTranslations(availableTranslation));
    description.setTitle("NO TRANSLATION YET");
    description.setDescription("NO TRANSLATION YET");

    return description;
  }

  private ProductDescriptionVo createDescription(Node node, String locale) throws RepositoryException {
    ProductDescriptionVo description = new ProductDescriptionVo();

    description.setLocale(locale);

    description.setTitle(node.getProperty("hippogogreen:title").getString());

    String htmlDescription = node.getNode("hippogogreen:description").getProperty("hippostd:content").getString();

    htmlDescription = StringUtils.remove(htmlDescription, "<html>");
    htmlDescription = StringUtils.remove(htmlDescription, "</html>");
    htmlDescription = StringUtils.remove(htmlDescription, "<body>");
    htmlDescription = StringUtils.remove(htmlDescription, "</body>");

    description.setDescription(StringUtils.trim(htmlDescription));


    // Set the summary
    description.setSummary(node.getProperty("hippogogreen:summary").getString());


    return description;
  }


}
