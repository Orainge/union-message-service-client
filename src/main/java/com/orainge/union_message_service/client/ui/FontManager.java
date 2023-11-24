package com.orainge.union_message_service.client.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.awt.*;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 字体管理器
 */
@Slf4j
public class FontManager {
    private static final String FONT_NORMAL_PATH = "font/OPlusSans3-Regular.ttf";
    private static final String FONT_BOLD_PATH = "font/OPlusSans3-Bold.ttf";
    private static final Font FONT_NORMAL;
    private static final Font FONT_BOLD;
    private static final Map<String, Font> FONT_CACHE = new HashMap<>();

    static {
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        Font FONT_NORMAL1;
        try (InputStream inputStream = new ClassPathResource(FONT_NORMAL_PATH).getInputStream()) {
            FONT_NORMAL1 = Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
        } catch (Exception e) {
            log.error("无法初始化字体 NORMAL 字体", e);
            FONT_NORMAL1 = null;
        }
        FONT_NORMAL = FONT_NORMAL1;

        Font FONT_BOLD1;
        try (InputStream inputStream = new ClassPathResource(FONT_BOLD_PATH).getInputStream()) {
            FONT_BOLD1 = Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
        } catch (Exception e) {
            log.error("无法初始化字体 BOLD 字体", e);
            FONT_BOLD1 = null;
        }
        FONT_BOLD = FONT_BOLD1;
    }

    /**
     * 获取 NORMAL 字体
     *
     * @param size 字体大小
     */
    public static Font getNormalFont(float size) {
        String key = "n" + size;
        Font font = FONT_CACHE.get(key);

        if (font == null) {
            font = FONT_NORMAL.deriveFont(Font.PLAIN, size);
            FONT_CACHE.put(key, font);
        }

        return font;
    }

    /**
     * 获取 BOLD 字体
     *
     * @param size 字体大小
     */
    public static Font getBoldFont(float size) {
        String key = "b" + size;
        Font font = FONT_CACHE.get(key);

        if (font == null) {
            font = FONT_BOLD.deriveFont(Font.BOLD, size);
            FONT_CACHE.put(key, font);
        }

        return font;
    }

    /**
     * 获取自定义字体
     *
     * @param filePath 字体文件路径
     * @param style    字体样式
     * @param size     字体大小
     */
    public static Font getFont(String filePath, int style, int size) {
        String key = style + "," + size;
        Font font = FONT_CACHE.get(key);
        if (font != null) {
            return font;
        }

        try (InputStream inputStream = new ClassPathResource(filePath).getInputStream()) {
            font = Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
            font.deriveFont(style, size);
            FONT_CACHE.put(key, font); // 放入缓存
        } catch (Exception e) {
            log.error("无法初始化字体 [" + filePath + "]", e);
            font = null;
        }

        return font;
    }
}
