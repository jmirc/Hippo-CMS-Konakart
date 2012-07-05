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

package org.onehippo.forge.konakart.common.engine;

public class KKActivityConfig {

    private String activityClass;
    private boolean acceptEmptyState;
    private String acceptState;
    private String nextNonLoggedState;
    private String nextLoggedState;
    private String templateRenderpath;

    public String getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(String activityClass) {
        this.activityClass = activityClass;
    }

    public boolean isAcceptEmptyState() {
        return acceptEmptyState;
    }

    public void setAcceptEmptyState(boolean acceptEmptyState) {
        this.acceptEmptyState = acceptEmptyState;
    }

    public String getAcceptState() {
        return acceptState;
    }

    public void setAcceptState(String acceptState) {
        this.acceptState = acceptState;
    }

    public String getNextNonLoggedState() {
        return nextNonLoggedState;
    }

    public void setNextNonLoggedState(String nextNonLoggedState) {
        this.nextNonLoggedState = nextNonLoggedState;
    }

    public String getNextLoggedState() {
        return nextLoggedState;
    }

    public void setNextLoggedState(String nextLoggedState) {
        this.nextLoggedState = nextLoggedState;
    }

    public String getTemplateRenderPath() {
        return templateRenderpath;
    }

    public void setTemplateRenderpath(String templateRenderpath) {
        this.templateRenderpath = templateRenderpath;
    }
}
