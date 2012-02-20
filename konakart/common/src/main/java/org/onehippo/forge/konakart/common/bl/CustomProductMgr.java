package org.onehippo.forge.konakart.common.bl;

import com.konakart.app.KKException;
import com.konakart.appif.DataDescriptorIf;
import com.konakart.appif.FetchProductOptionsIf;
import com.konakart.appif.KKEngIf;
import com.konakart.bl.KKCriteria;
import com.konakart.bl.ProductMgr;
import com.konakart.blif.ProductMgrIf;
import com.konakart.om.BaseProductsPeer;
import com.workingdogs.village.DataSetException;
import org.apache.torque.TorqueException;
import org.apache.torque.util.BasePeer;
import org.apache.torque.util.Criteria;
import org.apache.torque.util.SqlEnum;

import java.util.Date;

public class CustomProductMgr extends ProductMgr implements ProductMgrIf {

    private Date lastUpDate;

    public CustomProductMgr(KKEngIf kkEngIf, Date lastUpdate) throws Exception {
        super(kkEngIf);

        this.lastUpDate = lastUpdate;
    }

    @Override
    protected void setCriteriaWithStandardAttributes(KKCriteria kkCriteria, int i, boolean b, boolean b1, FetchProductOptionsIf fetchProductOptionsIf, DataDescriptorIf dataDescriptorIf) throws TorqueException, KKException, DataSetException, Exception {
        super.setCriteriaWithStandardAttributes(kkCriteria, i, b, b1, fetchProductOptionsIf, dataDescriptorIf);

        if (lastUpDate != null) {
            Criteria.Criterion lastModifiedDate = kkCriteria.getNewCriterion(BaseProductsPeer.PRODUCTS_LAST_MODIFIED,
                    lastUpDate, SqlEnum.GREATER_EQUAL);

            Criteria.Criterion lastAddedDate = kkCriteria.getNewCriterion(BaseProductsPeer.PRODUCTS_DATE_ADDED, lastUpDate, SqlEnum.GREATER_EQUAL);

            kkCriteria.add(lastModifiedDate.or(lastAddedDate));
        }

        kkCriteria.remove(BaseProductsPeer.PRODUCTS_STATUS);
    }


    /**
     * 
     * Update the product with UUID of the Hippo node. This is used to synchronize Hippo and Konakart
     *
     * @param productId id of the product to update
     * @param uuid the uuid
     * @throws Exception if any exception occurs
     */
    public void updateUUID(Integer productId, String uuid) throws Exception {
        KKCriteria localKKCriteria = getNewCriteria(isMultiStoreShareProducts());
        localKKCriteria.add(BaseProductsPeer.PRODUCTS_ID, productId);
        localKKCriteria.add(BaseProductsPeer.CUSTOM1, uuid);

        BasePeer.doUpdate(localKKCriteria);
    }

    /**
     * Update the following information
     * @param productId id of the product to update
     * @param uuid the node's UUID
     * @param publishedState true if the product is in a published state, false otherwise
     */
    public void updateProduct(Integer productId, String uuid, boolean publishedState) throws Exception {
        KKCriteria localKKCriteria = getNewCriteria(isMultiStoreShareProducts());
        localKKCriteria.add(BaseProductsPeer.PRODUCTS_ID, productId);
        localKKCriteria.add(BaseProductsPeer.CUSTOM1, uuid);
        localKKCriteria.add(BaseProductsPeer.PRODUCTS_STATUS, publishedState);

        BasePeer.doUpdate(localKKCriteria);
    }
}