package com.carrental.gui;

import com.carrental.entity.Staff;
import com.carrental.service.UserService;
import com.carrental.util.DatabaseConnection;
import com.carrental.util.AppLogger;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录界面
 * 提供员工登录功能
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    
    // 常量定义
    private static final String APP_TITLE = "汽车出租管理系统";
    private static final String LOGIN_TITLE = APP_TITLE + " - 登录";
    private static final int FRAME_WIDTH = 350;
    private static final int FRAME_HEIGHT = 250;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 30;
    private static final int FIELD_WIDTH = 15;
    private static final int INPUT_INSETS = 5;
    private static final int TITLE_BOTTOM_INSETS = 20;
    private static final int BUTTON_TOP_INSETS = 15;
    private static final String FONT_NAME = "微软雅黑";
    private static final int FONT_SIZE = 12;
    private static final int TITLE_FONT_SIZE = 18;
    
    // UI组件 - 使用普通字段而不是final，因为需要在构造函数中初始化
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private UserService userService;
    private final ExecutorService executorService;

    public LoginFrame() {
        // 移除构造函数中的数据库连接检查
        this.executorService = Executors.newSingleThreadExecutor();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
        usernameField = new JTextField(FIELD_WIDTH);
        passwordField = new JPasswordField(FIELD_WIDTH);
        loginButton = new JButton("登录");
        cancelButton = new JButton("取消");
        
        // 设置按钮样式
        Dimension buttonSize = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        loginButton.setPreferredSize(buttonSize);
        cancelButton.setPreferredSize(buttonSize);
        
        // 统一设置字体
        Font font = new Font(FONT_NAME, Font.PLAIN, FONT_SIZE);
        setComponentFont(font, usernameField, passwordField, loginButton, cancelButton);
    }

    /**
     * 为组件设置字体
     */
    private void setComponentFont(Font font, JComponent... components) {
        for (JComponent component : components) {
            component.setFont(font);
        }
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // 创建主面板
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * 创建主面板
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(INPUT_INSETS, INPUT_INSETS, INPUT_INSETS, INPUT_INSETS);
        
        // 添加标题
        addTitleLabel(mainPanel, gbc);
        
        // 添加用户名输入
        addUsernameField(mainPanel, gbc);
        
        // 添加密码输入
        addPasswordField(mainPanel, gbc);
        
        // 添加按钮面板
        addButtonPanel(mainPanel, gbc);
        
        return mainPanel;
    }

    /**
     * 添加标题标签
     */
    private void addTitleLabel(JPanel panel, GridBagConstraints gbc) {
        JLabel titleLabel = new JLabel(APP_TITLE);
        titleLabel.setFont(new Font(FONT_NAME, Font.BOLD, TITLE_FONT_SIZE));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, TITLE_BOTTOM_INSETS, 0);
        panel.add(titleLabel, gbc);
    }

    /**
     * 添加用户名输入字段
     */
    private void addUsernameField(JPanel panel, GridBagConstraints gbc) {
        // 用户名标签
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(INPUT_INSETS, INPUT_INSETS, INPUT_INSETS, INPUT_INSETS);
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(usernameLabel, gbc);
        
        // 用户名输入框
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);
    }

    /**
     * 添加密码输入字段
     */
    private void addPasswordField(JPanel panel, GridBagConstraints gbc) {
        // 密码标签
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font(FONT_NAME, Font.PLAIN, FONT_SIZE));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);
        
        // 密码输入框
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);
    }

    /**
     * 添加按钮面板
     */
    private void addButtonPanel(JPanel panel, GridBagConstraints gbc) {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(BUTTON_TOP_INSETS, 0, 0, 0);
        panel.add(buttonPanel, gbc);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> handleLogin());
        cancelButton.addActionListener(e -> System.exit(0));
        passwordField.addActionListener(e -> handleLogin());
    }

    /**
     * 处理登录事件
     */
    private void handleLogin() {
        if (validateInput()) {
            performLoginAsync();
        }
    }

    /**
     * 验证输入数据
     */
    private boolean validateInput() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        
        if (username.isEmpty()) {
            showMessage("请输入用户名", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            showMessage("请输入密码", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * 异步执行登录操作
     */
    private void performLoginAsync() {
        // 禁用登录按钮防止重复点击
        loginButton.setEnabled(false);
        
        // 创建一个Future来处理登录任务
        Future<?> loginFuture = executorService.submit(() -> {
            try {
                // 尝试获取数据库连接实例，这会触发数据库连接
                DatabaseConnection dbConnection = DatabaseConnection.getInstance();
                
                // 检查数据库连接是否失败
                if (dbConnection.isConnectionFailed()) {
                    throw new RuntimeException("数据库连接失败");
                }
                
                // 创建UserService
                this.userService = new UserService();
                
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                
                // 执行登录操作
                Staff staff = userService.login(username, password);
                
                // 在EDT中处理结果
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                    if (staff != null) {
                        showMessage("登录成功！\n欢迎，" + staff.getName() + "！", 
                                   "登录成功", JOptionPane.INFORMATION_MESSAGE);
                        
                        // 打开主界面
                        dispose();
                        new MainFrame(staff).setVisible(true);
                    } else {
                        // 如果上一次登录被锁阻塞，显示更明确的提示
                        if (userService != null && userService.wasLastLoginBlocked()) {
                            showMessage("当前用户已在另一台设备登录", "登录失败", JOptionPane.WARNING_MESSAGE);
                        } else {
                            // 通用失败提示（用户名/密码错误 或 其他原因）
                            showMessage("登录失败！可能原因：\n1. 用户名或密码错误\n2. 该用户已登录，无法重复登录", 
                                       "登录失败", JOptionPane.ERROR_MESSAGE);
                        }
                        passwordField.setText("");
                        passwordField.requestFocus();
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                    // 检查是否是数据库连接错误
                    if (e instanceof RuntimeException && 
                        e.getMessage().contains("数据库连接失败") || 
                        e.getMessage().contains("连接超时")) {
                        JOptionPane.showMessageDialog(this, 
                            "数据库连接出错", 
                            "错误", 
                            JOptionPane.ERROR_MESSAGE);
                    } else {
                        showMessage("登录过程中发生错误：" + e.getMessage(), 
                                   "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });
        
        // 启动一个定时器来检查是否超时
        Timer timeoutTimer = new Timer();
        timeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!loginFuture.isDone()) {
                    // 如果登录任务还在执行，取消它并显示错误
                    loginFuture.cancel(true);
                    SwingUtilities.invokeLater(() -> {
                        loginButton.setEnabled(true);
                        JOptionPane.showMessageDialog(LoginFrame.this, 
                            "数据库连接超时", 
                            "错误", 
                            JOptionPane.ERROR_MESSAGE);
                    });
                }
            }
        }, 5000); // 5秒超时
    }

    /**
     * 显示消息对话框
     */
    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    /**
     * 设置窗口属性
     */
    private void setupFrame() {
        setTitle(LOGIN_TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        
        // 设置图标
        setIcon();
    }

    /**
     * 设置窗口图标
     */
    private void setIcon() {
        try {
            Image icon = Toolkit.getDefaultToolkit()
                               .getImage(getClass().getResource("/icon.png"));
            if (icon != null) {
                setIconImage(icon);
            }
        } catch (Exception e) {
            AppLogger.logException("无法加载图标文件: " + e.getMessage(), e);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        // 关闭线程池
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * 主方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 设置系统外观
        setSystemLookAndFeel();
        
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    /**
     * 设置系统外观
     */
    private static void setSystemLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            AppLogger.logException("无法设置系统外观: " + e.getMessage(), e);
        }
    }
}