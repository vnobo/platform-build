package com.bootiful.system.core.qrcode;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2021/12/10
 */
@Data
@Schema(title = "二维码")
public class QrCode {

    @Schema(title = "内容")
    private String content;

    @Schema(title = "宽度")
    private Integer width;

    @Schema(title = "高度")
    private Integer height;

    @Schema(title = "二维码生成中间的图片")
    private String imagePath;
}