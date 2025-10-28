package com.carrental.gui;

import com.carrental.entity.Staff;
import com.carrental.entity.User;
import com.carrental.service.UserService;
import com.carrental.util.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.concurrent.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录界面
 */
public class LoginFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JRadioButton staffRadio;
    private JRadioButton userRadio;
    private ButtonGroup loginTypeGroup;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton registerButton;
    private UserService userService;
    private final ExecutorService executorService;

    public LoginFrame() {
        this.userService = new UserService();
        this.executorService = Executors.newSingleThreadExecutor();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }

    private void initializeComponents() {
        staffRadio = new JRadioButton("员工登录", true);
        userRadio = new JRadioButton("用户登录");
        loginTypeGroup = new ButtonGroup();
        loginTypeGroup.add(staffRadio);
        loginTypeGroup.add(userRadio);

        usernameField = new JTextField(25);
        passwordField = new JPasswordField(25);

        loginButton = new JButton("登录");
        cancelButton = new JButton("取消");
        registerButton = new JButton("用户注册");

        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        usernameField.setFont(font);
        passwordField.setFont(font);
        loginButton.setFont(font);
        cancelButton.setFont(font);
        registerButton.setFont(font);
        staffRadio.setFont(font);
        userRadio.setFont(font);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        // 标题
        JLabel titleLabel = new JLabel("汽车出租管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(0,0,20,0);
        mainPanel.add(titleLabel, gbc);

        // 登录类型
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        typePanel.add(staffRadio);
        typePanel.add(userRadio);
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        mainPanel.add(typePanel, gbc);

        // 用户名/手机号
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; // 不填充
        mainPanel.add(new JLabel("用户名/手机号:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0; // 让输入框占用剩余空间
        gbc.fill = GridBagConstraints.HORIZONTAL; // 让输入框横向填充
        mainPanel.add(usernameField, gbc);

        // 密码/身份证
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE; // 不填充
        mainPanel.add(new JLabel("密码/身份证:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL; // 让输入框横向填充
        mainPanel.add(passwordField, gbc);

        // 按钮
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15,0,0,0);
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventHandlers() {
        loginButton.addActionListener(e -> {
            try {
                performLogin();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        cancelButton.addActionListener(e -> System.exit(0));
        registerButton.addActionListener(e -> new UserRegisterDialog(this, userService).setVisible(true));
        passwordField.addActionListener(e -> {
            try {
                performLogin();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
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
                        try {
                            new MainFrame(staff).setVisible(true);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        // 检查是否是重复登录或其他错误
                        try {
                            // 由于UserService中已经处理了重复登录检查，这里显示相应的错误信息
                            showMessage("登录失败！可能原因：\n1. 用户名或密码错误\n2. 该用户已登录，无法重复登录",
                                    "登录失败", JOptionPane.ERROR_MESSAGE);
                        } catch (Exception e) {
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


    private void setupFrame() {
        setTitle("汽车出租管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }
    @Override
    public void dispose() {
        super.dispose();
        // 关闭线程池
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    private void performLogin() throws IOException {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, staffRadio.isSelected() ? "请输入用户名" : "请输入手机号", "提示", JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, staffRadio.isSelected() ? "请输入密码" : "请输入身份证号", "提示", JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return;
        }

        if (staffRadio.isSelected()) {
            Staff staff = userService.login(username, password);
            if (staff != null) {
                JOptionPane.showMessageDialog(this, "登录成功！\n欢迎，" + staff.getName() + "！", "登录成功", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainFrame(staff).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        } else {
            User user = userService.userLogin(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(this, "登录成功！\n欢迎，" + user.getName() + "！", "登录成功", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new MainFrame(user).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "手机号或身份证号错误！", "登录失败", JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}