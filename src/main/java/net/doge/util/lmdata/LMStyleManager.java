package net.doge.util.lmdata;

import com.alibaba.fastjson2.JSONObject;
import net.doge.constant.system.LMDataConstants;
import net.doge.constant.system.SimplePath;
import net.doge.util.ui.ImageUtil;

import java.awt.image.BufferedImage;

/**
 * @Author Doge
 * @Description 主题管理器
 * @Date 2020/12/15
 */
public class LMStyleManager {
    private static final JSONObject STYLE_DATA = LMDataUtil.read(SimplePath.RESOURCE_PATH + LMDataConstants.STYLE_DATA_FILE_NAME);

    /**
     * 根据 key 获取 BufferedImage
     *
     * @param key
     * @return
     */
    public static BufferedImage getImage(String key) {
        return ImageUtil.toImage(STYLE_DATA.getString(key));
    }

    /**
     * 判断是否存在 imgKey
     *
     * @param key
     * @return
     */
    public static boolean contains(String key) {
        return STYLE_DATA.containsKey(key);
    }
}
