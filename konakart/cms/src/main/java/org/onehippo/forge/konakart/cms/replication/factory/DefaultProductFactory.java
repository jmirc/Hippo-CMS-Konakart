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

package org.onehippo.forge.konakart.cms.replication.factory;

import com.konakart.app.Product;

import javax.jcr.Node;

/**
 * The default factory doesn't update any properties.
 *
 * This factory could be overridden if you want to add or to update properties
 */
public class DefaultProductFactory extends AbstractProductFactory {

    @Override
    protected void updateProperties(Product product, Node node) {
        // do nothing
    }
}
