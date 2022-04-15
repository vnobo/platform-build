package com.platform.system.core.files.export;

import com.platform.commons.annotation.RestServerException;
import com.platform.commons.client.AbstractClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.system.core.files.export.RemoteTemplateLoader
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/29
 */
@Log4j2
@Service
public class WordTemplateLoader extends AbstractClient {

    private final String templatePath;

    public WordTemplateLoader(ExportProperties exportProperties) {
        this.templatePath = exportProperties.getTemplatePath();

    }

    public Mono<Resource> loadTemplate(String name) {
        return this.webClient.get().uri(uriBuilder -> uriBuilder.path("word").path(name).build())
                .retrieve()
                .onStatus(HttpStatus::isError, clientResponse -> clientResponse.bodyToMono(String.class)
                        .flatMap(errMsg -> Mono.error(RestServerException.withMsg(1051,
                                "获取Word模板错误! " + errMsg))))
                .bodyToMono(Resource.class);
    }

    @Override
    public void afterPropertiesSet() {
        initializeBaseUrl(templatePath);
    }
}