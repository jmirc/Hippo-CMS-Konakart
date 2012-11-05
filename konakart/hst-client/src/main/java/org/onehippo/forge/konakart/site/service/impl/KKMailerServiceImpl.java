package org.onehippo.forge.konakart.site.service.impl;

import com.konakart.al.KKAppEng;
import com.konakart.appif.KKEngIf;
import com.konakart.bl.EmailConfig;
import com.konakartadmin.bl.AdminEmailMgr;
import org.apache.commons.lang.StringUtils;
import org.onehippo.forge.konakart.common.engine.KKAdminEngine;
import org.onehippo.forge.konakart.site.mailer.KKEmail;
import org.onehippo.forge.konakart.site.service.KKMailerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.internet.InternetAddress;
import java.util.List;

public class KKMailerServiceImpl implements KKMailerService {

    public static final Logger log = LoggerFactory.getLogger(KKMailerServiceImpl.class);

    private EmailConfig emailConfig = new EmailConfig();

    private String fromAddress;
    private String emailReplyTo;


    @Override
    public boolean sendEmail(final KKAppEng kkAppEng, final KKEmail kkEmail) {
        return send(kkAppEng, kkEmail, true);
    }

    @Override
    public void sendAsyncEmail(final KKAppEng kkAppEng, final KKEmail kkEmail) {
        send(kkAppEng, kkEmail, false);
    }

    /**
     * Send the email....
     * @param kkAppEng the konakart client engine
     * @param kkEmail the email object to fill out to define the email to send
     * @param sync set to true to send the mail asynchronously or false to send it synchronously
     */
    private boolean send(KKAppEng kkAppEng, final KKEmail kkEmail, boolean sync) {
        try {

            // create the email config object
            loadEmailConfig(kkAppEng.getEng());

            AdminEmailMgr emailMgr = new AdminEmailMgr(KKAdminEngine.getInstance().getEngine()) {

                @Override
                protected EmailConfig getConfigData() throws Exception {

                    if (StringUtils.isNotEmpty(kkEmail.getSmtpServer())) {
                        emailConfig.setSmtpServer(kkEmail.getSmtpServer());
                    }

                    return emailConfig;
                }
            };

            List<String> toAddresses = kkEmail.getToAddresses();

            for (String toAddress : toAddresses) {
                emailMgr.sendHTML(toAddress, kkEmail.getSubject(), kkEmail.getMessage(), kkEmail.isDoBlindCopy(), sync);
            }

            return true;
        } catch (Exception e) {
            log.error("Failed to send the email.", e);
        }

        return false;

    }

    private void loadEmailConfig(KKEngIf eng) throws Exception {

        String smtpServer = eng.getConfigurationValue("SMTP_SERVER");

        if (StringUtils.isEmpty(smtpServer)) {
            throw new Exception("An email cannot be sent because the SMTP_SERVER configuration has not been set to a valid SMTP server");
        }

        emailConfig.setSmtpServer(smtpServer);

        fromAddress = eng.getConfigurationValue("EMAIL_FROM");
        if (fromAddress == null) {
            throw new Exception("An email cannot be sent because the EMAIL_FROM configuration has not been set.");
        }
        emailConfig.setFromAddress(new InternetAddress(fromAddress));

        emailReplyTo = eng.getConfigurationValue("EMAIL_REPLY_TO");
        if (emailReplyTo == null) {
            emailConfig.setReplyToAddress(emailConfig.getFromAddress());
        } else {
            emailConfig.setReplyToAddress(new InternetAddress(emailReplyTo));
        }

        emailConfig.setDebugEmail(eng.getConfigurationValueAsBool("DEBUG_EMAIL_SESSIONS", false));
        emailConfig.setAuthenticateServer(eng.getConfigurationValueAsBool("SMTP_SECURE", false));
        emailConfig.setSmtpUsername(eng.getConfigurationValue("SMTP_USER"));
        emailConfig.setSmtpPassword(eng.getConfigurationValue("SMTP_PASSWORD"));
        if (emailConfig.isAuthenticateServer()) {
            if (StringUtils.isEmpty(emailConfig.getSmtpUsername())) {
                throw new Exception("The configuration SMTP_SECURE is set to true, so SMTP_USER must be set in order to authenticate the SMTP server.");
            }

            if (StringUtils.isEmpty(emailConfig.getSmtpPassword())) {
                throw new Exception("The configuration SMTP_SECURE is set to true, so SMTP_PASSWORD must be set in order to authenticate the SMTP server.");
            }
        }

        emailConfig.setBccEmails(eng.getConfigurationValue("SEND_EXTRA_EMAILS_TO"));

        // When the website is not located on the same konakart'server, the mail properties file could not be found
        emailConfig.setMailPropertiesFileName(null);
        emailConfig.setEmailIntegrationClassName(eng.getConfigurationValue("EMAIL_INTEGRATION_CLASS"));

        if (StringUtils.isEmpty(emailConfig.getEmailIntegrationClassName())) {
            emailConfig.setEmailIntegrationClassName("com.konakart.bl.EmailIntegrationMgr");
        }

        emailConfig.setSmtpServer(eng.getConfigurationValue("SMTP_SERVER"));
    }
}
