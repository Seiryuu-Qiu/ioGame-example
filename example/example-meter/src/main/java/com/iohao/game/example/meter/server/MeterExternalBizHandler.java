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
package com.iohao.game.example.meter.server;

import com.alipay.remoting.exception.RemotingException;
import com.iohao.game.action.skeleton.core.exception.ActionErrorEnum;
import com.iohao.game.action.skeleton.protocol.RequestMessage;
import com.iohao.game.bolt.broker.client.external.bootstrap.ExternalKit;
import com.iohao.game.bolt.broker.client.external.bootstrap.message.ExternalMessage;
import com.iohao.game.bolt.broker.client.external.config.ExternalGlobalConfig;
import com.iohao.game.bolt.broker.client.external.session.UserChannelId;
import com.iohao.game.bolt.broker.client.external.session.UserSession;
import com.iohao.game.bolt.broker.client.external.session.UserSessions;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author 渔民小镇
 * @date 2022-10-02
 */
@Slf4j
@ChannelHandler.Sharable
public class MeterExternalBizHandler extends SimpleChannelInboundHandler<ExternalMessage> {
    public static final LongAdder userIdAdder = new LongAdder();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ExternalMessage message) {

        // 得到 session
        UserSession userSession = UserSessions.me().getUserSession(ctx);

        // 如果没登录的，模拟登录
        if (!userSession.isVerifyIdentity()) {
            userIdAdder.increment();

            UserChannelId userChannelId = userSession.getUserChannelId();
            UserSessions.me().settingUserId(userChannelId, userIdAdder.longValue());
        }

        // 是否可以访问业务方法（action），true 表示可以访问该路由对应的业务方法
        boolean pass = ExternalGlobalConfig.accessAuthenticationHook.pass(userSession, message.getCmdMerge());

        // 当访问验证没通过，通知玩家
        if (!pass) {
            message.setResponseStatus(ActionErrorEnum.verifyIdentity.getCode());
            message.setValidMsg("请先登录，在请求业务方法");
            // 响应结果给用户
            Channel channel = userSession.getChannel();
            channel.writeAndFlush(message);
            return;
        }

        // 将 message 转换成 RequestMessage
        RequestMessage requestMessage = ExternalKit.convertRequestMessage(message);

        try {
            // 由内部逻辑服转发用户请求到游戏网关，在由网关转到具体的业务逻辑服
            ExternalKit.requestGateway(ctx, requestMessage);
        } catch (RemotingException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("channelInactive");
        // 从 session 管理中移除
        UserSession userSession = UserSessions.me().getUserSession(ctx);
        UserSessions.me().removeUserSession(userSession);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 加入到 session 管理
        UserSessions.me().add(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("exceptionCaught");
        // 从 session 管理中移除
        UserSession userSession = UserSessions.me().getUserSession(ctx);
        UserSessions.me().removeUserSession(userSession);
    }
}
