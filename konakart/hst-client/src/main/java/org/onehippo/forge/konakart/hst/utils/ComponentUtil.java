package org.onehippo.forge.konakart.hst.utils;

import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentUtil {

    private static final Logger log = LoggerFactory.getLogger(ComponentUtil.class);

    private static final String REDIRECTION_ON_WRONG_LANDING_MOUNT_DONE_ATTR = ComponentUtil.class.getName() + ".redirectionOnWrongLandingMount.done";

    private ComponentUtil() {
    }

    public static int getIntConfigurationParameter(final HstRequest request, final String param, final int defaultValue) {
        String paramValue = request.getParameter(param);
        if (paramValue != null) {
            try {
                return Integer.parseInt(paramValue);
            } catch (NumberFormatException nfe) {
                log.error("Error in parsing " + paramValue + " to integer for param " + param, nfe);
            }
        }
        return defaultValue;
    }

    public static int parseIntParameter(String name, String value, int defaultValue, Logger log) {
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("Illegal value for parameter '" + name + "': " + value);
            }
        }
        return defaultValue;
    }

    public static boolean parseAscendingParameter(String name, String value, boolean defaultValue, Logger log) {
        if ("ascending".equals(value)) {
            return true;
        } else if ("descending".equals(value)) {
            return false;
        } else if (value != null && value.trim().length() > 0) {
            log.warn("Illegal value for parameter '" + name + "': " + value);
        }
        return defaultValue;
    }


    public static void doRedirectionOnWrongLocale(HstRequest request, HstResponse response) {
        HstRequestContext requestContext = request.getRequestContext();


        if (requestContext.getAttribute(REDIRECTION_ON_WRONG_LANDING_MOUNT_DONE_ATTR) != null) {
            return;
        }

        requestContext.setAttribute(REDIRECTION_ON_WRONG_LANDING_MOUNT_DONE_ATTR, Boolean.TRUE);


        ResolvedMount resolvedMount = requestContext.getResolvedMount();

        String locale = resolvedMount.getMount().getLocale();


    }


}
