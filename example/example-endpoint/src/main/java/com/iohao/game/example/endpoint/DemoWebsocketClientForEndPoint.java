/*
 * # iohao.com . 渔民小镇
 * Copyright (C) 2021 - 2023 double joker （262610965@qq.com） . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License..
 */
package com.iohao.game.example.endpoint;

import com.iohao.game.action.skeleton.core.CmdKit;
import com.iohao.game.action.skeleton.core.DataCodecKit;
import com.iohao.game.bolt.broker.client.external.bootstrap.ExternalKit;
import com.iohao.game.bolt.broker.client.external.bootstrap.message.ExternalMessage;
import com.iohao.game.common.kit.ExecutorKit;
import com.iohao.game.example.common.msg.DemoOperation;
import com.iohao.game.example.common.msg.login.DemoLoginVerify;
import com.iohao.game.example.endpoint.match.action.DemoCmdForEndPointMatch;
import com.iohao.game.example.endpoint.room.action.DemoCmdForEndPointRoom;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

/**
 * 模拟游戏客户端
 *
 * @author 渔民小镇
 * @date 2022-05-28
 */
@Slf4j
public class DemoWebsocketClientForEndPoint {

    public static void main(String[] args) throws URISyntaxException {
        var websocketClient = new DemoWebsocketClientForEndPoint();
        websocketClient.init();
    }

    private static byte[] createExternalMessage(int cmd, int subCmd) {
        return createExternalMessage(cmd, subCmd, null);
    }

    private static byte[] createExternalMessage(int cmd, int subCmd, Object data) {
        // 游戏框架内置的协议， 与游戏前端相互通讯的协议
        ExternalMessage externalMessage = ExternalKit.createExternalMessage(cmd, subCmd, data);

        // 转为字节
        return DataCodecKit.encode(externalMessage);
    }

    private void init() throws URISyntaxException {
        // 这里模拟游戏客户端
        // 连接游戏服务器的地址
        var wsUrl = "ws://127.0.0.1:10100/websocket";

        WebSocketClient webSocketClient = new WebSocketClient(new URI(wsUrl), new Draft_6455()) {
            @SneakyThrows
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                // jwt 写个固定的
                DemoLoginVerify loginVerify = new DemoLoginVerify();
                loginVerify.jwt = "abc";

                // 路由, 对应服务端逻辑服的业务类路由地址 （登录）
                int cmd = DemoCmdForEndPointMatch.cmd;
                int subCmd = DemoCmdForEndPointMatch.loginVerify;
                // 创建对外服协议，用于请求游戏服
                byte[] externalMessageBytes = createExternalMessage(cmd, subCmd, loginVerify);
                // 发送数据到游戏服务器
                this.send(externalMessageBytes);

                ExecutorKit.newSingleThreadExecutor("otherRun")
                        .execute(this::otherRun);
            }

            private void otherRun() {
                // 睡眠 2 秒后进行后续操作
                // 注意，为了方便演示流程才这样写，正常下不要这么写
                try {
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }

                // 路由, 对应服务端逻辑服的业务类路由地址 （匹配）
                int cmd = DemoCmdForEndPointMatch.cmd;
                int subCmd = DemoCmdForEndPointMatch.matching;

                var externalMessageBytes = createExternalMessage(cmd, subCmd);
                // 发送数据到游戏服务器
                this.send(externalMessageBytes);

                // Scheduled 5 seconds 发送消息 -- 房间请求相关的
                ExecutorKit.newSingleScheduled("websocketClient").scheduleAtFixedRate(() -> {

                    DemoOperation demoOperation = new DemoOperation();
                    demoOperation.name = "卡莉斯塔";

                    // 路由, 对应服务端逻辑服的业务类路由地址 （请求房间逻辑服的 operation 方法）
                    int roomCmd = DemoCmdForEndPointRoom.cmd;
                    int roomSubCmd = DemoCmdForEndPointRoom.operation;
                    byte[] externalMessage = createExternalMessage(roomCmd, roomSubCmd, demoOperation);

                    // 发送数据到游戏服务器
                    this.send(externalMessage);
                }, 5, 5, TimeUnit.SECONDS);
            }

            @Override
            public void onMessage(ByteBuffer byteBuffer) {
                // 接收服务器返回的消息
                byte[] dataContent = byteBuffer.array();
                ExternalMessage message = DataCodecKit.decode(dataContent, ExternalMessage.class);
                String mergeToString = CmdKit.mergeToString(message.getCmdMerge());
                log.info("收到消息 ExternalMessage ========== \n{} {}", mergeToString, message);
                byte[] data = message.getData();

                int merge = CmdKit.merge(DemoCmdForEndPointRoom.cmd, DemoCmdForEndPointRoom.operation);
                if (data != null && merge == message.getCmdMerge()) {
                    DemoOperation demoOperation = DataCodecKit.decode(data, DemoOperation.class);
                    log.info("demoOperation ========== \n{}", demoOperation);
                }
            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {

            }

            @Override
            public void onError(Exception ex) {

            }
        };

        // 开始连接服务器
        webSocketClient.connect();
    }
}
