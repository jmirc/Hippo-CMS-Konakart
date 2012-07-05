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

package org.onehippo.forge.konakart.cms.replication.synchronization;

import org.hippoecm.repository.quartz.JCRSchedulingContext;
import org.quartz.Scheduler;
import org.quartz.core.QuartzScheduler;
import org.quartz.core.SchedulingContext;
import org.quartz.impl.StdScheduler;

import javax.jcr.Session;

public class KonakartResourceScheduler extends StdScheduler implements Scheduler {

    private QuartzScheduler qs;
    private SchedulingContext ctx;


    public KonakartResourceScheduler(QuartzScheduler sched, SchedulingContext schedCtxt) {
        super(sched, schedCtxt);

        this.qs = sched;
        this.ctx = schedCtxt;
    }

    public KonakartResourceScheduler(KonakartResourceScheduler sched, Session session) {
        super(sched.qs, new JCRSchedulingContext(sched.ctx, session));
    }

    public SchedulingContext getCtx() {
        return ctx;
    }

}
