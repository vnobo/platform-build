package com.platform.gateway.captcha;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.generator.RandomGenerator;
import com.platform.commons.annotation.RestServerException;
import com.platform.commons.security.AuthenticationToken;
import com.platform.commons.security.ReactiveSecurityHelper;
import com.platform.gateway.client.SystemClient;
import com.platform.gateway.security.LoginRequest;
import com.platform.gateway.security.SecurityManager;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * com.bootiful.gateway.captcha.CaptchaController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/11/4
 */
@Log4j2
@Hidden
@RestController
@RequestMapping("/oauth2/v1")
@RequiredArgsConstructor
public class CaptchaController {

    private final SystemClient systemClient;
    private final SecurityManager detailsService;

    @GetMapping("/captcha/code")
    public ResponseEntity<DataBuffer> getCaptcha(WebSession session, ServerWebExchange exchange) {
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().allocateBuffer();
        try (OutputStream outputStream = dataBuffer.asOutputStream()) {
            // 定义图形验证码的长、宽、验证码字符数、干扰元素个数
            LineCaptcha captcha = CaptchaUtil.createLineCaptcha(200, 100, 4, 10);
            captcha.setGenerator(new RandomGenerator("0123456789", 4));
            File fontFile = ResourceUtils.getFile("classpath:fonts/SansSerif.ttf");
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            captcha.setFont(font.deriveFont(Font.BOLD, 75f));
            // 图形验证码写出，可以写出到文件，也可以写出到流
            captcha.write(outputStream);
            Object o = session.getAttributes().put(session.getId() + "captcha", captcha);
            log.debug("captcha 保存成功 {} ", o);
            DataBufferUtils.release(dataBuffer);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(dataBuffer);
        } catch (IOException | FontFormatException e) {
            log.error("dataBuffer write exception {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(dataBuffer.read(e.getMessage().getBytes(StandardCharsets.UTF_8)));
        }
    }

    @GetMapping("/captcha/check")
    public Mono<Boolean> checkCaptcha(String code, WebSession session) {
        LineCaptcha captcha = session.getAttributeOrDefault(
                session.getId() + "captcha", CaptchaUtil.createLineCaptcha(200, 100, 4, 10));
        session.getAttributes().remove(session.getId() + "captcha");
        return Mono.just(captcha.verify(code));
    }


    @Operation(summary = "手机验证码发送", description = "返回认证信息TOKEN")
    @GetMapping("/phone/code")
    public Mono<CsrfToken> phoneCode(String phone, ServerWebExchange exchange) {
        CsrfToken csrfToken = exchange.getRequiredAttribute(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME);
        return systemClient.smsSend(phone).flatMap(code -> Mono.defer(() -> Mono.just(csrfToken)));
    }

    @Operation(summary = "手机登录", description = "返回认证信息TOKEN")
    @PostMapping("/phone/login")
    public Mono<AuthenticationToken> phoneLogin(@Valid @RequestBody LoginRequest loginRequest,
                                                ServerWebExchange exchange) {
        return systemClient.smsCheck(loginRequest).filter(res -> res)
                .switchIfEmpty(Mono.defer(() -> Mono.error(RestServerException
                        .withMsg(1401, "验证码错误,请重试!"))))
                .delayUntil(res -> exchange.getSession().doOnNext(webSession ->
                        webSession.setMaxIdleTime(Duration.ofMinutes(5))))
                .flatMap(res -> detailsService.appLogin(loginRequest
                        .system(ReactiveSecurityHelper.systemForHeader(exchange))))
                .flatMap(authentication -> ReactiveSecurityHelper.authenticationTokenMono(exchange, authentication))
                .delayUntil(res -> ReactiveSecurityHelper.removeToken(exchange));
    }
}