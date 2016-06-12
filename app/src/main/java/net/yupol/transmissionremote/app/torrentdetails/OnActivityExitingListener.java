package net.yupol.transmissionremote.app.torrentdetails;

public interface OnActivityExitingListener<T> {
    /**
     * @return Object containing state that needs to be saved before exit or <code>null</code> if there is nothing to save.
     */
    T onActivityExiting();
}
