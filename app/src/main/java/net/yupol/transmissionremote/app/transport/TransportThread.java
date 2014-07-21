package net.yupol.transmissionremote.app.transport;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import net.yupol.transmissionremote.app.server.Server;
import net.yupol.transmissionremote.app.transport.request.Request;
import net.yupol.transmissionremote.app.transport.response.Response;

import java.io.IOException;

public class TransportThread extends Thread {

    private static final String TAG = TransportThread.class.getSimpleName();

    public static final int REQUEST = 1;
    public static final int RESPONSE = 2;

    private Remote remote;
    private Handler handler;
    private Handler responseHandler;

    public TransportThread(Server server, Handler responseHandler) {
        remote = new Remote(server);
        this.responseHandler = responseHandler;
    }

    @Override
    public void run() {
        Looper.prepare();
        synchronized (this) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case REQUEST:
                            handleRequest(msg);
                            break;
                        default:
                            Log.d(TAG, "Unknown message: " + msg);
                    }
                }
            };
            notifyAll();
        }
        Looper.loop();
    }

    public Handler getHandler() {
        if (handler == null) {
            synchronized (this) {
                while (handler == null) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Waiting for handler interrupted");
                    }
                }
            }
        }
        return handler;
    }

    public void quit() {
        if (isAlive()) {
            getHandler().getLooper().quit();
        }
    }

    private void handleRequest(Message msg) {
        if (!(msg.obj instanceof  Request))
            throw new IllegalArgumentException("Request message must contain Request object in its 'obj' field");

        Response response;
        try {
            response = remote.sendRequest((Request) msg.obj, true);
        } catch (IOException e) {
            Log.e(TAG, "Error while sending request", e);
            return;
        }

        Message responseMsg = responseHandler.obtainMessage(RESPONSE);
        responseMsg.obj = response;
        responseHandler.sendMessage(responseMsg);
    }
}
