package org.onehippo.forge.konakart.hst.wizard;

import com.konakart.al.KKAppEng;
import org.apache.commons.lang.StringUtils;
import org.hippoecm.hst.component.support.forms.FormMap;
import org.hippoecm.hst.component.support.forms.FormUtils;
import org.hippoecm.hst.core.component.HstRequest;
import org.hippoecm.hst.core.component.HstResponse;
import org.onehippo.forge.konakart.hst.utils.KKActionsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Base class for all activities.
 */
public abstract class BaseActivity implements Activity {

  protected Logger log = LoggerFactory.getLogger(getClass());

  private String name;
  private String acceptState;
  private boolean acceptEmtpyState = false;
  private String nextLoggedState;
  private String nextNonLoggedState;
  private String templateRenderPath;

  private String overridesNextLoggedState;

  protected ProcessorContext processorContext;
  protected FormMap formMap;
  protected KKAppEng kkAppEng;
  protected HstRequest hstRequest;
  protected HstResponse hstResponse;


  protected BaseActivity() {
  }

  @Override
  public void initialize(ProcessorContext processorContext) {
    this.processorContext = processorContext;
    this.hstRequest = processorContext.getSeedData().getRequest();
    this.hstResponse = processorContext.getSeedData().getResponse();

    formMap = new FormMap(hstRequest, getCheckoutFormMapFields());
    FormUtils.populate(hstRequest, formMap);
    kkAppEng = processorContext.getSeedData().getKkBaseHstComponent().
        getKKAppEng(processorContext.getSeedData().getRequest());
  }

  @Override
  public FormMap getFormMap() {
    return formMap;
  }

  @Override
  public boolean hasErrors() {
    Map<String, List<String>> messages = formMap.getMessages();

    boolean hasError = false;

    if (messages != null) {
      for (List<String> values : messages.values()) {
        if (values.size() > 0) {
          hasError = true;
          break;
        }
      }
    }

    return hasError;
  }

  @Override
  public boolean acceptState(String state) {
    return (acceptEmtpyState && (state == null)) || ((state != null) && acceptState.equals(state));

  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public void setAcceptState(String acceptState) {
    this.acceptState = acceptState;
  }

  public String getAcceptState() {
    return acceptState;
  }

  @Override
  public void setAcceptEmptyState(boolean acceptEmtpyState) {
    this.acceptEmtpyState = acceptEmtpyState;
  }

  @Override
  public String computeNextState() {
    if (processorContext.getSeedData().getKkBaseHstComponent().isGuestCustomer(hstRequest) &&
        !isCheckoutAsGuest() && !isCheckoutAsRegister()) {
      return nextNonLoggedState;
    }

    return nextLoggedState;
  }

  public String getNextLoggedState() {
    return nextLoggedState;
  }

  public void updateNextLoggedState(String nextLoggedState) {
    hstResponse.setRenderParameter(KKActionsConstants.FORCE_NEXT_LOGGED_STATE, nextLoggedState);
  }

  @Override
  public void setNextLoggedState(String nextLoggedState) {
    this.nextLoggedState = nextLoggedState;
  }

  @Override
  public void setNextNonLoggedState(String nextNonLoggedState) {
    this.nextNonLoggedState = nextNonLoggedState;
  }

  @Override
  public void setTemplateRenderPath(String templateRenderPath) {
    this.templateRenderPath = templateRenderPath;
  }

  /**
   * @return the template that will be rendered.
   */
  public String getTemplateRenderPath() {
    return templateRenderPath;
  }

  @Override
  public boolean doValidForm() {
    hstResponse.setRenderParameter(KKActionsConstants.DONT_HAVE_ACCOUNT, getDontHaveAccountValue());

    // By default the form is valid.
    return true;
  }

  @Override
  public void doAction() throws ActivityException {
    doApplyTemplateRenderPath();
    hstResponse.setRenderParameter(KKActionsConstants.DONT_HAVE_ACCOUNT, getDontHaveAccountValue());
  }

  public void doApplyTemplateRenderPath() {
    hstResponse.setRenderPath(getTemplateRenderPath());
  }

  @Override
  public void doAdditionalData() {
    hstResponse.setRenderParameter(KKActionsConstants.DONT_HAVE_ACCOUNT, getDontHaveAccountValue());
  }

  @Override
  public List<String> getCheckoutFormMapFields() {
    return Collections.emptyList();
  }

  /**
   * Add a message to the formMap
   *
   * @param name    the name
   * @param message the message
   */
  protected void addMessage(String name, String message) {
    formMap.addMessage(name, message);
  }

  /**
   * @return the dontHaveAccount value if is has been set.
   */
  @Nullable
  protected String getDontHaveAccountValue() {
    String dontHaveAccount = hstRequest.getParameter(KKActionsConstants.DONT_HAVE_ACCOUNT);

    if (StringUtils.isEmpty(dontHaveAccount)) {
      dontHaveAccount = (String) hstRequest.getAttribute(KKActionsConstants.DONT_HAVE_ACCOUNT);
    }

    return dontHaveAccount;
  }

  /**
   * Check if the customer asked to checkout as a guest
   *
   * @return true if the customer asked to checkout as a guest, false otherwise
   */
  protected boolean isCheckoutAsGuest() {
    String dontHaveAccount = getDontHaveAccountValue();

    return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKActionsConstants.CHECKOUT_AS_GUEST);
  }

  /**
   * Check if the customer asked to checkout as a register
   *
   * @return true if the customer asked to checkout as a register, false otherwise
   */
  protected boolean isCheckoutAsRegister() {
    String dontHaveAccount = getDontHaveAccountValue();

    return StringUtils.isNotBlank(dontHaveAccount) && StringUtils.equals(dontHaveAccount, KKActionsConstants.CHECKOUT_ASK_REGISTER);
  }
}
