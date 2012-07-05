package org.onehippo.forge.konakart.hst.wizard;


public class ActivityException extends Exception {

    public ActivityException() {
    }

    public ActivityException(String s) {
        super(s);
    }

    public ActivityException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public ActivityException(Throwable throwable) {
        super(throwable);
    }
}
