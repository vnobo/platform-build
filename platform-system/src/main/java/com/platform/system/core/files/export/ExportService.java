package com.platform.system.core.files.export;

import com.deepoove.poi.XWPFTemplate;
import com.deepoove.poi.config.Configure;
import com.deepoove.poi.plugin.table.LoopRowTableRenderPolicy;
import com.deepoove.poi.policy.ParagraphRenderPolicy;
import com.platform.commons.annotation.RestServerException;
import com.platform.commons.utils.ExportRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

/**
 * com.bootiful.system.core.files.export.ExportService
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/29
 */
@Log4j2
@Service
public class ExportService {

  private final Configuration configuration;
  private final WordTemplateLoader wordTemplateLoader;

  public ExportService(
      ExportProperties exportProperties,
      Configuration configuration,
      WordTemplateLoader wordTemplateLoader) {
    this.configuration = configuration;
    this.wordTemplateLoader = wordTemplateLoader;
    this.configuration.setTemplateLoader(
        new ExcelTemplateLoader(exportProperties.getTemplatePath()));
    this.configuration.setDefaultEncoding("utf-8");
    this.configuration.setLocale(Locale.CHINA);
  }

  public Resource processExcel(ExportRequest wordRequest) {
    try {
      ByteArrayOutputStream writerFile = new ByteArrayOutputStream(64);
      Writer writer = new OutputStreamWriter(writerFile, StandardCharsets.UTF_8);

      Template template;
      try {
        template =
            this.configuration.getTemplate(wordRequest.getTenantId() + "/" + wordRequest.getName());
      } catch (IOException | RestServerException e) {
        template = this.configuration.getTemplate(wordRequest.getName());
      }
      template.process(wordRequest.getParams(), writer);

      return new ByteArrayResource(writerFile.toByteArray());
    } catch (TemplateException | IOException e) {
      log.error("template request map: {},errMsg: {}", wordRequest.getParams(), e.getMessage());
      throw ExportException.withMsg(1501, e.getMessage());
    }
  }

  public Mono<Resource> processWord(ExportRequest wordRequest) {
    return this.wordTemplateLoader
        .loadTemplate(wordRequest.getName())
        .map(
            templateResource -> {
              try {
                LoopRowTableRenderPolicy loop = new LoopRowTableRenderPolicy();
                ParagraphRenderPolicy paragraph = new ParagraphRenderPolicy();
                Configure config =
                    Configure.builder()
                        .bind("data", loop)
                        .bind("text", paragraph)
                        .useSpringEL(true)
                        .build();
                XWPFTemplate template =
                    XWPFTemplate.compile(templateResource.getInputStream(), config)
                        .render(wordRequest.getParams());

                ByteArrayOutputStream writerFile = new ByteArrayOutputStream(64);
                template.writeAndClose(writerFile);

                return new ByteArrayResource(writerFile.toByteArray());

              } catch (IOException e) {
                log.error(
                    "template request map: {},errMsg: {}", wordRequest.getParams(), e.getMessage());
                throw ExportException.withMsg(1501, e.getMessage());
              }
            });
  }
}