/*
 * # iohao.com . 渔民小镇
 * Copyright (C) 2021 - 2022 double joker （262610965@qq.com） . All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.iohao.game.example.ext.monitor;

//import com.iohao.game.bolt.broker.boot.monitor.server.MonitorExtServer;

import com.iohao.game.bolt.broker.client.AbstractBrokerClientStartup;
import com.iohao.game.example.ext.monitor.server.DemoExtLogicServer;
import com.iohao.game.simple.SimpleHelper;

import java.util.List;

/**
 * @author 渔民小镇
 * @date 2022-06-04
 */
public class DemoExtApplication {
    public static void main(String[] args) {

        List<AbstractBrokerClientStartup> logicServerList = List.of(
                // 逻辑服 demo
                new DemoExtLogicServer()
                // 监控逻辑服
//                , new MonitorExtServer()
        );

        // 游戏对外服端口
        int port = 10100;
        // 启动 对外服、网关服、逻辑服; 并生成游戏业务文档
        SimpleHelper.run(port, logicServerList);

        /*
         * 该示例文档地址
         */
    }
}
