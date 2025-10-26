package com.carrental.gui;

import com.carrental.entity.Staff;
import com.carrental.entity.User;
import com.carrental.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 登录界面
 */
public class LoginFrame extends JFrame {
    private JRadioButton staffRadio;
    private JRadioButton userRadio;
    private ButtonGroup loginTypeGroup;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JButton registerButton;
    private UserService userService;

    public LoginFrame() {
        this.userService = new UserService();
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
        loginButton.addActionListener(e -> performLogin());
        cancelButton.addActionListener(e -> System.exit(0));
        registerButton.addActionListener(e -> new UserRegisterDialog(this, userService).setVisible(true));
        passwordField.addActionListener(e -> performLogin());
    }

    private void setupFrame() {
        setTitle("汽车出租管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    private void performLogin() {
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