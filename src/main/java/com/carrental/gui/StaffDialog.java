package com.carrental.gui;

import com.carrental.entity.Staff;
import com.carrental.service.UserService;

import javax.swing.*;
import java.awt.*;

/**
 * 员工添加/编辑对话框
 */
public class StaffDialog extends JDialog {
    private Staff staff;
    private UserService userService;
    private JTextField nameField, phoneField, roleField;
    private DatePicker entryDatePicker;
    private JComboBox<String> positionComboBox;
    private JButton saveButton, cancelButton;
    private StaffManagementPanel parentPanel;

    public StaffDialog(StaffManagementPanel parent, Staff staff) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), staff == null ? "添加员工" : "修改员工", true);
        this.parentPanel = parent;
        this.staff = staff;
        this.userService = new UserService();

        initComponents();
        setupLayout();
        setupEvents();
        setupDialog();

        if (staff != null) loadStaffData();
    }

    private void initComponents() {
        Font font = new Font(Font.DIALOG, Font.PLAIN, 12);
        nameField = new JTextField();
        phoneField = new JTextField();
        roleField = new JTextField();
        entryDatePicker = new DatePicker();
        positionComboBox = new JComboBox<>(new String[]{"董事长", "经理", "员工"});

        nameField.setFont(font);
        phoneField.setFont(font);
        roleField.setFont(font);
        positionComboBox.setFont(font);

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

        String[] labels = {"姓名:", "电话:", "入职日期:", "职位:", "权限等级:"};
        Component[] comps = {nameField, phoneField, entryDatePicker, positionComboBox, roleField};

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
        saveButton.addActionListener(e -> saveStaff());
        cancelButton.addActionListener(e -> dispose());
    }

    private void setupDialog() {
        setSize(420, 320);
        setResizable(false);
        setLocationRelativeTo(getParent());
    }

    private void loadStaffData() {
        nameField.setText(staff.getName());
        phoneField.setText(staff.getPhone());
        if (staff.getEntryDate() != null) entryDatePicker.setSelectedDate(staff.getEntryDate());
        if (staff.getPosition() != null) positionComboBox.setSelectedItem(staff.getPosition());
        roleField.setText(String.valueOf(staff.getRole()));
    }

    private void saveStaff() {
        if (nameField.getText().trim().isEmpty()) { showError("请输入姓名"); return; }
        if (phoneField.getText().trim().isEmpty()) { showError("请输入电话"); return; }
        if (!entryDatePicker.isValidDate()) { showError("请选择入职日期"); return; }
        int role = 2;
        try { role = Integer.parseInt(roleField.getText().trim()); } catch (Exception ignored) {}

        try {
            Staff s = staff == null ? new Staff() : staff;
            s.setName(nameField.getText().trim());
            s.setPhone(phoneField.getText().trim());
            s.setEntryDate(entryDatePicker.getSelectedDate());
            s.setPosition((String) positionComboBox.getSelectedItem());
            s.setRole(role);

            boolean success = (staff == null) ? userService.addStaff(s) : userService.updateStaff(s);
            if (success) {
                JOptionPane.showMessageDialog(this, "保存成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                parentPanel.refreshData();
                dispose();
            } else showError("保存失败，请检查输入或数据库连接");
        } catch (Exception ex) { showError("保存失败: " + ex.getMessage()); }
    }

    private void showError(String msg) { JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE); }
}
