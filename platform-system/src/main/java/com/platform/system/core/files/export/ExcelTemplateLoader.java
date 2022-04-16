package com.platform.system.core.files.export;

import com.platform.commons.annotation.RestServerException;
import freemarker.cache.URLTemplateLoader;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * com.bootiful.system.core.files.export.RemoteTemplateLoader
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2022/1/29
 */
@Log4j2
public class ExcelTemplateLoader extends URLTemplateLoader {

  private String remotePath;

  public ExcelTemplateLoader(String remotePath) {
    if (remotePath == null) {
      throw new IllegalArgumentException("remotePath is null");
    }
    this.remotePath = canonicalizePrefix(remotePath);
    if (this.remotePath.indexOf('/') == 0) {
      this.remotePath = this.remotePath.substring(this.remotePath.indexOf('/') + 1);
    }
  }

  private static boolean isSchemaless(String fullPath) {
    int i = 0;
    int ln = fullPath.length();

    if ((i < ln) && (fullPath.charAt(i) == '/')) {
      i++;
    }

    while (i < ln) {
      char c = fullPath.charAt(i);
      if (c == '/') {
        return true;
      }
      if (c == ':') {
        return false;
      }
      i++;
    }
    return true;
  }

  @Override
  protected URL getURL(String name) {
    if (name.indexOf("_zh_CN") > 0) {
      name = name.replace("_zh_CN", "");
    }
    String fullPath = this.remotePath + name;
    if ((this.remotePath.equals("/")) && (!isSchemaless(fullPath))) {
      return null;
    }
    try {
      return UriComponentsBuilder.fromHttpUrl(fullPath).build().toUri().toURL();
    } catch (MalformedURLException e) {
      log.error("获取远程模板URL异常,信息: {}", e.getMessage());
      throw RestServerException.withMsg(1504, "远程模板URL异常,信息:" + e.getMessage());
    }
  }
}