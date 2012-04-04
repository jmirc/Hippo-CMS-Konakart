package org.onehippo.forge.konakart.hst.wizard;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Base class for all Workflow Processors. Responsible of keeping track of an ordered collection of
 * {@link Activity Activities}
 */
public abstract class BaseProcessor implements InitializingBean, BeanNameAware, Processor {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private String beanName;
    private List<Activity> activities;

    /* Sets name of the spring bean in the application context that this
     * processor is configured under
     * (non-Javadoc)
     * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
     */
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    /**
     * @return the spring bean
     */
    public String getBeanName() {
        return beanName;
    }


    /*
     * Called after the properties have been set, Ensures the list of activities
     *  is not empty and each activity is supported by this Workflow Processor
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {

        if (activities == null || activities.isEmpty()) {
            throw new BeanInitializationException("No activities were wired for this workflow");
        }

        for (Activity activity : activities) {
            if (!supports(activity)) {
                throw new BeanInitializationException("The workflow processor [" + beanName + "] does " +
                        "not support the activity of type" + activity.getClass().getName());
            }
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

    public List<Activity> getActivities() {
        return activities;
    }
}
