package com.bootiful.system.core.sms;

import cn.hutool.core.util.IdUtil;
import com.alibaba.cloud.spring.boot.sms.ISmsService;
import com.aliyuncs.dysmsapi.model.v20170525.*;
import com.aliyuncs.exceptions.ClientException;
import com.bootiful.commons.utils.BaseAutoToolsUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * com.bootiful.system.core.sms.SmsService
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/6/25
 */
@Service
@RequiredArgsConstructor
public class SimplerSmsService extends BaseAutoToolsUtil {

    private final ISmsService smsService;

    public SendBatchSmsResponse batchSendCheckCode(String code, List<String> phoneNumbers,
                                                   List<String> signNames,
                                                   String templateCode) {
        try {
            // 组装请求对象
            SendBatchSmsRequest request = new SendBatchSmsRequest();
            // 必填:待发送手机号。支持JSON格式的批量调用，批量上限为100个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式
            request.setPhoneNumberJson(this.objectMapper.writeValueAsString(phoneNumbers));
            // 必填:短信签名-支持不同的号码发送不同的短信签名
            request.setSignNameJson(this.objectMapper.writeValueAsString(signNames));
            // 必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(templateCode);
            // 必填:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            // 友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
            request.setTemplateParamJson(
                    "[{\"code\":\"" + code + "\"},{\"code\":\"" + code + "\"}]");
            // 可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
            // request.setSmsUpExtendCodeJson("[\"90997\",\"90998\"]");

            return smsService
                    .sendSmsBatchRequest(request);
        } catch (ClientException | JsonProcessingException e) {
            e.printStackTrace();
        }
        return new SendBatchSmsResponse();
    }

    public SendSmsResponse sendCheckCode(String code, String phoneNumber,
                                         String signName,
                                         String templateCode) {
        // 组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        // 必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        // 必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        // 必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        // 可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        // 选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        // request.setSmsUpExtendCode("90997");

        // 可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId(IdUtil.simpleUUID());
        try {
            return smsService.sendSmsRequest(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return new SendSmsResponse();
    }

    public QuerySendDetailsResponse querySendDetailsResponse(String telephone) {
        // 组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        // 必填-号码
        request.setPhoneNumber(telephone);
        // 必填-短信发送的日期 支持30天内记录查询（可查其中一天的发送数据），格式yyyyMMdd
        request.setSendDate("20190103");
        // 必填-页大小
        request.setPageSize(10L);
        // 必填-当前页码从1开始计数
        request.setCurrentPage(1L);
        try {
            return smsService.querySendDetails(request);
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return new QuerySendDetailsResponse();
    }

}