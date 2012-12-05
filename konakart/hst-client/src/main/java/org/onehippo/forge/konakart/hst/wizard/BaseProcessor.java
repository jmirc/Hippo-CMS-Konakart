package org.onehippo.forge.konakart.hst.wizard;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.onehippo.forge.konakart.common.engine.KKActivityConfig;
import org.onehippo.forge.konakart.common.jcr.HippoModuleConfig;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.onehippo.forge.konakart.hst.utils.KKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Base class for all Workflow Processors. Responsible of keeping track of an ordered collection of
 * {@link org.onehippo.forge.konakart.hst.wizard.Activity Activities}
 */
public abstract class BaseProcessor implements Processor {


  protected Logger log = LoggerFactory.getLogger(getClass());

  private List<Activity> activities = Lists.newLinkedList();

  /**
   * Default constructor.
   * <p/>
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

      activity.setName(kkActivityConfig.getName());
      activity.setAcceptEmptyState(kkActivityConfig.isAcceptEmptyState());
      activity.setAcceptState(kkActivityConfig.getAcceptState());
      activity.setNextLoggedState(kkActivityConfig.getNextLoggedState());
      activity.setNextNonLoggedState(kkActivityConfig.getNextNonLoggedState());
      activity.setTemplateRenderPath(kkActivityConfig.getTemplateRenderPath());

      activities.add(activity);
    }
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
    return KKUtil.getActionRequestParameter(request, KKActionsConstants.STATE);
  }

  /**
   * @param request the Hst Request
   * @return the next state
   */
  protected String getNextState(HstRequest request) {
    String forceNextLoggedState = KKUtil.getActionRequestParameter(request, KKActionsConstants.FORCE_NEXT_LOGGED_STATE);

    if (StringUtils.isNotBlank(forceNextLoggedState)) {
      return forceNextLoggedState;
    }

    return getCurrentState(request);
  }


  /**
   * @param request the Hst request
   * @return the current action
   */
  protected String getCurrentAction(HstRequest request) {
    return request.getParameter(KKActionsConstants.ACTION);
  }

  /**
   * Valid if the customer has clicked on edit button
   *
   * @return true if the customer has clicked on the edit button, false otherwise
   */
  protected boolean isEditAction(HstRequest request) {
    String currentAction = getCurrentAction(request);

    return (currentAction != null) && currentAction.equals(KKActionsConstants.ACTIONS.EDIT.name());
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
