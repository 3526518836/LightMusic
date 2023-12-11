package net.doge.ui.widget.dialog;

import com.alibaba.fastjson2.JSONObject;
import javafx.application.Platform;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import net.doge.constant.async.GlobalExecutors;
import net.doge.constant.lang.I18n;
import net.doge.constant.system.AudioQuality;
import net.doge.constant.system.SimplePath;
import net.doge.constant.system.VideoQuality;
import net.doge.constant.ui.BlurConstants;
import net.doge.constant.ui.Colors;
import net.doge.constant.ui.LyricAlignment;
import net.doge.constant.ui.SpectrumConstants;
import net.doge.constant.window.CloseWindowOptions;
import net.doge.constant.window.WindowSize;
import net.doge.constant.window.WindowState;
import net.doge.ui.MainFrame;
import net.doge.ui.widget.button.DialogButton;
import net.doge.ui.widget.checkbox.CustomCheckBox;
import net.doge.ui.widget.combobox.CustomComboBox;
import net.doge.ui.widget.combobox.ui.ComboBoxUI;
import net.doge.ui.widget.dialog.factory.AbstractTitledDialog;
import net.doge.ui.widget.label.CustomLabel;
import net.doge.ui.widget.panel.CustomPanel;
import net.doge.ui.widget.scrollpane.CustomScrollPane;
import net.doge.ui.widget.scrollpane.ui.ScrollBarUI;
import net.doge.ui.widget.tabbedpane.CustomTabbedPane;
import net.doge.ui.widget.tabbedpane.ui.TabbedPaneUI;
import net.doge.ui.widget.textfield.CustomTextField;
import net.doge.ui.widget.textfield.SafeDocument;
import net.doge.util.collection.ListUtil;
import net.doge.util.common.JsonUtil;
import net.doge.util.common.StringUtil;
import net.doge.util.system.FileUtil;
import net.doge.util.system.KeyUtil;
import net.doge.util.ui.ColorUtil;
import net.doge.util.ui.ImageUtil;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author Doge
 * @Description 设置对话框
 * @Date 2020/12/15
 */
public class SettingDialog extends AbstractTitledDialog {
    // 目录不存在提示
    private final String CATALOG_NOT_FOUND_MSG = I18n.getText("catalogNotFoundMsg");

    private CustomPanel centerPanel = new CustomPanel();
    private CustomPanel buttonPanel = new CustomPanel();

    // 标签页
    private CustomTabbedPane tabbedPane = new CustomTabbedPane(CustomTabbedPane.LEFT);
    private CustomPanel generalPanel = new CustomPanel();
    private CustomLabel generalLabel = new CustomLabel(I18n.getText("generalTab"));
    private Box generalContentBox = new Box(BoxLayout.Y_AXIS);
    private CustomScrollPane generalScrollPane = new CustomScrollPane(generalContentBox);
    private CustomPanel appearancePanel = new CustomPanel();
    private CustomLabel appearanceLabel = new CustomLabel(I18n.getText("appearance"));
    private Box appearanceContentBox = new Box(BoxLayout.Y_AXIS);
    private CustomScrollPane appearanceScrollPane = new CustomScrollPane(appearanceContentBox);
    private CustomPanel downloadAndCachePanel = new CustomPanel();
    private CustomLabel downloadAndCacheLabel = new CustomLabel(I18n.getText("downloadAndCache"));
    private Box downloadAndCacheContentBox = new Box(BoxLayout.Y_AXIS);
    private CustomScrollPane downloadAndCacheScrollPane = new CustomScrollPane(downloadAndCacheContentBox);
    private CustomPanel playbackPanel = new CustomPanel();
    private CustomLabel playbackLabel = new CustomLabel(I18n.getText("playback"));
    private Box playbackContentBox = new Box(BoxLayout.Y_AXIS);
    private CustomScrollPane playbackScrollPane = new CustomScrollPane(playbackContentBox);
    private CustomPanel hotKeyPanel = new CustomPanel();
    private CustomLabel hotKeyLabel = new CustomLabel(I18n.getText("hotkey"));
    private Box hotKeyContentBox = new Box(BoxLayout.Y_AXIS);
    private CustomScrollPane hoyKeyScrollPane = new CustomScrollPane(hotKeyContentBox);

    // 设置项
    private CustomPanel autoUpdatePanel = new CustomPanel();
    private CustomCheckBox autoUpdateCheckBox = new CustomCheckBox(I18n.getText("autoUpdate"));
    private CustomPanel videoOnlyPanel = new CustomPanel();
    private CustomCheckBox videoOnlyCheckBox = new CustomCheckBox(I18n.getText("videoOnly"));
    private CustomPanel langPanel = new CustomPanel();
    private CustomLabel langLabel = new CustomLabel(I18n.getText("lang"));
    private CustomComboBox<String> langComboBox = new CustomComboBox<>();
    private CustomPanel closeOptionPanel = new CustomPanel();
    private CustomLabel closeOptionLabel = new CustomLabel(I18n.getText("closeOption"));
    private CustomComboBox<String> closeOptionComboBox = new CustomComboBox<>();
    private CustomPanel windowSizePanel = new CustomPanel();
    private CustomLabel windowSizeLabel = new CustomLabel(I18n.getText("windowSize"));
    private CustomComboBox<String> windowSizeComboBox = new CustomComboBox<>();

    private CustomPanel showTabTextPanel = new CustomPanel();
    private CustomCheckBox showTabTextCheckBox = new CustomCheckBox(I18n.getText("showTabText"));
    private CustomPanel lrcAlignmentPanel = new CustomPanel();
    private CustomLabel lrcAlignmentLabel = new CustomLabel(I18n.getText("lrcAlignment"));
    private CustomComboBox<String> lrcAlignmentComboBox = new CustomComboBox<>();
    private final int specMaxHeightLimit = 150;
    private CustomPanel specMaxHeightPanel = new CustomPanel();
    private CustomLabel specMaxHeightLabel = new CustomLabel(String.format(I18n.getText("specMaxHeight"), specMaxHeightLimit));
    private CustomTextField specMaxHeightTextField = new CustomTextField(10);
    private CustomPanel gsFactorPanel = new CustomPanel();
    private CustomLabel gsFactorLabel = new CustomLabel(I18n.getText("gsFactor"));
    private CustomComboBox<String> gsFactorComboBox = new CustomComboBox<>();
    private CustomPanel darkerFactorPanel = new CustomPanel();
    private CustomLabel darkerFactorLabel = new CustomLabel(I18n.getText("darkerFactor"));
    private CustomComboBox<String> darkerFactorComboBox = new CustomComboBox<>();

    private CustomPanel autoDownloadLrcPanel = new CustomPanel();
    private CustomCheckBox autoDownloadLrcCheckBox = new CustomCheckBox(I18n.getText("autoDownloadLrc"));
    //    private CustomPanel verbatimTimelinePanel = new CustomPanel();
//    private CustomCheckBox verbatimTimelineCheckBox = new CustomCheckBox(I18n.getText("verbatimTimeline"));
    private CustomPanel musicDownPanel = new CustomPanel();
    private CustomLabel musicDownLabel = new CustomLabel(I18n.getText("musicDown"));
    private CustomTextField musicDownPathTextField = new CustomTextField(20);
    private DialogButton changeMusicDownPathButton;
    private DialogButton openMusicDownPathButton;
    private CustomPanel mvDownPanel = new CustomPanel();
    private CustomLabel mvDownLabel = new CustomLabel(I18n.getText("mvDown"));
    private CustomTextField mvDownPathTextField = new CustomTextField(20);
    private DialogButton changeMvDownPathButton;
    private DialogButton openMvDownPathButton;
    private CustomPanel cachePanel = new CustomPanel();
    private CustomLabel cacheLabel = new CustomLabel(I18n.getText("cache"));
    private CustomTextField cachePathTextField = new CustomTextField(20);
    private DialogButton changeCachePathButton;
    private DialogButton openCachePathButton;
    private final int maxCacheSizeLimit = 4096;
    private CustomPanel maxCacheSizePanel = new CustomPanel();
    private CustomLabel maxCacheSizeLabel = new CustomLabel(String.format(I18n.getText("maxCacheSize"), maxCacheSizeLimit));
    private CustomTextField maxCacheSizeTextField = new CustomTextField(10);

    private CustomPanel audioQualityPanel = new CustomPanel();
    private CustomLabel audioQualityLabel = new CustomLabel(I18n.getText("audioQuality"));
    private CustomComboBox<String> audioQualityComboBox = new CustomComboBox<>();
    private CustomPanel videoQualityPanel = new CustomPanel();
    private CustomLabel videoQualityLabel = new CustomLabel(I18n.getText("videoQuality"));
    private CustomComboBox<String> videoQualityComboBox = new CustomComboBox<>();
    private CustomPanel fobPanel = new CustomPanel();
    private CustomLabel fobLabel = new CustomLabel(I18n.getText("fob"));
    private CustomComboBox<String> fobComboBox = new CustomComboBox<>();
    private CustomPanel balancePanel = new CustomPanel();
    private CustomLabel balanceLabel = new CustomLabel(I18n.getText("balance"));
    private CustomComboBox<String> balanceComboBox = new CustomComboBox<>();
    private final int maxHistoryCountLimit = 500;
    private CustomPanel maxHistoryCountPanel = new CustomPanel();
    private CustomLabel maxHistoryCountLabel = new CustomLabel(String.format(I18n.getText("maxHistoryCount"), maxHistoryCountLimit));
    private CustomTextField maxHistoryCountTextField = new CustomTextField(10);
    private final int maxSearchHistoryLimit = 100;
    private CustomPanel maxSearchHistoryCountPanel = new CustomPanel();
    private CustomLabel maxSearchHistoryCountLabel = new CustomLabel(String.format(I18n.getText("maxSearchHistoryCount"), maxSearchHistoryLimit));
    private CustomTextField maxSearchHistoryCountTextField = new CustomTextField(10);
    private final int maxConcurrentTaskCountLimit = 3;
    private CustomPanel maxConcurrentTaskCountPanel = new CustomPanel();
    private CustomLabel maxConcurrentTaskCountLabel = new CustomLabel(String.format(I18n.getText("maxConcurrentTaskCount"), maxConcurrentTaskCountLimit));
    private CustomTextField maxConcurrentTaskCountTextField = new CustomTextField(10);
    private CustomPanel backupPanel = new CustomPanel();
    private CustomLabel backupLabel = new CustomLabel(I18n.getText("backupAndRestore"));
    private DialogButton backupListButton;
    private DialogButton restoreListButton;

    public List<Integer> playOrPauseKeys = new LinkedList<>();
    public List<Integer> playLastKeys = new LinkedList<>();
    public List<Integer> playNextKeys = new LinkedList<>();
    public List<Integer> backwardKeys = new LinkedList<>();
    public List<Integer> forwardKeys = new LinkedList<>();
    public List<Integer> videoFullScreenKeys = new LinkedList<>();
    public LinkedList<Integer> currKeys = new LinkedList<>();
    private CustomPanel keyPanel = new CustomPanel();
    private CustomLabel keyLabel = new CustomLabel(I18n.getText("key"));
    private CustomCheckBox enableKeyCheckBox = new CustomCheckBox(I18n.getText("enableKey"));
    private CustomPanel playOrPausePanel = new CustomPanel();
    private CustomLabel playOrPauseLabel = new CustomLabel(I18n.getText("playOrPause"));
    private CustomTextField playOrPauseTextField = new CustomTextField(10);
    private CustomPanel playLastPanel = new CustomPanel();
    private CustomLabel playLastLabel = new CustomLabel(I18n.getText("playLast"));
    private CustomTextField playLastTextField = new CustomTextField(10);
    private CustomPanel playNextPanel = new CustomPanel();
    private CustomLabel playNextLabel = new CustomLabel(I18n.getText("playNext"));
    private CustomTextField playNextTextField = new CustomTextField(10);
    private CustomPanel backwardPanel = new CustomPanel();
    private CustomLabel backwardLabel = new CustomLabel(I18n.getText("backward"));
    private CustomTextField backwardTextField = new CustomTextField(10);
    private CustomPanel forwardPanel = new CustomPanel();
    private CustomLabel forwardLabel = new CustomLabel(I18n.getText("forward"));
    private CustomTextField forwardTextField = new CustomTextField(10);
    private CustomPanel videoFullScreenPanel = new CustomPanel();
    private CustomLabel videoFullScreenLabel = new CustomLabel(I18n.getText("videoFullScreen"));
    private CustomTextField videoFullScreenTextField = new CustomTextField(10);

    private DialogButton okButton;
    private DialogButton applyButton;
    private DialogButton cancelButton;

    private Object currKeyComp;
    private AWTEventListener keyBindListener;
    private AWTEventListener mouseListener;

    public SettingDialog(MainFrame f) {
        super(f, I18n.getText("settingTitle"));

        Color textColor = f.currUIStyle.getTextColor();
        okButton = new DialogButton(I18n.getText("save"), textColor);
        applyButton = new DialogButton(I18n.getText("dialogApply"), textColor);
        cancelButton = new DialogButton(I18n.getText("cancel"), textColor);
    }

    public void showDialog() {
        setResizable(false);
        setSize(800, 650);

        globalPanel.setLayout(new BorderLayout());

        initTitleBar();
        initView();
        initSettings();

        okButton.addActionListener(e -> {
            if (!applySettings()) return;
            f.currDialogs.remove(this);
            // 移除监听器
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            toolkit.removeAWTEventListener(keyBindListener);
            toolkit.removeAWTEventListener(mouseListener);
            dispose();
        });
        applyButton.addActionListener(e -> {
            applySettings();
            new TipDialog(f, I18n.getText("applySuccess"), true).showDialog();
        });
        cancelButton.addActionListener(e -> close());
        buttonPanel.add(okButton);
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        globalPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(globalPanel);
        setUndecorated(true);
        setBackground(Colors.TRANSPARENT);
        setLocationRelativeTo(null);

        updateBlur();

        f.currDialogs.add(this);
        setVisible(true);
    }

    private void initView() {
        // 标签页
        generalPanel.setLayout(new BorderLayout());
        appearancePanel.setLayout(new BorderLayout());
        downloadAndCachePanel.setLayout(new BorderLayout());
        playbackPanel.setLayout(new BorderLayout());
        hotKeyPanel.setLayout(new BorderLayout());

        generalLabel.setHorizontalAlignment(SwingConstants.LEFT);
        appearanceLabel.setHorizontalAlignment(SwingConstants.LEFT);
        downloadAndCacheLabel.setHorizontalAlignment(SwingConstants.LEFT);
        playbackLabel.setHorizontalAlignment(SwingConstants.LEFT);
        hotKeyLabel.setHorizontalAlignment(SwingConstants.LEFT);

        Border eb = BorderFactory.createEmptyBorder(0, 10, 0, 0);
        generalPanel.setBorder(eb);
        appearancePanel.setBorder(eb);
        downloadAndCachePanel.setBorder(eb);
        playbackPanel.setBorder(eb);
        hotKeyPanel.setBorder(eb);

        generalPanel.add(generalLabel, BorderLayout.CENTER);
        appearancePanel.add(appearanceLabel, BorderLayout.CENTER);
        downloadAndCachePanel.add(downloadAndCacheLabel, BorderLayout.CENTER);
        playbackPanel.add(playbackLabel, BorderLayout.CENTER);
        hotKeyPanel.add(hotKeyLabel, BorderLayout.CENTER);

        tabbedPane.addTab(null, null, generalScrollPane);
        tabbedPane.addTab(null, null, appearanceScrollPane);
        tabbedPane.addTab(null, null, downloadAndCacheScrollPane);
        tabbedPane.addTab(null, null, playbackScrollPane);
        tabbedPane.addTab(null, null, hoyKeyScrollPane);

        tabbedPane.setTabComponentAt(0, generalPanel);
        tabbedPane.setTabComponentAt(1, appearancePanel);
        tabbedPane.setTabComponentAt(2, downloadAndCachePanel);
        tabbedPane.setTabComponentAt(3, playbackPanel);
        tabbedPane.setTabComponentAt(4, hotKeyPanel);

        tabbedPane.setUI(new TabbedPaneUI());
        f.updateTabUI(tabbedPane, f.currUIStyle);
        tabbedPane.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                f.updateTabUI(tabbedPane, f.currUIStyle, e.getPoint());
            }
        });
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                f.updateTabUI(tabbedPane, f.currUIStyle);
            }
        });
        tabbedPane.addChangeListener(e -> f.updateTabUI(tabbedPane, f.currUIStyle));

        Color scrollBarColor = f.currUIStyle.getScrollBarColor();
        generalScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        generalScrollPane.setVUI(new ScrollBarUI(scrollBarColor));
        appearanceScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        appearanceScrollPane.setVUI(new ScrollBarUI(scrollBarColor));
        downloadAndCacheScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        downloadAndCacheScrollPane.setVUI(new ScrollBarUI(scrollBarColor));
        playbackScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        playbackScrollPane.setVUI(new ScrollBarUI(scrollBarColor));
        hoyKeyScrollPane.setHUI(new ScrollBarUI(scrollBarColor));
        hoyKeyScrollPane.setVUI(new ScrollBarUI(scrollBarColor));

        // 标签大小
        Dimension d = new Dimension(120, 40);
        generalPanel.setPreferredSize(d);
        appearancePanel.setPreferredSize(d);
        downloadAndCachePanel.setPreferredSize(d);
        playbackPanel.setPreferredSize(d);
        hotKeyPanel.setPreferredSize(d);

        // 对齐
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        autoUpdatePanel.setLayout(fl);
        videoOnlyPanel.setLayout(fl);
        langPanel.setLayout(fl);
        closeOptionPanel.setLayout(fl);
        windowSizePanel.setLayout(fl);
        showTabTextPanel.setLayout(fl);
        lrcAlignmentPanel.setLayout(fl);
        specMaxHeightPanel.setLayout(fl);
        gsFactorPanel.setLayout(fl);
        darkerFactorPanel.setLayout(fl);
        autoDownloadLrcPanel.setLayout(fl);
//        verbatimTimelinePanel.setLayout(fl);
        musicDownPanel.setLayout(fl);
        mvDownPanel.setLayout(fl);
        cachePanel.setLayout(fl);
        maxCacheSizePanel.setLayout(fl);
        maxHistoryCountPanel.setLayout(fl);
        maxSearchHistoryCountPanel.setLayout(fl);
        maxConcurrentTaskCountPanel.setLayout(fl);
        audioQualityPanel.setLayout(fl);
        videoQualityPanel.setLayout(fl);
        fobPanel.setLayout(fl);
        balancePanel.setLayout(fl);
        backupPanel.setLayout(fl);
        keyPanel.setLayout(fl);
        playOrPausePanel.setLayout(fl);
        playLastPanel.setLayout(fl);
        playNextPanel.setLayout(fl);
        backwardPanel.setLayout(fl);
        forwardPanel.setLayout(fl);
        videoFullScreenPanel.setLayout(fl);

        // 最大尺寸
        d = new Dimension(Integer.MAX_VALUE, 30);
        autoUpdatePanel.setMaximumSize(d);
        videoOnlyPanel.setMaximumSize(d);
        langPanel.setMaximumSize(d);
        closeOptionPanel.setMaximumSize(d);
        windowSizePanel.setMaximumSize(d);
        showTabTextPanel.setMaximumSize(d);
        lrcAlignmentPanel.setMaximumSize(d);
        specMaxHeightPanel.setMaximumSize(d);
        gsFactorPanel.setMaximumSize(d);
        darkerFactorPanel.setMaximumSize(d);
        autoDownloadLrcPanel.setMaximumSize(d);
//        verbatimTimelinePanel.setMaximumSize(d);
        musicDownPanel.setMaximumSize(d);
        mvDownPanel.setMaximumSize(d);
        cachePanel.setMaximumSize(d);
        maxCacheSizePanel.setMaximumSize(d);
        maxHistoryCountPanel.setMaximumSize(d);
        maxSearchHistoryCountPanel.setMaximumSize(d);
        maxConcurrentTaskCountPanel.setMaximumSize(d);
        audioQualityPanel.setMaximumSize(d);
        videoQualityPanel.setMaximumSize(d);
        fobPanel.setMaximumSize(d);
        balancePanel.setMaximumSize(d);
        backupPanel.setMaximumSize(d);
        keyPanel.setMaximumSize(d);
        playOrPausePanel.setMaximumSize(d);
        playLastPanel.setMaximumSize(d);
        playNextPanel.setMaximumSize(d);
        backwardPanel.setMaximumSize(d);
        forwardPanel.setMaximumSize(d);
        videoFullScreenPanel.setMaximumSize(d);

        // 字体颜色
        Color textColor = f.currUIStyle.getTextColor();
        generalLabel.setForeground(textColor);
        appearanceLabel.setForeground(textColor);
        downloadAndCacheLabel.setForeground(textColor);
        playbackLabel.setForeground(textColor);
        hotKeyLabel.setForeground(textColor);

        autoUpdateCheckBox.setForeground(textColor);
        videoOnlyCheckBox.setForeground(textColor);
        showTabTextCheckBox.setForeground(textColor);
        lrcAlignmentLabel.setForeground(textColor);
        specMaxHeightLabel.setForeground(textColor);
        gsFactorLabel.setForeground(textColor);
        darkerFactorLabel.setForeground(textColor);
        autoDownloadLrcCheckBox.setForeground(textColor);
//        verbatimTimelineCheckBox.setForeground(textColor);
        musicDownLabel.setForeground(textColor);
        mvDownLabel.setForeground(textColor);
        cacheLabel.setForeground(textColor);
        maxCacheSizeLabel.setForeground(textColor);
        maxHistoryCountLabel.setForeground(textColor);
        maxSearchHistoryCountLabel.setForeground(textColor);
        maxConcurrentTaskCountLabel.setForeground(textColor);
        langLabel.setForeground(textColor);
        closeOptionLabel.setForeground(textColor);
        windowSizeLabel.setForeground(textColor);
        audioQualityLabel.setForeground(textColor);
        videoQualityLabel.setForeground(textColor);
        fobLabel.setForeground(textColor);
        balanceLabel.setForeground(textColor);
        backupLabel.setForeground(textColor);
        keyLabel.setForeground(textColor);
        enableKeyCheckBox.setForeground(textColor);
        playOrPauseLabel.setForeground(textColor);
        playLastLabel.setForeground(textColor);
        playNextLabel.setForeground(textColor);
        backwardLabel.setForeground(textColor);
        forwardLabel.setForeground(textColor);
        videoFullScreenLabel.setForeground(textColor);

        // 文本框
        Color darkerTextAlphaColor = ColorUtil.deriveAlphaColor(ColorUtil.darker(textColor), 0.5f);
        specMaxHeightTextField.setForeground(textColor);
        specMaxHeightTextField.setCaretColor(textColor);
        specMaxHeightTextField.setSelectedTextColor(textColor);
        specMaxHeightTextField.setSelectionColor(darkerTextAlphaColor);
        SafeDocument doc = new SafeDocument(0, specMaxHeightLimit);
        specMaxHeightTextField.setDocument(doc);
        musicDownPathTextField.setForeground(textColor);
        musicDownPathTextField.setCaretColor(textColor);
        musicDownPathTextField.setSelectedTextColor(textColor);
        musicDownPathTextField.setSelectionColor(darkerTextAlphaColor);
        mvDownPathTextField.setForeground(textColor);
        mvDownPathTextField.setCaretColor(textColor);
        mvDownPathTextField.setSelectedTextColor(textColor);
        mvDownPathTextField.setSelectionColor(darkerTextAlphaColor);
        cachePathTextField.setForeground(textColor);
        cachePathTextField.setCaretColor(textColor);
        cachePathTextField.setSelectedTextColor(textColor);
        cachePathTextField.setSelectionColor(darkerTextAlphaColor);
        maxCacheSizeTextField.setForeground(textColor);
        maxCacheSizeTextField.setCaretColor(textColor);
        maxCacheSizeTextField.setSelectedTextColor(textColor);
        maxCacheSizeTextField.setSelectionColor(darkerTextAlphaColor);
        doc = new SafeDocument(0, maxCacheSizeLimit);
        maxCacheSizeTextField.setDocument(doc);
        maxHistoryCountTextField.setForeground(textColor);
        maxHistoryCountTextField.setCaretColor(textColor);
        maxHistoryCountTextField.setSelectedTextColor(textColor);
        maxHistoryCountTextField.setSelectionColor(darkerTextAlphaColor);
        doc = new SafeDocument(0, maxHistoryCountLimit);
        maxHistoryCountTextField.setDocument(doc);
        maxSearchHistoryCountTextField.setForeground(textColor);
        maxSearchHistoryCountTextField.setCaretColor(textColor);
        maxSearchHistoryCountTextField.setSelectedTextColor(textColor);
        maxSearchHistoryCountTextField.setSelectionColor(darkerTextAlphaColor);
        doc = new SafeDocument(0, maxSearchHistoryLimit);
        maxSearchHistoryCountTextField.setDocument(doc);
        maxConcurrentTaskCountTextField.setForeground(textColor);
        maxConcurrentTaskCountTextField.setCaretColor(textColor);
        maxConcurrentTaskCountTextField.setSelectedTextColor(textColor);
        maxConcurrentTaskCountTextField.setSelectionColor(darkerTextAlphaColor);
        doc = new SafeDocument(1, maxConcurrentTaskCountLimit);
        maxConcurrentTaskCountTextField.setDocument(doc);
        keyBindListener = new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (!(event instanceof KeyEvent) || currKeyComp == null) return;
                KeyEvent e = (KeyEvent) event;
                int code = e.getKeyCode();
                boolean released = e.getID() == KeyEvent.KEY_RELEASED, pressed = e.getID() == KeyEvent.KEY_PRESSED;
                if (!released && !pressed) return;

                if (released && !currKeys.isEmpty()) currKeys.removeLast();

                if (pressed) {
                    CustomTextField tf = (CustomTextField) currKeyComp;
                    if (currKeys.contains(code)) return;
                    currKeys.add(code);
                    // 检查重复按键
                    List<Integer> keyList = checkKeyDuplicated();
                    if (keyList != null) {
                        if (keyList == playOrPauseKeys) {
                            playOrPauseKeys.clear();
                            playOrPauseTextField.setText("");
                        } else if (keyList == playLastKeys) {
                            playLastKeys.clear();
                            playLastTextField.setText("");
                        } else if (keyList == playNextKeys) {
                            playNextKeys.clear();
                            playNextTextField.setText("");
                        } else if (keyList == backwardKeys) {
                            backwardKeys.clear();
                            backwardTextField.setText("");
                        } else if (keyList == forwardKeys) {
                            forwardKeys.clear();
                            forwardTextField.setText("");
                        } else if (keyList == videoFullScreenKeys) {
                            videoFullScreenKeys.clear();
                            videoFullScreenTextField.setText("");
                        }
                    }
                    // 暂存快捷键
                    if (tf == playOrPauseTextField) {
                        playOrPauseKeys.clear();
                        playOrPauseKeys.addAll(currKeys);
                    } else if (tf == playLastTextField) {
                        playLastKeys.clear();
                        playLastKeys.addAll(currKeys);
                    } else if (tf == playNextTextField) {
                        playNextKeys.clear();
                        playNextKeys.addAll(currKeys);
                    } else if (tf == backwardTextField) {
                        backwardKeys.clear();
                        backwardKeys.addAll(currKeys);
                    } else if (tf == forwardTextField) {
                        forwardKeys.clear();
                        forwardKeys.addAll(currKeys);
                    } else if (tf == videoFullScreenTextField) {
                        videoFullScreenKeys.clear();
                        videoFullScreenKeys.addAll(currKeys);
                    }
                    tf.setText(KeyUtil.join(currKeys));
                }
            }

            // 检查是否有重复按键
            private List<Integer> checkKeyDuplicated() {
                if (ListUtil.equals(currKeys, playOrPauseKeys)) return playOrPauseKeys;
                if (ListUtil.equals(currKeys, playLastKeys)) return playLastKeys;
                if (ListUtil.equals(currKeys, playNextKeys)) return playNextKeys;
                if (ListUtil.equals(currKeys, backwardKeys)) return backwardKeys;
                if (ListUtil.equals(currKeys, forwardKeys)) return forwardKeys;
                if (ListUtil.equals(currKeys, videoFullScreenKeys)) return videoFullScreenKeys;
                return null;
            }
        };
        mouseListener = event -> {
            if (event instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) event;
                if (me.getID() == MouseEvent.MOUSE_PRESSED) currKeyComp = null;
            }
        };
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        toolkit.addAWTEventListener(keyBindListener, AWTEvent.KEY_EVENT_MASK);
        toolkit.addAWTEventListener(mouseListener, AWTEvent.MOUSE_EVENT_MASK);
        FocusAdapter focusAdapter = new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                currKeyComp = e.getSource();
                currKeys.clear();
            }
        };
        playOrPauseTextField.setForeground(textColor);
        playOrPauseTextField.setCaretColor(textColor);
        playOrPauseTextField.setSelectedTextColor(textColor);
        playOrPauseTextField.setSelectionColor(darkerTextAlphaColor);
        playOrPauseTextField.setEditable(false);
        playOrPauseTextField.addFocusListener(focusAdapter);
        playLastTextField.setForeground(textColor);
        playLastTextField.setCaretColor(textColor);
        playLastTextField.setSelectedTextColor(textColor);
        playLastTextField.setSelectionColor(darkerTextAlphaColor);
        playLastTextField.setEditable(false);
        playLastTextField.addFocusListener(focusAdapter);
        playNextTextField.setForeground(textColor);
        playNextTextField.setCaretColor(textColor);
        playNextTextField.setSelectedTextColor(textColor);
        playNextTextField.setSelectionColor(darkerTextAlphaColor);
        playNextTextField.setEditable(false);
        playNextTextField.addFocusListener(focusAdapter);
        backwardTextField.setForeground(textColor);
        backwardTextField.setCaretColor(textColor);
        backwardTextField.setSelectedTextColor(textColor);
        backwardTextField.setSelectionColor(darkerTextAlphaColor);
        backwardTextField.setEditable(false);
        backwardTextField.addFocusListener(focusAdapter);
        forwardTextField.setForeground(textColor);
        forwardTextField.setCaretColor(textColor);
        forwardTextField.setSelectedTextColor(textColor);
        forwardTextField.setSelectionColor(darkerTextAlphaColor);
        forwardTextField.setEditable(false);
        forwardTextField.addFocusListener(focusAdapter);
        videoFullScreenTextField.setForeground(textColor);
        videoFullScreenTextField.setCaretColor(textColor);
        videoFullScreenTextField.setSelectedTextColor(textColor);
        videoFullScreenTextField.setSelectionColor(darkerTextAlphaColor);
        videoFullScreenTextField.setEditable(false);
        videoFullScreenTextField.addFocusListener(focusAdapter);

        // 下拉框 UI
        lrcAlignmentComboBox.setUI(new ComboBoxUI(lrcAlignmentComboBox, f));
        gsFactorComboBox.setUI(new ComboBoxUI(gsFactorComboBox, f));
        darkerFactorComboBox.setUI(new ComboBoxUI(darkerFactorComboBox, f));
        langComboBox.setUI(new ComboBoxUI(langComboBox, f));
        closeOptionComboBox.setUI(new ComboBoxUI(closeOptionComboBox, f));
        windowSizeComboBox.setUI(new ComboBoxUI(windowSizeComboBox, f));
        audioQualityComboBox.setUI(new ComboBoxUI(audioQualityComboBox, f));
        videoQualityComboBox.setUI(new ComboBoxUI(videoQualityComboBox, f));
        fobComboBox.setUI(new ComboBoxUI(fobComboBox, f));
        balanceComboBox.setUI(new ComboBoxUI(balanceComboBox, f));

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(I18n.getText("chooseFolder"));

        // 按钮
        changeMusicDownPathButton = new DialogButton(I18n.getText("change"), textColor);
        changeMusicDownPathButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir == null) return;
                // 文件夹不存在直接跳出
                if (!dir.exists()) return;
                musicDownPathTextField.setText(dir.getAbsolutePath());
                pack();
            });
        });
        openMusicDownPathButton = new DialogButton(I18n.getText("dialogOpen"), textColor);
        openMusicDownPathButton.addActionListener(e -> {
            try {
                File dir = new File(musicDownPathTextField.getText());
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        changeMvDownPathButton = new DialogButton(I18n.getText("change"), textColor);
        changeMvDownPathButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir == null) return;
                // 文件夹不存在直接跳出
                if (!dir.exists()) return;
                mvDownPathTextField.setText(dir.getAbsolutePath());
                pack();
            });
        });
        openMvDownPathButton = new DialogButton(I18n.getText("dialogOpen"), textColor);
        openMvDownPathButton.addActionListener(e -> {
            try {
                File dir = new File(mvDownPathTextField.getText());
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        changeCachePathButton = new DialogButton(I18n.getText("change"), textColor);
        changeCachePathButton.addActionListener(e -> {
            Platform.runLater(() -> {
                File dir = dirChooser.showDialog(null);
                if (dir == null) return;
                // 文件夹不存在直接跳出
                if (!dir.exists()) return;
                cachePathTextField.setText(dir.getAbsolutePath());
                pack();
            });
        });
        openCachePathButton = new DialogButton(I18n.getText("dialogOpen"), textColor);
        openCachePathButton.addActionListener(e -> {
            try {
                File dir = new File(cachePathTextField.getText());
                if (!dir.exists()) {
                    new TipDialog(f, CATALOG_NOT_FOUND_MSG, true).showDialog();
                    return;
                }
                Desktop.getDesktop().open(dir);
            } catch (IOException ex) {

            }
        });

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(I18n.getText("jsonFile"), "*.json");
        fileChooser.getExtensionFilters().add(filter);
        backupListButton = new DialogButton(I18n.getText("backup"), textColor);
        backupListButton.addActionListener(e -> {
            Platform.runLater(() -> {
                fileChooser.setTitle(I18n.getText("saveFile"));
                File output = fileChooser.showSaveDialog(null);
                if (output == null) return;
                JSONObject config = new JSONObject();
                f.putLocalMusicList(config);
                f.putCollectedItemList(config);
                new TipDialog(f, JsonUtil.toFile(config, output) ? I18n.getText("backupSuccess") : I18n.getText("backupFailed"), true).showDialog();
            });
        });
        restoreListButton = new DialogButton(I18n.getText("restore"), textColor);
        restoreListButton.addActionListener(e -> {
            Platform.runLater(() -> {
                fileChooser.setTitle(I18n.getText("chooseFile"));
                File input = fileChooser.showOpenDialog(null);
                if (input == null) return;
                JSONObject config = JsonUtil.read(input);
                f.loadLocalMusicList(config);
                f.loadCollectedMusicList(config);
                new TipDialog(f, I18n.getText("restoreSuccess"), true).showDialog();
            });
        });

        // 复选框图标
        Color iconColor = f.currUIStyle.getIconColor();
        ImageIcon icon = ImageUtil.dye(f.uncheckedIcon, iconColor);
        ImageIcon selectedIcon = ImageUtil.dye(f.checkedIcon, iconColor);
        autoUpdateCheckBox.setIcon(icon);
        autoUpdateCheckBox.setSelectedIcon(selectedIcon);
        videoOnlyCheckBox.setIcon(icon);
        videoOnlyCheckBox.setSelectedIcon(selectedIcon);
        showTabTextCheckBox.setIcon(icon);
        showTabTextCheckBox.setSelectedIcon(selectedIcon);
        autoDownloadLrcCheckBox.setIcon(icon);
        autoDownloadLrcCheckBox.setSelectedIcon(selectedIcon);
//        verbatimTimelineCheckBox.setIcon(icon);
//        verbatimTimelineCheckBox.setSelectedIcon(selectedIcon);
        enableKeyCheckBox.setIcon(icon);
        enableKeyCheckBox.setSelectedIcon(selectedIcon);

        autoUpdatePanel.add(autoUpdateCheckBox);

        videoOnlyPanel.add(videoOnlyCheckBox);

        showTabTextPanel.add(showTabTextCheckBox);

        for (String name : LyricAlignment.NAMES) lrcAlignmentComboBox.addItem(name);
        lrcAlignmentPanel.add(lrcAlignmentLabel);
        lrcAlignmentPanel.add(lrcAlignmentComboBox);

        specMaxHeightPanel.add(specMaxHeightLabel);
        specMaxHeightPanel.add(specMaxHeightTextField);

        for (String name : BlurConstants.GAUSSIAN_FACTOR_NAME) gsFactorComboBox.addItem(name);
        gsFactorPanel.add(gsFactorLabel);
        gsFactorPanel.add(gsFactorComboBox);

        for (String name : BlurConstants.DARKER_FACTOR_NAME) darkerFactorComboBox.addItem(name);
        darkerFactorPanel.add(darkerFactorLabel);
        darkerFactorPanel.add(darkerFactorComboBox);

        autoDownloadLrcPanel.add(autoDownloadLrcCheckBox);

//        verbatimTimelinePanel.add(verbatimTimelineCheckBox);

        musicDownPanel.add(musicDownLabel);
        musicDownPanel.add(musicDownPathTextField);
        musicDownPanel.add(changeMusicDownPathButton);
        musicDownPanel.add(openMusicDownPathButton);

        mvDownPanel.add(mvDownLabel);
        mvDownPanel.add(mvDownPathTextField);
        mvDownPanel.add(changeMvDownPathButton);
        mvDownPanel.add(openMvDownPathButton);

        cachePanel.add(cacheLabel);
        cachePanel.add(cachePathTextField);
        cachePanel.add(changeCachePathButton);
        cachePanel.add(openCachePathButton);

        maxCacheSizePanel.add(maxCacheSizeLabel);
        maxCacheSizePanel.add(maxCacheSizeTextField);

        maxHistoryCountPanel.add(maxHistoryCountLabel);
        maxHistoryCountPanel.add(maxHistoryCountTextField);

        maxSearchHistoryCountPanel.add(maxSearchHistoryCountLabel);
        maxSearchHistoryCountPanel.add(maxSearchHistoryCountTextField);

        maxConcurrentTaskCountPanel.add(maxConcurrentTaskCountLabel);
        maxConcurrentTaskCountPanel.add(maxConcurrentTaskCountTextField);

        for (String name : I18n.NAMES) langComboBox.addItem(name);
        langPanel.add(langLabel);
        langPanel.add(langComboBox);

        for (String name : CloseWindowOptions.NAMES) closeOptionComboBox.addItem(name);
        closeOptionPanel.add(closeOptionLabel);
        closeOptionPanel.add(closeOptionComboBox);

        for (String name : WindowSize.NAMES) windowSizeComboBox.addItem(name);
        windowSizePanel.add(windowSizeLabel);
        windowSizePanel.add(windowSizeComboBox);

        for (String name : AudioQuality.NAMES) audioQualityComboBox.addItem(name);
        audioQualityPanel.add(audioQualityLabel);
        audioQualityPanel.add(audioQualityComboBox);

        for (String name : VideoQuality.NAMES) videoQualityComboBox.addItem(name);
        videoQualityPanel.add(videoQualityLabel);
        videoQualityPanel.add(videoQualityComboBox);

        for (int i = 5; i <= 60; i += 5) {
            String item = i + I18n.getText("seconds");
            fobComboBox.addItem(item);
            if (f.forwardOrBackwardTime == i) fobComboBox.setSelectedItem(item);
        }
        fobPanel.add(fobLabel);
        fobPanel.add(fobComboBox);

        balanceComboBox.addItem(I18n.getText("leftChannel"));
        balanceComboBox.addItem(I18n.getText("stereo"));
        balanceComboBox.addItem(I18n.getText("rightChannel"));
        balancePanel.add(balanceLabel);
        balancePanel.add(balanceComboBox);

        backupPanel.add(backupLabel);
        backupPanel.add(backupListButton);
        backupPanel.add(restoreListButton);

        keyPanel.add(keyLabel);
        keyPanel.add(enableKeyCheckBox);
        playOrPausePanel.add(playOrPauseLabel);
        playOrPausePanel.add(playOrPauseTextField);
        playLastPanel.add(playLastLabel);
        playLastPanel.add(playLastTextField);
        playNextPanel.add(playNextLabel);
        playNextPanel.add(playNextTextField);
        backwardPanel.add(backwardLabel);
        backwardPanel.add(backwardTextField);
        forwardPanel.add(forwardLabel);
        forwardPanel.add(forwardTextField);
        videoFullScreenPanel.add(videoFullScreenLabel);
        videoFullScreenPanel.add(videoFullScreenTextField);

        int vGap = 10;
        generalContentBox.add(autoUpdatePanel);
        generalContentBox.add(Box.createVerticalStrut(vGap));
        generalContentBox.add(videoOnlyPanel);
        generalContentBox.add(Box.createVerticalStrut(vGap));
        generalContentBox.add(langPanel);
        generalContentBox.add(Box.createVerticalStrut(vGap));
        generalContentBox.add(closeOptionPanel);
        generalContentBox.add(Box.createVerticalStrut(vGap));
        generalContentBox.add(windowSizePanel);
        generalContentBox.add(Box.createVerticalGlue());

        appearanceContentBox.add(showTabTextPanel);
        appearanceContentBox.add(Box.createVerticalStrut(vGap));
        appearanceContentBox.add(lrcAlignmentPanel);
        appearanceContentBox.add(Box.createVerticalStrut(vGap));
        appearanceContentBox.add(specMaxHeightPanel);
        appearanceContentBox.add(Box.createVerticalStrut(vGap));
        appearanceContentBox.add(gsFactorPanel);
        appearanceContentBox.add(Box.createVerticalStrut(vGap));
        appearanceContentBox.add(darkerFactorPanel);
        appearanceContentBox.add(Box.createVerticalGlue());

        downloadAndCacheContentBox.add(autoDownloadLrcPanel);
        downloadAndCacheContentBox.add(Box.createVerticalStrut(vGap));
//        downloadAndCacheContentBox.add(verbatimTimelinePanel);
//        downloadAndCacheContentBox.add(Box.createVerticalStrut(vGap));
        downloadAndCacheContentBox.add(musicDownPanel);
        downloadAndCacheContentBox.add(Box.createVerticalStrut(vGap));
        downloadAndCacheContentBox.add(mvDownPanel);
        downloadAndCacheContentBox.add(Box.createVerticalStrut(vGap));
        downloadAndCacheContentBox.add(cachePanel);
        downloadAndCacheContentBox.add(Box.createVerticalStrut(vGap));
        downloadAndCacheContentBox.add(maxCacheSizePanel);
        downloadAndCacheContentBox.add(Box.createVerticalStrut(vGap));
        downloadAndCacheContentBox.add(maxConcurrentTaskCountPanel);
        downloadAndCacheContentBox.add(Box.createVerticalGlue());

        playbackContentBox.add(audioQualityPanel);
        playbackContentBox.add(Box.createVerticalStrut(vGap));
        playbackContentBox.add(videoQualityPanel);
        playbackContentBox.add(Box.createVerticalStrut(vGap));
        playbackContentBox.add(fobPanel);
        playbackContentBox.add(Box.createVerticalStrut(vGap));
        playbackContentBox.add(balancePanel);
        playbackContentBox.add(Box.createVerticalStrut(vGap));
        playbackContentBox.add(maxHistoryCountPanel);
        playbackContentBox.add(Box.createVerticalStrut(vGap));
        playbackContentBox.add(maxSearchHistoryCountPanel);
        playbackContentBox.add(Box.createVerticalStrut(vGap));
        playbackContentBox.add(backupPanel);
        playbackContentBox.add(Box.createVerticalGlue());

        hotKeyContentBox.add(keyPanel);
        hotKeyContentBox.add(Box.createVerticalStrut(vGap));
        hotKeyContentBox.add(playOrPausePanel);
        hotKeyContentBox.add(Box.createVerticalStrut(vGap));
        hotKeyContentBox.add(playLastPanel);
        hotKeyContentBox.add(Box.createVerticalStrut(vGap));
        hotKeyContentBox.add(playNextPanel);
        hotKeyContentBox.add(Box.createVerticalStrut(vGap));
        hotKeyContentBox.add(backwardPanel);
        hotKeyContentBox.add(Box.createVerticalStrut(vGap));
        hotKeyContentBox.add(forwardPanel);
        hotKeyContentBox.add(Box.createVerticalStrut(vGap));
        hotKeyContentBox.add(videoFullScreenPanel);
        hotKeyContentBox.add(Box.createVerticalGlue());

        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        globalPanel.add(centerPanel, BorderLayout.CENTER);
    }

    // 加载设置
    private void initSettings() {
        autoUpdateCheckBox.setSelected(f.autoUpdate);
        videoOnlyCheckBox.setSelected(f.videoOnly);
        showTabTextCheckBox.setSelected(f.showTabText);
        lrcAlignmentComboBox.setSelectedIndex(LyricAlignment.lrcAlignmentIndex);
        specMaxHeightTextField.setText(String.valueOf(SpectrumConstants.barMaxHeight));
        gsFactorComboBox.setSelectedIndex(BlurConstants.gsFactorIndex);
        darkerFactorComboBox.setSelectedIndex(BlurConstants.darkerFactorIndex);
        autoDownloadLrcCheckBox.setSelected(f.isAutoDownloadLrc);
//        verbatimTimelineCheckBox.setSelected(LyricType.verbatimTimeline);
        musicDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MUSIC_PATH).getAbsolutePath());
        mvDownPathTextField.setText(new File(SimplePath.DOWNLOAD_MV_PATH).getAbsolutePath());
        cachePathTextField.setText(new File(SimplePath.CACHE_PATH).getAbsolutePath());
        maxCacheSizeTextField.setText(String.valueOf(f.maxCacheSize));
        maxHistoryCountTextField.setText(String.valueOf(f.maxHistoryCount));
        maxSearchHistoryCountTextField.setText(String.valueOf(f.maxSearchHistoryCount));
        maxConcurrentTaskCountTextField.setText(String.valueOf(((ThreadPoolExecutor) GlobalExecutors.downloadExecutor).getCorePoolSize()));
        langComboBox.setSelectedIndex(I18n.currLang);
        closeOptionComboBox.setSelectedIndex(f.currCloseWindowOption);
        windowSizeComboBox.setSelectedIndex(f.windowSize);
        audioQualityComboBox.setSelectedIndex(AudioQuality.quality);
        videoQualityComboBox.setSelectedIndex(VideoQuality.quality);
        balanceComboBox.setSelectedIndex(Double.valueOf(f.currBalance).intValue() + 1);

        enableKeyCheckBox.setSelected(f.keyEnabled);
        playOrPauseKeys.addAll(f.playOrPauseKeys);
        playOrPauseTextField.setText(KeyUtil.join(f.playOrPauseKeys));
        playLastKeys.addAll(f.playLastKeys);
        playLastTextField.setText(KeyUtil.join(f.playLastKeys));
        playNextKeys.addAll(f.playNextKeys);
        playNextTextField.setText(KeyUtil.join(f.playNextKeys));
        backwardKeys.addAll(f.backwardKeys);
        backwardTextField.setText(KeyUtil.join(f.backwardKeys));
        forwardKeys.addAll(f.forwardKeys);
        forwardTextField.setText(KeyUtil.join(f.forwardKeys));
        videoFullScreenKeys.addAll(f.videoFullScreenKeys);
        videoFullScreenTextField.setText(KeyUtil.join(f.videoFullScreenKeys));
    }

    // 应用设置
    private boolean applySettings() {
        // 验证
        File musicDir = new File(musicDownPathTextField.getText());
        if (!musicDir.exists()) {
            new TipDialog(f, I18n.getText("invalidMusicDown"), true).showDialog();
            return false;
        }
        File mvDir = new File(mvDownPathTextField.getText());
        if (!mvDir.exists()) {
            new TipDialog(f, I18n.getText("invalidMvDown"), true).showDialog();
            return false;
        }
        File cacheDir = new File(cachePathTextField.getText());
        if (!cacheDir.exists()) {
            new TipDialog(f, I18n.getText("invalidCache"), true).showDialog();
            return false;
        }

        f.autoUpdate = autoUpdateCheckBox.isSelected();
        f.videoOnly = videoOnlyCheckBox.isSelected();

        f.showTabText = showTabTextCheckBox.isSelected();
        f.updateTabSize();

        LyricAlignment.lrcAlignmentIndex = lrcAlignmentComboBox.getSelectedIndex();

        int gsFactorIndex = BlurConstants.gsFactorIndex;
        BlurConstants.gsFactorIndex = gsFactorComboBox.getSelectedIndex();

        int darkerFactorIndex = BlurConstants.darkerFactorIndex;
        BlurConstants.darkerFactorIndex = darkerFactorComboBox.getSelectedIndex();

        if (gsFactorIndex != BlurConstants.gsFactorIndex || darkerFactorIndex != BlurConstants.darkerFactorIndex)
            f.doBlur();

        f.isAutoDownloadLrc = autoDownloadLrcCheckBox.isSelected();

//        LyricType.verbatimTimeline = verbatimTimelineCheckBox.isSelected();

        SimplePath.DOWNLOAD_MUSIC_PATH = musicDir.getAbsolutePath() + File.separator;
        SimplePath.DOWNLOAD_MV_PATH = mvDir.getAbsolutePath() + File.separator;
        SimplePath.CACHE_PATH = cacheDir.getAbsolutePath() + File.separator;
        SimplePath.IMG_CACHE_PATH = SimplePath.CACHE_PATH + File.separator + "img" + File.separator;
        // 更改缓存图像路径并创建
        FileUtil.mkDir(SimplePath.IMG_CACHE_PATH);

        String text = specMaxHeightTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, I18n.getText("invalidSpecMaxHeight"), true).showDialog();
            return false;
        }
        SpectrumConstants.barMaxHeight = Integer.parseInt(text);
        f.spectrumPanel.setPreferredSize(new Dimension(f.spectrumPanel.getPreferredSize().width, SpectrumConstants.barMaxHeight));
        f.spectrumPanel.setMinimumSize(new Dimension(1, SpectrumConstants.barMaxHeight));
        f.spectrumPanel.setVisible(false);
        f.spectrumPanel.setVisible(true);

        text = maxCacheSizeTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, I18n.getText("invalidMaxCacheSize"), true).showDialog();
            return false;
        }
        f.maxCacheSize = Long.parseLong(text);

        text = maxHistoryCountTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, I18n.getText("invalidHistoryCount"), true).showDialog();
            return false;
        }
        f.maxHistoryCount = Integer.parseInt(text);

        text = maxSearchHistoryCountTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, I18n.getText("invalidSearchHistoryCount"), true).showDialog();
            return false;
        }
        f.maxSearchHistoryCount = Integer.parseInt(text);

        text = maxConcurrentTaskCountTextField.getText();
        if (StringUtil.isEmpty(text)) {
            new TipDialog(f, I18n.getText("invalidConcurrentTaskCount"), true).showDialog();
            return false;
        }
        GlobalExecutors.downloadExecutor = Executors.newFixedThreadPool(Integer.parseInt(text));

        // 删除多余的播放历史记录
        for (int i = f.maxHistoryCount, s = f.historyModel.size(); i < s; i++)
            f.historyModel.remove(f.maxHistoryCount);

        // 删除多余的搜索历史记录
        CustomPanel[] ips = new CustomPanel[]{f.netMusicHistorySearchInnerPanel2,
                f.netPlaylistHistorySearchInnerPanel2,
                f.netAlbumHistorySearchInnerPanel2,
                f.netArtistHistorySearchInnerPanel2,
                f.netRadioHistorySearchInnerPanel2,
                f.netMvHistorySearchInnerPanel2,
                f.netUserHistorySearchInnerPanel2};
        for (int i = 0, len = ips.length; i < len; i++) {
            CustomPanel ip = ips[i];
            for (int j = f.maxSearchHistoryCount, c = ip.getComponentCount(); j < c; j++) {
                ip.remove(f.maxSearchHistoryCount);
            }
        }

        I18n.currLang = langComboBox.getSelectedIndex();
        f.currCloseWindowOption = closeOptionComboBox.getSelectedIndex();
        f.windowSize = windowSizeComboBox.getSelectedIndex();
        f.windowWidth = WindowSize.DIMENSIONS[f.windowSize][0];
        f.windowHeight = WindowSize.DIMENSIONS[f.windowSize][1];
        f.x = f.y = 0x3f3f3f3f;
        if (f.windowState != WindowState.MAXIMIZED) f.setSize(f.windowWidth, f.windowHeight);

        AudioQuality.quality = audioQualityComboBox.getSelectedIndex();
        VideoQuality.quality = videoQualityComboBox.getSelectedIndex();
        f.forwardOrBackwardTime = Integer.parseInt(((String) fobComboBox.getSelectedItem()).replace(I18n.getText("seconds"), ""));
        f.currBalance = balanceComboBox.getSelectedIndex() - 1;
        f.player.setBalance(f.currBalance);

        f.keyEnabled = enableKeyCheckBox.isSelected();
        f.playOrPauseKeys.clear();
        f.playOrPauseKeys.addAll(playOrPauseKeys);
        f.playLastKeys.clear();
        f.playLastKeys.addAll(playLastKeys);
        f.playNextKeys.clear();
        f.playNextKeys.addAll(playNextKeys);
        f.backwardKeys.clear();
        f.backwardKeys.addAll(backwardKeys);
        f.forwardKeys.clear();
        f.forwardKeys.addAll(forwardKeys);
        f.videoFullScreenKeys.clear();
        f.videoFullScreenKeys.addAll(videoFullScreenKeys);

        return true;
    }
}
