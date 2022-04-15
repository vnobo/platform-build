package com.bootiful.system.core.qrcode;

import cn.hutool.core.codec.Base64;
import com.bootiful.commons.annotation.RestServerException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/motcs">motcs</a>
 * @since 2021/12/9
 */
@Log4j2
@Service
public class QrCodeManager {
    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFFFFFFFF;
    private static final int WIDTH_HEIGHT = 200;
    private static final String URL = "https://code.jinqiruanjian.com/";


    /**
     * 二维码写码器
     */
    private static final MultiFormatWriter MULTI_FORMAT_WRITER = new MultiFormatWriter();

    /**
     * 生成二维码图片文件（不带LOGO）
     *
     * @param content 要生成二维码的内容
     * @param width   二维码的高度
     * @param height  二维码的宽度
     * @return 二维码图片
     * @throws WriterException 异常
     */
    private static BufferedImage genQrcode(String content, int width, int height) throws WriterException {
        Map<EncodeHintType, String> hints = new HashMap<>(4);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, "1");
        //根据高度和宽度生成像素矩阵
        BitMatrix bitMatrix = MULTI_FORMAT_WRITER.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
        BufferedImage image = new BufferedImage(bitMatrix.getWidth(), bitMatrix.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < bitMatrix.getWidth(); x++) {
            for (int y = 0; y < bitMatrix.getHeight(); y++) {
                //填充黑白两色
                image.setRGB(x, y, bitMatrix.get(x, y) ? BLACK : WHITE);
            }
        }
        return image;
    }

    /**
     * 将图片转为base64
     *
     * @param image 图片
     * @return base64
     */
    public static String getBase64(BufferedImage image) {
        String base64 = "data:image/png;base64,";
        try {
            //输出流
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", stream);
            base64 += Base64.encode(stream.toByteArray());
            stream.flush();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }

    /**
     * 生成二维码
     *
     * @param qrCode 要生成二维码的内容
     */
    public Boolean encodeImg(QrCode qrCode) {
        try {
            //生成二维码图片文件（不带LOGO）
            return ImageIO.write(genQrcode(URL + qrCode.getContent(), qrCode.getWidth(), qrCode.getHeight()),
                    "png", new File(qrCode.getImagePath()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 生成二维码图片文件（不带LOGO）
     * <p>请求的URL已由后台定死，不可改变</p>
     *
     * @param qrCode 要生成二维码的各种信息
     * @return 二维码图片
     */
    public String encodeString(QrCode qrCode) {
        try {
            return getBase64(genQrcode(URL + qrCode.getContent(), WIDTH_HEIGHT, WIDTH_HEIGHT));
        } catch (WriterException e) {
            throw RestServerException.withMsg(1101, e.getMessage());
        }
    }

    /**
     * 自定义生成二维码图片文件（不带LOGO）
     * <p>自定义的内容为所有内容全部自定义</p>
     * <p>而非后台给定死的请求域名</p>
     *
     * @param qrCode 要生成二维码的各种信息
     * @return 二维码图片
     */
    public String encodeCustomString(QrCode qrCode) {
        try {
            return getBase64(genQrcode(qrCode.getContent(), WIDTH_HEIGHT, WIDTH_HEIGHT));
        } catch (WriterException e) {
            throw RestServerException.withMsg(1101, e.getMessage());
        }
    }

}