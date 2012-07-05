/*
 * =========================================================
 * Hippo CMS - Konakart
 * https://bitbucket.org/jmirc/hippo-cms-konakart
 * =========================================================
 * Copyright 2012
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================================================
 */

package org.onehippo.forge.konakart.cms.replication.jcr;

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

            while(nodeIterator.hasNext()) {
                Node serviceNode = nodeIterator.nextNode();

                String serviceName = serviceNode.getName();

                ImageConfig imageConfig = new ImageConfig();
                imageConfig.setHeight(serviceNode.getProperty(ImageConfig.HEIGHT).getLong());
                imageConfig.setWidth(serviceNode.getProperty(ImageConfig.WIDTH).getLong());
                imageConfig.setUpscaling(serviceNode.getProperty(ImageConfig.UPSCALING).getBoolean());

                imageConfigMap.put(serviceName, imageConfig);

            }
        } catch (RepositoryException e) {
            log.error("Failed to load interspire configuration: " + e.toString());
        }
    }



    public static class ImageConfig {
        public static final String HEIGHT ="height";
        public static final String WIDTH ="width";
        public static final String UPSCALING ="upscaling";

        private Long height;
        private Long width;
        private Boolean upscaling;

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
    }





}
