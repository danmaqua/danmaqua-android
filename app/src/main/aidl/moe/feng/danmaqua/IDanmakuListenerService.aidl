package moe.feng.danmaqua;

import moe.feng.danmaqua.IDanmakuListenerCallback;

interface IDanmakuListenerService {

    void connect(long roomId);

    void disconnect();

    void requestHeartbeat();

    boolean isConnected();

    long getRoomId();

    void registerCallback(IDanmakuListenerCallback callback, boolean filter);

    void unregisterCallback(IDanmakuListenerCallback callback);

}
