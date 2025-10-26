package com.carrental.gui;

import com.carrental.entity.User;
import com.carrental.service.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * 用户添加/编辑对话框
 */
public class UserDialog extends JDialog {
    private User user;
    private UserService userService;
    private JTextField nameField, identityField, phoneField;
    private DatePicker registerDatePicker;
    private JComboBox<String> memberComboBox;
    private JComboBox<String> judgeComboBox;
    private JButton saveButton, cancelButton;
    private UserManagementPanel parentPanel;

    public UserDialog(UserManagementPanel parent, User user) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), user == null ? "添加用户" : "修改用户", true);
        this.parentPanel = parent;
        this.user = user;
        this.userService = new UserService();

        initComponents();
        setupLayout();
        setupEvents();
        setupDialog();

        if (user != null) loadUserData();
    }

    private void initComponents() {
        Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
        nameField = new JTextField();
        identityField = new JTextField();
        phoneField = new JTextField();
        registerDatePicker = new DatePicker();
        memberComboBox = new JComboBox<>(new String[]{"是", "非"});
        judgeComboBox = new JComboBox<>(new String[]{"非常低", "低", "中", "高", "非常高"});

        nameField.setFont(font);
        identityField.setFont(font);
        phoneField.setFont(font);
        memberComboBox.setFont(font);
        judgeComboBox.setFont(font);

        saveButton = new JButton("保存");
        cancelButton = new JButton("取消");
        saveButton.setFont(font);
        cancelButton.setFont(font);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"姓名:", "身份证号:", "电话:", "注册日期:", "会员:", "信誉:"};
        Component[] comps = {nameField, identityField, phoneField, registerDatePicker, memberComboBox, judgeComboBox};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
            main.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            main.add(comps[i], gbc);
        }

        add(main, BorderLayout.CENTER);

        JPanel btn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btn.add(saveButton);
        btn.add(cancelButton);
        add(btn, BorderLayout.SOUTH);
    }

    private void setupEvents() {
        saveButton.addActionListener(e -> saveUser());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupDialog() {
        setSize(420, 340);
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    private void loadUserData() {
        nameField.setText(user.getName());
        identityField.setText(user.getIdentityId());
        phoneField.setText(user.getPhone());
        if (user.getRegisterDate() != null) registerDatePicker.setSelectedDate(user.getRegisterDate());
        if (user.getMember() != null) memberComboBox.setSelectedItem(user.getMember());
        if (user.getJudge() != null) judgeComboBox.setSelectedItem(user.getJudge());
    }

    private void saveUser() {
        // 验证
        if (nameField.getText().trim().isEmpty()) { showError("请输入姓名"); return; }
        if (identityField.getText().trim().isEmpty()) { showError("请输入身份证号"); return; }
        if (identityField.getText().trim().length() != 18) { showError("身份证号需为18位"); return; }
        if (phoneField.getText().trim().isEmpty()) { showError("请输入联系电话"); return; }
        if (!registerDatePicker.isValidDate()) { showError("请选择注册日期"); return; }

        try {
            User u = user == null ? new User() : user;
            u.setName(nameField.getText().trim());
            u.setIdentityId(identityField.getText().trim());
            u.setPhone(phoneField.getText().trim());
            u.setRegisterDate(registerDatePicker.getSelectedDate());
            u.setMember((String) memberComboBox.getSelectedItem());
            u.setJudge((String) judgeComboBox.getSelectedItem());

            boolean success = (user == null) ? userService.addUser(u) : userService.updateUser(u);
            if (success) {
                JOptionPane.showMessageDialog(this, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                parentPanel.refreshData();
                dispose();
            } else showError("保存失败，请检查输入或数据库连接");
        } catch (Exception ex) {
            showError("保存失败: " + ex.getMessage());
        }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE); }
}
