package org.onehippo.forge.konakart.site.mailer;

import java.util.ArrayList;
import java.util.List;

public class KKEmail {

    private List<String> toAddresses = new ArrayList<String>();
    private String subject;
    private String message;
    private String smtpServer;

    // Used to send a copy of the email to the list of bcc emails that have been set within the Konakart admin
    // @see Send Extra Emails To field.
    private boolean doBlindCopy;

    public void setToAddress(String toAddress) {
        toAddresses.add(toAddress);
    }

    public List<String> getToAddresses() {
        return toAddresses;
    }

    public void setToAddresses(List<String> toAddresses) {
        this.toAddresses = toAddresses;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public boolean isDoBlindCopy() {
        return doBlindCopy;
    }

    public void setDoBlindCopy(boolean doBlindCopy) {
        this.doBlindCopy = doBlindCopy;
    }
}
