package org.onehippo.forge.konakart.common.engine;

public class KKActivityConfig {

  private String name;
  private String activityClass;
  private boolean acceptEmptyState;
  private String acceptState;
  private String nextNonLoggedState;
  private String nextLoggedState;
  private String templateRenderpath;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

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
