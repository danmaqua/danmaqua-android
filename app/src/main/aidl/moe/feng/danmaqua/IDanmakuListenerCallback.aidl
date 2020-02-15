package moe.feng.danmaqua;

import moe.feng.danmaqua.model.BiliChatDanmaku;

interface IDanmakuListenerCallback {

    void onConnect(long roomId);

    void onDisconnect();

    void onHeartbeat(int online);

    void onReceiveDanmaku(in BiliChatDanmaku danmaku);

    void onConnectFailed(int reason);

}
