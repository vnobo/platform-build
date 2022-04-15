package com.bootiful.system.core.files.export;

import com.bootiful.commons.utils.ExportRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * com.bootiful.system.core.files.export.ExportController
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/29
 */
@Tag(name = "文件导出模板处理")
@Controller
@RequestMapping("files/export/v1")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("template")
    public ResponseEntity<Resource> export(@Valid @RequestBody ExportRequest exportRequest) {
        return this.exportExcel(exportRequest);
    }

    @PostMapping("excel")
    public ResponseEntity<Resource> exportExcel(@Valid @RequestBody ExportRequest exportRequest) {
        return ResponseEntity.ok()
                .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        URLEncoder.encode(exportRequest.getTitle(), StandardCharsets.UTF_8) + "\"")
                .body(exportService.processExcel(exportRequest));
    }

    @PostMapping("word")
    public Mono<ResponseEntity<Resource>> exportWord(@Valid @RequestBody ExportRequest exportRequest) {
        return exportService.processWord(exportRequest)
                .map(resource -> ResponseEntity.ok()
                        .headers(httpHeaders -> httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                                URLEncoder.encode(exportRequest.getTitle(), StandardCharsets.UTF_8) + "\"")
                        .body(resource));
    }

}