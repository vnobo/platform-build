package com.platform.system.core.qrcode;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2021/12/9
 */
@Tag(name = "二维码生成")
@RestController
@RequiredArgsConstructor
@RequestMapping("qrcode/v1")
public class QrCodeController {

    private final QrCodeManager qrCodeManager;

    @PostMapping("encode")
    public Mono<String> encodeString(@Valid @RequestBody QrCode qrCode) {
        return Mono.justOrEmpty(this.qrCodeManager.encodeString(qrCode));
    }

    @PostMapping("encode/custom")
    public Mono<String> encodeCustomString(@Valid @RequestBody QrCode qrCode) {
        return Mono.justOrEmpty(this.qrCodeManager.encodeCustomString(qrCode));
    }

    @GetMapping("encode/img")
    public Mono<Boolean> encodeImg(QrCode qrCode) {
        return Mono.justOrEmpty(this.qrCodeManager.encodeImg(qrCode));
    }
}