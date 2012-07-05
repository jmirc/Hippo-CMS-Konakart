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

package org.onehippo.forge.konakart.site.service;

import com.konakart.appif.BasketIf;
import com.konakart.appif.OrderIf;
import org.hippoecm.hst.core.component.HstRequest;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface KKOrderService {

    /**
     * Populate checkout order with a temporary order created before the checkout process really
     * begins. If the customer hasn't registered or logged in yet, we use the default customer to
     * create the order.
     * <p/>
     * With this temporary order we can give the customer useful information on shipping costs and
     * discounts without him having to login.
     *
     * @param request the hst request
     * @param custId  the customer Id
     * @param items   the basket's items
     */
    @Nullable
    OrderIf createTempOrder(@Nonnull HstRequest request, int custId, final BasketIf[] items);

}
