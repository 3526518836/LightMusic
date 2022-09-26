package net.doge.ui.components;

import net.doge.constants.Colors;
import net.doge.ui.PlayerFrame;
import net.doge.ui.componentui.ScrollBarUI;
import net.doge.utils.ImageUtils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;

public class CustomComboPopup extends BasicComboPopup {
    private Color foreColor;
    private PlayerFrame f;

    // 最大阴影透明度
    private final int TOP_OPACITY = 30;
    // 阴影大小像素
    private final int pixels = 10;

    public CustomComboPopup(JComboBox comboBox, PlayerFrame f, Color foreColor) {
        super(comboBox);

        this.f = f;
        this.foreColor = foreColor;

        setDefaultLightWeightPopupEnabled(false);
        setLightWeightPopupEnabled(false);

        // 阴影边框
        Border border = BorderFactory.createEmptyBorder(pixels, pixels, pixels, pixels);
        setBorder(BorderFactory.createCompoundBorder(getBorder(), border));
    }

    @Override
    protected JList createList() {
        JList list = super.createList();
        list.setOpaque(false);
        return list;
    }

    @Override
    protected JScrollPane createScroller() {
        JScrollPane sp = new JScrollPane(list);
        sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setHorizontalScrollBar(null);
        sp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        sp.getVerticalScrollBar().setOpaque(false);
        sp.getVerticalScrollBar().setUI(new ScrollBarUI(foreColor));
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        return sp;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Rectangle rect = getVisibleRect();
        Graphics2D g2d = (Graphics2D) g;

        // 避免锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(ImageUtils.getAvgRGB(f.getGlobalPanel().getBackgroundImage()));
//                g2d.fillRoundRect(rect.x, rect.y , rect.width, rect.height , 8, 8);
        g2d.fillRoundRect(rect.x + pixels, rect.y + pixels, rect.width - 2 * pixels, rect.height - 2 * pixels, 8, 8);

        // 画边框阴影
        for (int i = 0; i < pixels; i++) {
            g2d.setColor(new Color(0, 0, 0, ((TOP_OPACITY / pixels) * i)));
            g2d.drawRoundRect(i, i, getWidth() - ((i * 2) + 1), getHeight() - ((i * 2) + 1), 8, 8);
        }
    }

    @Override
    protected void paintBorder(Graphics g) {
//        super.paintBorder(g);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b) {
            // 使 JPopupMenu 对应的 Window 透明！
            Window w = SwingUtilities.getWindowAncestor(this);
            if(!w.getBackground().equals(Color.black)) {
                w.setVisible(false);
                w.setBackground(Color.black);
            }
            w.setBackground(Colors.TRANSLUCENT);
//            AWTUtilities.setWindowOpaque(w, false);
            w.setVisible(true);
        }
        f.currPopup = b ? this : null;
    }
}
