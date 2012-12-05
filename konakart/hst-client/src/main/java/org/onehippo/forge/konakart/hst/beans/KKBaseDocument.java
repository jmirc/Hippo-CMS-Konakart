package org.onehippo.forge.konakart.hst.beans;

import org.hippoecm.hst.content.beans.Node;
import org.hippoecm.hst.content.beans.standard.HippoDocument;
import org.onehippo.forge.konakart.common.KKCndConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Node(jcrType = KKCndConstants.BASEDOCUMENT_DOC_TYPE)
public class KKBaseDocument extends HippoDocument {

  protected Logger log = LoggerFactory.getLogger(getClass());
}
