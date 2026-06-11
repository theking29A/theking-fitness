package com.theking.theking_backend.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 图形验证码生成器
 */
public class CaptchaUtil {

    private static final Random RANDOM = new Random();
    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // 去掉易混淆字符

    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    public static String generateBase64Image(String code) throws IOException {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 背景
        g.setColor(new Color(245, 245, 247));
        g.fillRect(0, 0, width, height);

        // 干扰线
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 8; i++) {
            int x1 = RANDOM.nextInt(width);
            int y1 = RANDOM.nextInt(height);
            int x2 = RANDOM.nextInt(width);
            int y2 = RANDOM.nextInt(height);
            g.drawLine(x1, y1, x2, y2);
        }

        // 画文字
        g.setFont(new Font("Arial", Font.BOLD, 24));
        for (int i = 0; i < code.length(); i++) {
            g.setColor(new Color(30 + RANDOM.nextInt(100), 30 + RANDOM.nextInt(100), 30 + RANDOM.nextInt(100)));
            int x = 15 + i * 22;
            int y = 28 + RANDOM.nextInt(6) - 3;
            g.drawString(String.valueOf(code.charAt(i)), x, y);
        }

        // 干扰点
        g.setColor(new Color(180, 180, 180));
        for (int i = 0; i < 30; i++) {
            int x = RANDOM.nextInt(width);
            int y = RANDOM.nextInt(height);
            g.drawRect(x, y, 1, 1);
        }

        g.dispose();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "png", os);
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(os.toByteArray());
    }
}
