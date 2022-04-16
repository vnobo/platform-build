package com.platform.gateway.weixin;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.platform.commons.annotation.RestServerException;
import com.platform.commons.security.AuthenticationToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Map;

/**
 * com.bootiful.gateway.weixin.WxMaUserController
 *
 * <p>微信小程序用户接口
 *
 * @author <a href="https://github.com/vnobo">Alex bob</a>
 * @date Created by 2021/5/28
 */
@Log4j2
@Tag(name = "微信认证管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/wx/user/{appId}/v1")
public class WxMaUserController {

  private final WxMaUserService maUserService;

  /**
   * 微信APPID根据CODE获取认证token
   *
   * @param appId 微信APP
   * @param code 微信返回的CODE
   * @param exchange 当前登录会话
   * @return 返回微信认证信息和当前系统匿名认证信息需要更新当前TOKEN
   */
  @Operation(summary = "根据CODE获取微信token", description = "返回微信TOKEN")
  @GetMapping("authorize")
  public Mono<Object> authorize(
      @PathVariable String appId, String code, ServerWebExchange exchange) {
    log.debug("请求[AppId]: {}", appId);
    if (StringUtils.isBlank(code)) {
      return Mono.defer(() -> Mono.error(RestServerException.withMsg(1401, "请求[CODE]不能为空!")));
    }
    try {
      WxMaJscode2SessionResult session =
          WxMaConfiguration.getMaService(appId).getUserService().getSessionInfo(code);
      return this.maUserService
          .anonymousLogin(session, exchange)
          .map(token -> Map.of("authentication", token, "jscode2Session", session));
    } catch (WxErrorException e) {
      log.error(e.getMessage(), e);
      return Mono.defer(
          () -> Mono.error(RestServerException.withMsg(1502, "微信远程服务异常: " + e.getMessage())));
    }
  }

  /**
   * 微信用户登录当前系统
   *
   * @param appId 登录微信APPID
   * @param regRequest 登录的提交参数
   * @param exchange 当前登录会话
   * @return 返回认证信息需要更新当前TOKEN
   */
  @Operation(summary = "微信用户登录", description = "返回认证信息需要更新当前TOKEN")
  @PostMapping("login")
  public Mono<AuthenticationToken> login(
      @PathVariable String appId,
      @Valid @RequestBody WxRequest regRequest,
      ServerWebExchange exchange) {
    log.debug("请求[AppId]: {}", appId);
    return this.maUserService.login(regRequest.appId(appId), exchange);
  }

  @Operation(summary = "获取微信用户信息")
  @GetMapping("info")
  public Mono<WxMaUserInfo> info(
      @PathVariable String appId,
      String sessionKey,
      String signature,
      String rawData,
      String encryptedData,
      String iv) {

    log.debug("请求[AppId]: {}", appId);
    // 用户信息校验
    if (!WxMaConfiguration.getMaService(appId)
        .getUserService()
        .checkUserInfo(sessionKey, rawData, signature)) {
      return Mono.defer(() -> Mono.error(RestServerException.withMsg(1403, "用户信息验证失败!")));
    }

    // 解密用户信息
    WxMaUserInfo userInfo =
        WxMaConfiguration.getMaService(appId)
            .getUserService()
            .getUserInfo(sessionKey, encryptedData, iv);

    return Mono.just(userInfo);
  }

  @Operation(summary = "获取微信用户手机号")
  @PostMapping("phone")
  public Mono<WxMaPhoneNumberInfo> phone(
      @PathVariable String appId, @RequestBody Map<String, String> params) {

    try {
      WxMaPhoneNumberInfo wxMaPhoneNumberInfo =
          WxMaConfiguration.getMaService(appId)
              .getUserService()
              .getPhoneNoInfo(
                  params.get("sessionKey"), params.get("encryptedData"), params.get("iv"));
      return Mono.just(wxMaPhoneNumberInfo);
    } catch (Exception e) {
      log.error("获取微信用户手机号,错误: {}", e.getMessage());
      return Mono.error(RestServerException.withMsg("获取用户手机号错误,信息: " + e.getMessage()));
    }
  }
}