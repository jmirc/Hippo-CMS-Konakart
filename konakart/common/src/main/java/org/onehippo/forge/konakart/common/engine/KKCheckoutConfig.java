package org.onehippo.forge.konakart.common.engine;

import com.google.common.collect.Lists;

import java.util.LinkedList;

public class KKCheckoutConfig {

  /**
   * The class associated with the checkout process
   */
  private String processorClass;

  /**
   * List of activities used by the checkout process
   */
  private LinkedList<KKActivityConfig> activityConfigList = Lists.newLinkedList();


  public LinkedList<KKActivityConfig> getActivityConfigList() {
    return activityConfigList;
  }

  public void addActivityConfigList(KKActivityConfig activityConfigList) {
    this.activityConfigList.addLast(activityConfigList);
  }

  public String getProcessorClass() {
    return processorClass;
  }

  public void setProcessorClass(String processorClass) {
    this.processorClass = processorClass;
  }
}
