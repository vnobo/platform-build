package com.bootiful.system.core.sms;

/**
 * com.bootiful.system.core.sms.SmsReportMessageListener
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/6/25
 */

import com.aliyun.mns.model.Message;
import lombok.extern.log4j.Log4j2;

import java.util.LinkedList;
import java.util.List;

/**
 * @author 如果需要监听短信是否被对方成功接收，只需实现这个接口并初始化一个 Spring Bean 即可。
 */
@Log4j2
public class SmsReportMessageListener implements com.alibaba.cloud.spring.boot.sms.SmsReportMessageListener {

    private final List<Message> smsReportMessageSet = new LinkedList<>();

    @Override
    public boolean dealMessage(Message message) {
        smsReportMessageSet.add(message);
        log.info("是否被对方成功接收; {}", message);
        return true;
    }

    public List<Message> getSmsReportMessageSet() {
        return smsReportMessageSet;
    }
}