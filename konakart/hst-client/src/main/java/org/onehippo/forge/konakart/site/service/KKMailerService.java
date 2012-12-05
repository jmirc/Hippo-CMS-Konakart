package org.onehippo.forge.konakart.site.service;

import com.konakart.al.KKAppEng;
import org.onehippo.forge.konakart.site.mailer.KKEmail;

public interface KKMailerService {

  /**
   * Send a synchronously email
   *
   * @param kkEmail the email object to fill out to define the email to send
   * @return true if the message is sent, false otherwise
   */
  boolean sendEmail(final KKAppEng kkAppEng, final KKEmail kkEmail);

  /**
   * Send an asynchronously email
   *
   * @param kkEmail the email object to fill out to define the email to send
   */
  void sendAsyncEmail(final KKAppEng kkAppEng, final KKEmail kkEmail);
}
