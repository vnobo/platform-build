package com.bootiful.system.core.sms;

/**
 * com.bootiful.system.core.sms.SmsUpMessageListener
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/6/25
 */

import com.aliyun.mns.model.Message;
import lombok.extern.log4j.Log4j2;

/**
 * @author 如果发送的短信需要接收对方回复的状态消息，只需实现该接口并初始化一个 Spring Bean 即可。
 */
@Log4j2
public class SmsUpMessageListener implements com.alibaba.cloud.spring.boot.sms.SmsUpMessageListener {

    @Override
    public boolean dealMessage(Message message) {
        log.info("对方回复的状态消息; {}", message);
        return true;
    }

}