/*
 * ioGame
 * Copyright (C) 2021 - 2023  渔民小镇 （262610965@qq.com、luoyizhu@gmail.com） . All Rights Reserved.
 * # iohao.com . 渔民小镇
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.iohao.example.json.app;

import com.iohao.game.action.skeleton.core.IoGameGlobalSetting;
import com.iohao.game.action.skeleton.core.codec.JsonDataCodec;
import com.iohao.game.common.kit.InternalKit;
import com.iohao.game.example.common.cmd.JsonCmd;
import com.iohao.game.example.common.msg.HelloReq;
import com.iohao.game.external.client.AbstractInputCommandRegion;
import com.iohao.game.external.client.InputCommandRegion;
import com.iohao.game.external.client.join.ClientRunOne;
import com.iohao.game.external.client.kit.ClientUserConfigs;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author 渔民小镇
 * @date 2023-07-17
 */
@Slf4j
public class JsonClient {
    public static void main(String[] args) {
        // 使用 json 编解码
        IoGameGlobalSetting.setDataCodec(new JsonDataCodec());

        ClientUserConfigs.closeLog();

        // 模拟请求数据
        List<InputCommandRegion> inputCommandRegions = List.of(
                new InternalRegion()
        );

        // 启动模拟客户端
        new ClientRunOne()
                .setInputCommandRegions(inputCommandRegions)
                .startup();
    }

    static class InternalRegion extends AbstractInputCommandRegion {
        @Override
        public void initInputCommand() {
            // 模拟请求的主路由
            inputCommandCreate.cmd = JsonCmd.cmd;

            HelloReq helloReq = new HelloReq();
            helloReq.name = "塔姆";

            // 配置一些模拟请求
            ofCommand(JsonCmd.hello).callback(HelloReq.class, result -> {
                HelloReq value = result.getValue();
                log.info("value : {}", value);
            }).setDescription("hello").setRequestData(helloReq);

            JsonMsg jsonMsg = new JsonMsg();
            ofCommand(JsonCmd.jsonMsg).callback(JsonMsg.class, result -> {
                JsonMsg value = result.getValue();
                log.info("value : {}", value);
            }).setDescription("jsonMsg").setRequestData(jsonMsg);

            // 一秒后，执行模拟请求;
            InternalKit.newTimeoutSeconds(task -> {
                ofRequestCommand(JsonCmd.hello).request();
                ofRequestCommand(JsonCmd.jsonMsg).request();
            });
        }
    }
}
