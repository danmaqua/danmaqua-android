package moe.feng.danmaqua;

import moe.feng.danmaqua.IDanmakuListenerCallback;

interface IDanmakuListenerService {

    void connect(long roomId);

    void disconnect();

    void requestHeartbeat();

    void showFloating();

    void hideFloating();

    boolean isConnected();

    boolean isFloatingShowing();

    long getRoomId();

    void registerCallback(IDanmakuListenerCallback callback, boolean filter);

    void unregisterCallback(IDanmakuListenerCallback callback);

}
