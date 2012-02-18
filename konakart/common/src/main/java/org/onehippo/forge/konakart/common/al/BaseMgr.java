package org.onehippo.forge.konakart.common.al;

import com.konakart.appif.KKEngIf;
import org.onehippo.forge.konakart.common.engine.KKEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * All managers extend BaseMgr.
 */
public class BaseMgr {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected KKEngine kkEngine;
    protected KKEngIf kkEng;

    /**
     * Default constuctor
     * @param kkEngine the Konakart Engine
     */
    public BaseMgr(KKEngine kkEngine) {
        this.kkEngine = kkEngine;
        kkEng = kkEngine.getEngine();
    }
}
