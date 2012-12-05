package org.onehippo.forge.konakart.cms.replication.jcr;

import org.apache.commons.lang.StringUtils;
import org.hippoecm.frontend.plugins.gallery.imageutil.ImageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GalleryProcesssorConfig {

  public static final Logger log = LoggerFactory.getLogger(GalleryProcesssorConfig.class);

  public static final String SERVICE_CONFIG_PATH = "/hippo:configuration/hippo:frontend/cms/cms-services/galleryProcessorService/";

  private static GalleryProcesssorConfig config = new GalleryProcesssorConfig();


  private Map<String, ImageConfig> imageConfigMap = new HashMap<String, ImageConfig>();


  /**
   * @return the config class
   */
  public static GalleryProcesssorConfig getConfig() {
    return config;
  }

  /**
   * @return the image config.
   */
  public ImageConfig getImageConfigMap(String imageConfigName) {
    return imageConfigMap.get(imageConfigName);
  }

  /**
   * @return the list of differents images' version
   */
  public Set<String> getImagesVersionSet() {
    return imageConfigMap.keySet();
  }

  /**
   * @param session a JCR session
   * @return an instance of the config
   */
  public static GalleryProcesssorConfig load(Session session) {

    if (session == null) {
      log.error("Failed to load the Konakart config. JCR Session is null");
      throw new RuntimeException("Failed to load the Konakart config. JCR Session is null");
    }

    // load configuration
    config.loadConfiguration(session);

    return config;
  }

  private void loadConfiguration(Session session) {

    try {
      Node node = session.getNode(SERVICE_CONFIG_PATH);

      NodeIterator nodeIterator = node.getNodes();

      while (nodeIterator.hasNext()) {
        Node serviceNode = nodeIterator.nextNode();

        String serviceName = serviceNode.getName();

        ImageConfig imageConfig = new ImageConfig();

        if (serviceNode.hasProperty(ImageConfig.HEIGHT)) {
          imageConfig.setHeight(serviceNode.getProperty(ImageConfig.HEIGHT).getLong());
        }

        if (serviceNode.hasProperty(ImageConfig.WIDTH)) {
          imageConfig.setWidth(serviceNode.getProperty(ImageConfig.WIDTH).getLong());
        }

        if (serviceNode.hasProperty(ImageConfig.UPSCALING)) {
          imageConfig.setUpscaling(serviceNode.getProperty(ImageConfig.UPSCALING).getBoolean());
        }

        if (serviceNode.hasProperty(ImageConfig.OPTIMIZE)) {
          imageConfig.setScalingStrategy(serviceNode.getProperty(ImageConfig.OPTIMIZE).getString());
        }

        if (serviceNode.hasProperty(ImageConfig.COMPRESSION)) {
          imageConfig.setCompression(serviceNode.getProperty(ImageConfig.COMPRESSION).getDouble());
        }

        imageConfigMap.put(serviceName, imageConfig);

      }
    } catch (RepositoryException e) {
      log.error("Failed to load interspire configuration: " + e.toString());
    }
  }


  public static class ImageConfig {
    public static final String HEIGHT = "height";
    public static final String WIDTH = "width";
    public static final String UPSCALING = "upscaling";
    public static final String OPTIMIZE = "optimize";
    public static final String COMPRESSION = "compression";

    private Long height;
    private Long width;
    private Boolean upscaling;
    private ImageUtils.ScalingStrategy scalingStrategy = ImageUtils.ScalingStrategy.QUALITY;
    private Double compression = 1D;


    public int getHeight() {
      return height.intValue();
    }

    public void setHeight(Long height) {
      this.height = height;
    }

    public int getWidth() {
      return width.intValue();
    }

    public void setWidth(Long width) {
      this.width = width;
    }

    public boolean getUpscaling() {
      return upscaling;
    }

    public void setUpscaling(Boolean upscaling) {
      this.upscaling = upscaling;
    }

    public ImageUtils.ScalingStrategy getScalingStrategy() {
      return scalingStrategy;
    }

    public void setScalingStrategy(String scalingStrategy) {

      if (StringUtils.equalsIgnoreCase(scalingStrategy, ImageUtils.ScalingStrategy.SPEED.name())) {
        this.scalingStrategy = ImageUtils.ScalingStrategy.SPEED;
      }

      if (StringUtils.equalsIgnoreCase(scalingStrategy, ImageUtils.ScalingStrategy.SPEED_AND_QUALITY.name())) {
        this.scalingStrategy = ImageUtils.ScalingStrategy.SPEED_AND_QUALITY;
      }

      if (StringUtils.equalsIgnoreCase(scalingStrategy, ImageUtils.ScalingStrategy.QUALITY.name())) {
        this.scalingStrategy = ImageUtils.ScalingStrategy.QUALITY;
      }

      if (StringUtils.equalsIgnoreCase(scalingStrategy, ImageUtils.ScalingStrategy.AUTO.name())) {
        this.scalingStrategy = ImageUtils.ScalingStrategy.AUTO;
      }

      if (StringUtils.equalsIgnoreCase(scalingStrategy, ImageUtils.ScalingStrategy.BEST_QUALITY.name())) {
        this.scalingStrategy = ImageUtils.ScalingStrategy.BEST_QUALITY;
      }
    }

    public float getCompression() {
      return compression.floatValue();
    }

    public void setCompression(Double compression) {
      this.compression = compression;
    }
  }


}
