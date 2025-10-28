package com.carrental.gui;

import com.carrental.entity.User;
import com.carrental.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class UserRegisterDialog extends JDialog {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField identityField;
    private JButton registerButton;
    private JButton cancelButton;
    private final UserService userService;

    public UserRegisterDialog(JFrame parent, UserService userService) {
        super(parent, "用户注册", true);
        this.userService = userService;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setSize(350, 250);
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        nameField = new JTextField(15);
        phoneField = new JTextField(15);
        identityField = new JTextField(15);
        registerButton = new JButton("注册");
        cancelButton = new JButton("取消");
    }

    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);

        gbc.gridx=0; gbc.gridy=0; add(new JLabel("姓名:"), gbc);
        gbc.gridx=1; add(nameField, gbc);

        gbc.gridx=0; gbc.gridy=1; add(new JLabel("手机号:"), gbc);
        gbc.gridx=1; add(phoneField, gbc);

        gbc.gridx=0; gbc.gridy=2; add(new JLabel("身份证号:"), gbc);
        gbc.gridx=1; add(identityField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2;
        add(buttonPanel, gbc);
    }

    private void setupEventHandlers() {
        registerButton.addActionListener(e -> performRegister());
        cancelButton.addActionListener(e -> dispose());
    }

    private void performRegister() {
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String identity = identityField.getText().trim();

        if(name.isEmpty() || phone.isEmpty() || identity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写完整信息", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = new User();
        user.setName(name);
        user.setPhone(phone);
        user.setIdentityId(identity);
        user.setRegisterDate(LocalDate.now());
        user.setMember("普通");
        user.setJudge("良好");

        if(userService.addUser(user)) {
            JOptionPane.showMessageDialog(this, "注册成功！请登录", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "注册失败，手机号或身份证号已存在", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}