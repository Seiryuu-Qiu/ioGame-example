/*
 * # iohao.com . 渔民小镇
 * Copyright (C) 2021 - 2023 double joker （262610965@qq.com） . All Rights Reserved.
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
package com.iohao.game.example.endpoint.room.action;

import com.iohao.game.example.common.DemoModuleCmd;

/**
 * 战斗服的 cmd
 *
 * @author 渔民小镇
 * @date 2022-05-28
 */
public interface DemoCmdForEndPointRoom {
    /** 模块 - 主 cmd : 10 */
    int cmd = DemoModuleCmd.demoModule_10_endpoint_room_cmd;

    /** 示例 统计房间数量 方法 */
    int countRoom = 0;
    /** 房间内的操作 */
    int operation = 1;
}
