package com.bootiful.system.core.sms;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.RandomUtil;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.bootiful.commons.annotation.RestServerException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * com.bootiful.system.core.sms.SmsController
 *
 * @author Alex bob(https://github.com/vnobo)
 * @date Created by 2021/7/2
 */
@Hidden
@Log4j2
@RestController
@RequiredArgsConstructor
@RequestMapping("sms/manager/v1")
public class SmsManagerController {

    private final static String SMS_REDIS_PREFIX_KEY = "countryside:system:sms:code";

    private final SimplerSmsService simplerSmsService;
    private final ReactiveStringRedisTemplate stringRedisTemplate;

    @GetMapping("send")
    public Mono<SendSmsResponse> send(String phone) {

        Assert.notBlank(phone, "需要发送的手机号不能为空!");

        String code = RandomUtil.randomNumbers(6);
        return stringRedisTemplate.opsForValue()
                .set(SMS_REDIS_PREFIX_KEY + ":" + phone, code, Duration.ofMinutes(5))
                .map(result -> simplerSmsService.sendCheckCode(code, phone, "锦润科技",
                        "SMS_173686711"))
                .onErrorResume(e -> Mono.error(RestServerException.withMsg("短信发送失败, 信息: " + e.getMessage())))
                .doOnNext(sendSmsResponse -> log.debug("短信发送完成. 信息: {}", sendSmsResponse.getMessage()));

    }

    @PostMapping("check")
    public Mono<Boolean> check(@RequestBody Map<String, String> params) {
        Assert.notBlank(params.get("phone"), "验证手机号不能为空!");
        Assert.notBlank(params.get("code"), "验证码不能为空!");
        return stringRedisTemplate.opsForValue().get(SMS_REDIS_PREFIX_KEY + ":" + params.get("phone"))
                .map(code -> code.equals(params.get("code"))).filter(result -> result)
                .switchIfEmpty(Mono.error(RestServerException.withMsg("短信验证失败, 信息: "
                        + params.get("code") + " 过期或者不正确!")))
                .flatMap(result -> stringRedisTemplate.opsForValue()
                        .delete(SMS_REDIS_PREFIX_KEY + ":" + params.get("phone")));

    }

}