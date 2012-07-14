package org.onehippo.cms7.hst.hippokart.components;

import javax.jcr.Session;
import javax.servlet.ServletContext;

import org.hippoecm.hst.configuration.hosting.Mount;
import org.hippoecm.hst.content.beans.manager.ObjectConverter;
import org.hippoecm.hst.content.beans.standard.HippoBean;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.hippoecm.hst.core.request.ComponentConfiguration;
import org.hippoecm.hst.core.request.HstRequestContext;
import org.hippoecm.hst.core.request.ResolvedMount;
import org.hippoecm.hst.core.request.ResolvedSiteMapItem;
import org.hippoecm.hst.mock.content.beans.standard.MockHippoBean;
import org.hippoecm.hst.mock.core.component.MockHstRequest;
import org.hippoecm.hst.mock.core.request.MockComponentConfiguration;
import org.hippoecm.hst.mock.core.request.MockHstRequestContext;
import org.hippoecm.hst.mock.core.request.MockResolvedSiteMapItem;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertNull;

/**
 * Test class for {@link Detail}
 */
public class DetailTest {

    @Test
    public void doBeforeRender_FoundBean() throws Exception {
        HippoBean detailBean = new MockHippoBean();

        ServletContext servletContext = createMock(ServletContext.class);
        ComponentConfiguration componentConfiguration = new MockComponentConfiguration();
        ObjectConverter objectConverter = createMock(ObjectConverter.class);

        HstRequest request = new MockHstRequest();
        Session session = createMock(Session.class);
        HstResponse response = createMock(HstResponse.class);

        HstRequestContext requestContext = new MockHstRequestContext();
        ((MockHstRequestContext) requestContext).setSession(session);
        ResolvedMount resolvedMount = createMock(ResolvedMount.class);
        Mount mount = createMock(Mount.class);
        ((MockHstRequestContext) requestContext).setResolvedMount(resolvedMount);

        ResolvedSiteMapItem resolvedSiteMapItem = new MockResolvedSiteMapItem();
        ((MockResolvedSiteMapItem) resolvedSiteMapItem).setRelativeContentPath("common/detail");
        ((MockHstRequestContext) requestContext).setResolvedSiteMapItem(resolvedSiteMapItem);
        ((MockHstRequest) request).setRequestContext(requestContext);

        expect(resolvedMount.getMount()).andReturn(mount);
        expect(mount.getContentPath()).andReturn("/hst:hst/hst:sites/mysite-live/hst:content");
        expect(servletContext.getAttribute("org.hippoecm.hst.component.support.bean.BaseHstComponent.objectConverter")).andReturn(objectConverter).anyTimes();

        expect(objectConverter.getObject(session, "/hst:hst/hst:sites/mysite-live/hst:content/common/detail")).andReturn(detailBean);

        replay(servletContext, objectConverter, resolvedMount, mount, response, session);

        Detail detail = new Detail();
        detail.init(servletContext, componentConfiguration);
        detail.doBeforeRender(request, response);
        verify(servletContext, objectConverter, resolvedMount, mount, response, session);

        assertEquals(detailBean, request.getAttribute("document"));
    }

    @Test
    public void doBeforeRender_MissingBean() throws Exception {

        ServletContext servletContext = createMock(ServletContext.class);
        ComponentConfiguration componentConfiguration = new MockComponentConfiguration();
        ObjectConverter objectConverter = createMock(ObjectConverter.class);

        HstRequest request = new MockHstRequest();
        Session session = createMock(Session.class);
        HstResponse response = createMock(HstResponse.class);

        HstRequestContext requestContext = new MockHstRequestContext();
        ((MockHstRequestContext) requestContext).setSession(session);
        ResolvedMount resolvedMount = createMock(ResolvedMount.class);
        Mount mount = createMock(Mount.class);
        ((MockHstRequestContext) requestContext).setResolvedMount(resolvedMount);

        ResolvedSiteMapItem resolvedSiteMapItem = new MockResolvedSiteMapItem();
        ((MockResolvedSiteMapItem) resolvedSiteMapItem).setRelativeContentPath("common/detail");
        ((MockHstRequestContext) requestContext).setResolvedSiteMapItem(resolvedSiteMapItem);
        ((MockHstRequest) request).setRequestContext(requestContext);

        expect(resolvedMount.getMount()).andReturn(mount);
        expect(mount.getContentPath()).andReturn("/hst:hst/hst:sites/mysite-live/hst:content");
        expect(servletContext.getAttribute("org.hippoecm.hst.component.support.bean.BaseHstComponent.objectConverter")).andReturn(objectConverter).anyTimes();

        expect(objectConverter.getObject(session, "/hst:hst/hst:sites/mysite-live/hst:content/common/detail")).andReturn(null);

        response.setStatus(HstResponse.SC_NOT_FOUND);
        expectLastCall();
        replay(servletContext, objectConverter, resolvedMount, mount, response, session);

        Detail detail = new Detail();
        detail.init(servletContext, componentConfiguration);
        detail.doBeforeRender(request, response);
        verify(servletContext, objectConverter, resolvedMount, mount, response, session);

        assertNull(request.getAttribute("document"));
    }
}
