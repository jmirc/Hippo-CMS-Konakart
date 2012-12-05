package org.onehippo.forge.konakart.hst.vo;

import java.io.Serializable;

public class OrderItem implements Serializable {

  String title;
  String value;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
