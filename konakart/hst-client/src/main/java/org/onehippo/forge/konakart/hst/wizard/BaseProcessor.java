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

package org.onehippo.forge.konakart.hst.wizard;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.common.engine.KKActivityConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.utils.KKCheckoutConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Base class for all Workflow Processors. Responsible of keeping track of an ordered collection of
 * {@link Activity Activities}
 */
public abstract class BaseProcessor implements Processor {


    protected Logger log = LoggerFactory.getLogger(getClass());

    private List<Activity> activities = Lists.newLinkedList();

     /**
     * Default constructor.
     *
     * Load the list of activities
     */
    protected BaseProcessor() {
        loadActivities();
    }

    /*
    * Ensures the list of activities is not empty and each activity is supported by this Workflow Processor
    */
    public void loadActivities() {

        List<KKActivityConfig> kkActivityConfigs = HippoModuleConfig.getConfig().getCheckoutConfig().getActivityConfigList();

        if (kkActivityConfigs == null || kkActivityConfigs.isEmpty()) {
            throw new IllegalStateException("No activities were wired for this workflow");
        }

        for (KKActivityConfig kkActivityConfig : kkActivityConfigs) {

            Activity activity = instanciateActivity(kkActivityConfig.getActivityClass());

            if (!supports(activity)) {
                throw new IllegalStateException("The workflow processor [" + getClass().getSimpleName() + "] does " +
                        "not support the activity of type" + activity.getClass().getName());
            }

            activity.setAcceptEmptyState(kkActivityConfig.isAcceptEmptyState());
            activity.setAcceptState(kkActivityConfig.getAcceptState());
            activity.setNextLoggedState(kkActivityConfig.getNextLoggedState());
            activity.setNextNonLoggedState(kkActivityConfig.getNextNonLoggedState());
            activity.setTemplateRenderPath(kkActivityConfig.getTemplateRenderPath());

            activities.add(activity);
        }
    }


    /**
     * Sets the collection of Activities to be executed by the Workflow Process
     *
     * @param activities ordered collection (List) of activities to be executed by the processor
     */
    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }

    /**
     * @return the list of activities
     */
    public List<Activity> getActivities() {
        return activities;
    }


    /**
     * @param request the Hst Request
     * @return the current state
     */
    protected String getCurrentState(HstRequest request) {
        return request.getParameter(KKCheckoutConstants.STATE);
    }

    /**
     *
     * @param request the Hst request
     * @return the current action
     */
    protected String getCurrentAction(HstRequest request) {
        return request.getParameter(KKCheckoutConstants.ACTION);
    }

    /**
     * Valid if the customer has clicked on edit button
     * @return true if the customer has clicked on the edit button, false otherwise
     */
    protected boolean isEditAction(HstRequest request) {
        String currentAction = getCurrentAction(request);

        return (currentAction != null) && currentAction.equals(KKCheckoutConstants.ACTIONS.EDIT.name());
    }


    @Nonnull
    private Activity instanciateActivity(String activityClassName) {
        if (StringUtils.isNotBlank(activityClassName)) {
            try {
                return (Activity) Class.forName(activityClassName).newInstance();

            } catch (InstantiationException e) {
                log.error("Unable to find the extension class: " + e.toString());
            } catch (IllegalAccessException e) {
                log.error("Unable to find the extension class: " + e.toString());
            } catch (ClassNotFoundException e) {
                log.error("Unable to find the extension class: " + e.toString());
            }
        }

        throw new InstantiationError("Unable to create an instance of the class : " + activityClassName);
    }


}
