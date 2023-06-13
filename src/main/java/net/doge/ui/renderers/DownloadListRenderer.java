package net.doge.ui.renderers;

import net.doge.constants.*;
import net.doge.models.Task;
import net.doge.ui.components.CustomLabel;
import net.doge.ui.components.panel.CustomPanel;
import net.doge.ui.components.CustomSlider;
import net.doge.ui.componentui.slider.MuteSliderUI;
import net.doge.utils.FileUtil;
import net.doge.utils.ImageUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.doge.utils.StringUtil;

import javax.swing.*;
import java.awt.*;

/**
 * @Author yzx
 * @Description
 * @Date 2020/12/7
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DownloadListRenderer extends DefaultListCellRenderer {
    // 属性不能用 font，不然重复！
    private Font customFont = Fonts.NORMAL;
    private Color foreColor;
    private Color selectedColor;
    private Color textColor;
    private Color iconColor;
    private int hoverIndex = -1;

    private static ImageIcon taskIcon = new ImageIcon(ImageUtil.width(ImageUtil.read(SimplePath.ICON_PATH + "taskItem.png"), ImageConstants.smallWidth));

    public void setIconColor(Color iconColor) {
        this.iconColor = iconColor;
        taskIcon = ImageUtil.dye(taskIcon, iconColor);
    }

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Task task = (Task) value;

        CustomPanel outerPanel = new CustomPanel();
        CustomLabel iconLabel = new CustomLabel(taskIcon);
        CustomLabel nameLabel = new CustomLabel();
        CustomLabel typeLabel = new CustomLabel();
        CustomLabel sizeLabel = new CustomLabel();
        CustomSlider progressSlider = new CustomSlider();
        CustomLabel percentLabel = new CustomLabel();
        CustomLabel statusLabel = new CustomLabel();

        progressSlider.setMinimum(0);
        progressSlider.setMaximum(100);
        progressSlider.setUI(new MuteSliderUI(progressSlider, textColor));

        outerPanel.setForeground(isSelected ? selectedColor : foreColor);
        iconLabel.setForeground(textColor);
        nameLabel.setForeground(textColor);
        typeLabel.setForeground(textColor);
        sizeLabel.setForeground(textColor);
        percentLabel.setForeground(textColor);
        statusLabel.setForeground(textColor);

        iconLabel.setFont(customFont);
        nameLabel.setFont(customFont);
        typeLabel.setFont(customFont);
        sizeLabel.setFont(customFont);
        percentLabel.setFont(customFont);
        statusLabel.setFont(customFont);

        GridLayout layout = new GridLayout(1, 5);
        layout.setHgap(15);
        outerPanel.setLayout(layout);

        outerPanel.add(iconLabel);
        outerPanel.add(nameLabel);
        outerPanel.add(typeLabel);
        outerPanel.add(sizeLabel);
        outerPanel.add(progressSlider);
        outerPanel.add(percentLabel);
        outerPanel.add(statusLabel);

        final int maxWidth = (list.getVisibleRect().width - 10 - (outerPanel.getComponentCount() - 1) * layout.getHgap()) / outerPanel.getComponentCount();
        String type = StringUtil.textToHtml(TaskType.s[task.getType()]);
        String name = StringUtil.textToHtml(StringUtil.wrapLineByWidth(task.getName(), maxWidth));
        double percent = task.isProcessing() ? task.getPercent() : task.isFinished() ? 100 : 0;
        String percentStr = StringUtil.textToHtml(String.format("%.2f %%", percent));
        String size = StringUtil.textToHtml(StringUtil.wrapLineByWidth(
                String.format("%s / %s", FileUtil.getUnitString(task.getFinished()), FileUtil.getUnitString(task.getTotal())), maxWidth));
        String status = StringUtil.textToHtml(TaskStatus.s[task.getStatus()]);

        nameLabel.setText(name);
        typeLabel.setText(type);
        sizeLabel.setText(size);
        progressSlider.setValue((int) percent);
        percentLabel.setText(percentStr);
        statusLabel.setText(status);

        Dimension ps = nameLabel.getPreferredSize();
        Dimension ps2 = sizeLabel.getPreferredSize();
        int ph = Math.max(ps.height, ps2.height);
        Dimension d = new Dimension(list.getVisibleRect().width - 10, Math.max(ph + 10, 46));
        outerPanel.setPreferredSize(d);
//        list.setFixedCellWidth(list.getVisibleRect().width - 10);

        outerPanel.setBluntDrawBg(true);
        outerPanel.setDrawBg(isSelected || index == hoverIndex);

        return outerPanel;
    }
}
