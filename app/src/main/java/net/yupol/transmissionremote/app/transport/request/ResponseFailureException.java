package net.yupol.transmissionremote.app.transport.request;


public class ResponseFailureException extends RuntimeException {

    private final String failureMessage;

    public ResponseFailureException(String failureMessage) {
        this.failureMessage = failureMessage;
    }

    public String getFailureMessage() {
        return failureMessage;
    }
}
