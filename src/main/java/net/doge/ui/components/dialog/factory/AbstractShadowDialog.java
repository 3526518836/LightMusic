package net.doge.ui.components.dialog.factory;

import net.coobird.thumbnailator.Thumbnails;
import net.doge.constants.BlurConstants;
import net.doge.models.UIStyle;
import net.doge.ui.PlayerFrame;
import net.doge.utils.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author yzx
 * @Description 抽象阴影对话框
 * @Date 2021/1/5
 */
public abstract class AbstractShadowDialog extends JDialog {
    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    protected final int pixels = 10;

    protected DialogPanel globalPanel = new DialogPanel();

    protected PlayerFrame f;

    public AbstractShadowDialog(Window owner) {
        this(owner, true);
    }

    public AbstractShadowDialog(Window owner, boolean modal) {
        super(owner);
        if(owner instanceof PlayerFrame) this.f = (PlayerFrame) owner;
        setModal(modal);
    }

    public void updateBlur() {
        BufferedImage bufferedImage;
        if (f.blurType != BlurConstants.OFF && f.player.loadedMusic()) {
            bufferedImage = f.player.getMusicInfo().getAlbumImage();
            if (bufferedImage == f.defaultAlbumImage) bufferedImage = ImageUtil.eraseTranslucency(bufferedImage);
            if (f.blurType == BlurConstants.MC)
                bufferedImage = ImageUtil.dyeRect(1, 1, ImageUtil.getAvgRGB(bufferedImage));
            else if (f.blurType == BlurConstants.LG)
                bufferedImage = ImageUtil.toGradient(bufferedImage);
        } else {
            UIStyle style = f.currUIStyle;
            bufferedImage = style.getImg();
        }
        doBlur(bufferedImage);
    }

    private void doBlur(BufferedImage bufferedImage) {
        int dw = getWidth() - 2 * pixels, dh = getHeight() - 2 * pixels;
        try {
            boolean loadedMusic = f.player.loadedMusic();
            // 截取中间的一部分(有的图片是长方形)
            if (loadedMusic && f.blurType == BlurConstants.CV) bufferedImage = ImageUtil.cropCenter(bufferedImage);
            // 处理成 100 * 100 大小
            if (f.gsOn) bufferedImage = ImageUtil.width(bufferedImage, 100);
            // 消除透明度
            bufferedImage = ImageUtil.eraseTranslucency(bufferedImage);
            // 高斯模糊并暗化
            if (f.gsOn) bufferedImage = ImageUtil.doBlur(bufferedImage);
            if (f.darkerOn) bufferedImage = ImageUtil.darker(bufferedImage);
            // 放大至窗口大小
            bufferedImage = ImageUtil.width(bufferedImage, dw);
            if (dh > bufferedImage.getHeight())
                bufferedImage = ImageUtil.height(bufferedImage, dh);
            // 裁剪中间的一部分
            if (!loadedMusic || f.blurType == BlurConstants.CV || f.blurType == BlurConstants.OFF) {
                int iw = bufferedImage.getWidth(), ih = bufferedImage.getHeight();
                bufferedImage = Thumbnails.of(bufferedImage)
                        .scale(1f)
                        .sourceRegion(iw > dw ? (iw - dw) / 2 : 0, iw > dw ? 0 : (ih - dh) / 2, dw, dh)
                        .outputQuality(0.1)
                        .asBufferedImage();
            } else {
                bufferedImage = ImageUtil.forceSize(bufferedImage, dw, dh);
            }
            // 设置圆角
            bufferedImage = ImageUtil.setRadius(bufferedImage, 10);
            globalPanel.setBackgroundImage(bufferedImage);
            repaint();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    protected class DialogPanel extends JPanel {
        private BufferedImage backgroundImage;

        public DialogPanel() {
            initBorder();
        }

        public void initBorder() {
            // 阴影边框
            Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
            setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
        }

        public void eraseBorder() {
            setBorder(null);
        }

        public void setBackgroundImage(BufferedImage backgroundImage) {
            this.backgroundImage = backgroundImage;
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            // 避免锯齿
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (backgroundImage != null) {
//            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                g2d.drawImage(backgroundImage, pixels, pixels, getWidth() - 2 * pixels, getHeight() - 2 * pixels, this);
            }

            // 画边框阴影
            for (int i = 0; i < pixels; i++) {
                g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
                g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 10, 10);
            }
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
        }
    }
}