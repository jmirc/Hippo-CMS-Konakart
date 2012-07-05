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

import org.hippoecm.hst.component.support.forms.FormMap;

import java.util.List;

public interface Processor {

    /**
     * To be implemented by subclasses, ensures each Activity configured in this process
     * is supported.  This method is called by the Processor
     * for each Activity configured.  An implementing subclass should ensure the Activity
     *  type passed in is supported.  Implementors could possibly support multiple
     * types of activities.
     *
     * @param activity - activity instance of the configured activity
     * @return true if the activity is supported
     */
    boolean supports(Activity activity);

    /**
     * Allows the activity to do some business logic processing before rendering
     *
     * @param seedObject - data necessary for the workflow process to start execution
     */
    void doBeforeRender(SeedData seedObject) throws ActivityException;

    /**
     * Allows the component to process actions
     *
     * @param seedObject - data necessary for the workflow process to start execution
     */
    FormMap doAction(SeedData seedObject) throws ActivityException;

    /**
     * Executed when an activity has been previously processed.
     * This method could be used to add into the request information to inform that
     * the activity is already past.
     */
    void doAdditionalData(SeedData seedObject) throws ActivityException;

    /**
     * Sets the collection of Activities to be executed by the Workflow Process
     *
     * @param activities ordered collection (List) of activities to be executed by the processor
     */
    void setActivities(List<Activity> activities);

}