package org.onehippo.forge.konakart.hst.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.bean.BaseHstComponent;
import org.hippoecm.hst.component.support.forms.FormField;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.content.beans.ObjectBeanManagerException;
import org.hippoecm.hst.content.beans.ObjectBeanPersistenceException;
import org.hippoecm.hst.content.beans.manager.ObjectBeanManager;
import org.hippoecm.hst.content.beans.manager.ObjectBeanManagerImpl;
import org.hippoecm.hst.content.beans.manager.ObjectConverter;
import org.hippoecm.hst.content.beans.manager.workflow.WorkflowPersistenceManager;
import org.hippoecm.hst.content.beans.query.HstQueryManager;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstComponentException;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.container.ComponentManager;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.search.HstQueryManagerFactory;
import org.hippoecm.hst.site.HstServices;
import org.hippoecm.hst.util.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;

public final class KKUtil {

    private static final Logger log = LoggerFactory.getLogger(KKUtil.class);

    private KKUtil() {
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


    /**
     * Returns null if parameter is empty string or  null, it escapes HTML otherwise
     *
     * @param request       hst request
     * @param parameterName name of the parameter
     * @return html escaped value
     */
    public static String getEscapedParameter(final HstRequest request, final String parameterName) {
        String value = request.getParameter(parameterName);
        if (value == null || value.trim().length() == 0) {
            return null;
        }
        return StringEscapeUtils.escapeHtml(value);
    }


    public static void refreshWorkflowManager(final WorkflowPersistenceManager wpm) {
        if (wpm != null) {
            try {
                wpm.refresh();
            } catch (ObjectBeanPersistenceException obpe) {
                log.warn("Failed to refresh: " + obpe.getMessage(), obpe);
            }
        }
    }

    /**
     * Check if a mandatory field is empty or not.
     *
     * @param formMap      the formMap
     * @param fieldName    the field name to check
     * @param errorMessage the error message
     * @return true if a mandatory is empty, false otherwise.
     */
    public static boolean checkMandatoryField(FormMap formMap, String fieldName, String errorMessage) {
        FormField formField = formMap.getField(fieldName);
        if ((formField == null) || StringUtils.isBlank(formField.getValue())) {
            formMap.addMessage(fieldName, errorMessage);
            return false;
        }

        return true;
    }

    /**
     * Get the site content base bean, which is the root document bean whithin
     * preview or live context.
     */
    @Nullable
    public static HippoBean getSiteContentBaseBean(@Nonnull final HstRequest request) {
        String base = getSiteContentBasePath(request);
        try {
            return (HippoBean) getObjectBeanManager(request).getObject("/" + base);
        } catch (ObjectBeanManagerException e) {
            log.error("ObjectBeanManagerException. Return null : {}", e);
        }
        return null;
    }

    @Nonnull
    public static String getSiteContentBasePath(@Nonnull final HstRequest request) {
        return PathUtils.normalizePath(request.getRequestContext().getResolvedMount().getMount().getContentPath());
    }

    @Nonnull
    public static ObjectBeanManager getObjectBeanManager(@Nonnull final HstRequest request) {
        try {
            HstRequestContext requestContext = request.getRequestContext();
            return new ObjectBeanManagerImpl(requestContext.getSession(), getObjectConverter(requestContext));
        } catch (UnsupportedRepositoryOperationException e) {
            throw new HstComponentException(e);
        } catch (RepositoryException e) {
            throw new HstComponentException(e);
        }
    }

    @Nonnull
    public static ObjectConverter getObjectConverter(@Nonnull final HstRequestContext requestContext) {
        // get the objectconverter that was put in servlet context by HstComponent
        return (ObjectConverter) requestContext.getServletContext().getAttribute(BaseHstComponent.OBJECT_CONVERTER_CONTEXT_ATTRIBUTE);
    }

    @Nonnull
    public static HstQueryManager getQueryManager(@Nonnull final HstRequestContext requestContext) {

        try {
            ComponentManager compMngr = HstServices.getComponentManager();
            if (compMngr != null) {
                HstQueryManagerFactory hstQueryManagerFactory = (HstQueryManagerFactory) compMngr.getComponent(HstQueryManagerFactory.class.getName());
                return hstQueryManagerFactory.createQueryManager(requestContext.getSession(), KKUtil.getObjectConverter(requestContext));
            }
        } catch (RepositoryException e) {
            log.error("Failed to retrieve the HstQueryManager component", e);
        }

        throw new IllegalStateException("Failed to retrieve the HstQueryManager component");
    }
}