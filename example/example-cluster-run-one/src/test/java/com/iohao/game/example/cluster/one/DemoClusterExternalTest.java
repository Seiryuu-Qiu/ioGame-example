package com.iohao.game.example.cluster.one;

import com.iohao.game.bolt.broker.client.external.ExternalServer;
import com.iohao.game.bolt.broker.client.external.bootstrap.ExternalJoinEnum;
import com.iohao.game.bolt.broker.client.external.config.ExternalGlobalConfig;
import com.iohao.game.simple.SimpleHelper;

import java.util.concurrent.TimeUnit;

/**
 * 游戏对外服
 *
 * @author 渔民小镇
 * @date 2022-05-16
 */
public class DemoClusterExternalTest {
    public static void main(String[] args) throws InterruptedException {

        // 游戏对外服端口
        int port = 10100;

        // 对外服 tcp 方法的连接
        ExternalServer externalServer = SimpleHelper.createExternalServer(ExternalJoinEnum.WEBSOCKET, port);

        externalServer.startup();

        TimeUnit.SECONDS.sleep(1);

    }

}