package com.carrental.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.carrental.entity.Staff;
import com.carrental.service.UserService;

/**
 * 系统设置面板
 * 提供系统设置相关功能
 */
public class SystemSettingsPanel extends JPanel {
    private Properties props;
    private File propsFile;

    // DB fields
    private JTextField dbUrlField;
    private JTextField dbUserField;
    private JPasswordField dbPasswordField;
    private JTextField dbDriverField;

    // system fields
    private JTextField systemNameField;
    private JTextField systemVersionField;
    private JTextField systemAuthorField;

    // business fields
    private JTextField rentMaxDaysField;
    private JTextField rentMinDaysField;
    private JTextField fineDailyRateField;

    // admin password change
    private JTextField adminNameField;
    private JPasswordField adminNewPasswordField;

    private UserService userService;

    public SystemSettingsPanel() {
        this.userService = new UserService();
        props = new Properties();
        propsFile = new File("config.properties");
        initializeComponents();
        setupLayout();
        loadProperties();
    }

    private void initializeComponents() {
        dbUrlField = new JTextField();
        dbUserField = new JTextField();
        dbPasswordField = new JPasswordField();
        dbDriverField = new JTextField();

        systemNameField = new JTextField();
        systemVersionField = new JTextField();
        systemAuthorField = new JTextField();

        rentMaxDaysField = new JTextField();
        rentMinDaysField = new JTextField();
        fineDailyRateField = new JTextField();

        adminNameField = new JTextField();
        adminNewPasswordField = new JPasswordField();
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6,6,6,6);
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        // DB section
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1; form.add(new JLabel("数据库 URL:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0; form.add(dbUrlField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0; form.add(new JLabel("DB 用户:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(dbUserField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("DB 密码:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(dbPasswordField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("DB 驱动:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(dbDriverField, gbc);
        row++;

        // System section
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("系统名称:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(systemNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("版本:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(systemVersionField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("作者:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(systemAuthorField, gbc);
        row++;

        // Business section
        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("最大租期(天):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(rentMaxDaysField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("最小租期(天):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(rentMinDaysField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.fill = GridBagConstraints.NONE; form.add(new JLabel("违约每天费率:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; form.add(fineDailyRateField, gbc);
        row++;

        main.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton testDbBtn = new JButton("测试数据库连接");
        JButton saveBtn = new JButton("保存配置");
        JButton reloadBtn = new JButton("重新加载配置");
        btnPanel.add(testDbBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(reloadBtn);

        testDbBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { testDatabaseConnection(); }
        });

        saveBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { saveProperties(); }
        });

        reloadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { loadProperties(); }
        });

        main.add(btnPanel, BorderLayout.SOUTH);

        // Admin password section
        JPanel adminPanel = new JPanel(new GridBagLayout());
        adminPanel.setBorder(BorderFactory.createTitledBorder("管理员密码修改"));
        GridBagConstraints ag = new GridBagConstraints();
        ag.insets = new Insets(5,5,5,5);
        ag.anchor = GridBagConstraints.WEST;
        ag.gridx = 0; ag.gridy = 0; adminPanel.add(new JLabel("员工姓名:"), ag);
        ag.gridx = 1; ag.fill = GridBagConstraints.HORIZONTAL; ag.weightx = 1.0; adminPanel.add(adminNameField, ag);
        ag.gridx = 0; ag.gridy = 1; ag.fill = GridBagConstraints.NONE; adminPanel.add(new JLabel("新密码:"), ag);
        ag.gridx = 1; ag.fill = GridBagConstraints.HORIZONTAL; adminPanel.add(adminNewPasswordField, ag);
        JButton changePwdBtn = new JButton("修改密码");
        ag.gridx = 1; ag.gridy = 2; ag.fill = GridBagConstraints.NONE; adminPanel.add(changePwdBtn, ag);

        changePwdBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { changeAdminPassword(); }
        });

        JPanel right = new JPanel(new BorderLayout());
        right.add(adminPanel, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, main, right);
        split.setResizeWeight(0.7);
        add(split, BorderLayout.CENTER);
    }

    private void loadProperties() {
        if (!propsFile.exists()) return;
        try (FileInputStream fis = new FileInputStream(propsFile)) {
            props.load(fis);
            dbUrlField.setText(props.getProperty("database.url", ""));
            dbUserField.setText(props.getProperty("database.username", ""));
            dbPasswordField.setText(props.getProperty("database.password", ""));
            dbDriverField.setText(props.getProperty("database.driver", ""));

            systemNameField.setText(props.getProperty("system.name", ""));
            systemVersionField.setText(props.getProperty("system.version", ""));
            systemAuthorField.setText(props.getProperty("system.author", ""));

            rentMaxDaysField.setText(props.getProperty("rent.maxdays", ""));
            rentMinDaysField.setText(props.getProperty("rent.mindays", ""));
            fineDailyRateField.setText(props.getProperty("fine.dailyrate", ""));

            repaint();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "加载配置失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProperties() {
        props.setProperty("database.url", dbUrlField.getText().trim());
        props.setProperty("database.username", dbUserField.getText().trim());
        props.setProperty("database.password", new String(dbPasswordField.getPassword()));
        props.setProperty("database.driver", dbDriverField.getText().trim());

        props.setProperty("system.name", systemNameField.getText().trim());
        props.setProperty("system.version", systemVersionField.getText().trim());
        props.setProperty("system.author", systemAuthorField.getText().trim());

        props.setProperty("rent.maxdays", rentMaxDaysField.getText().trim());
        props.setProperty("rent.mindays", rentMinDaysField.getText().trim());
        props.setProperty("fine.dailyrate", fineDailyRateField.getText().trim());

        try (FileOutputStream fos = new FileOutputStream(propsFile)) {
            props.store(fos, "Updated by SystemSettingsPanel");
            JOptionPane.showMessageDialog(this, "配置已保存（部分更改可能需要重启应用生效）", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "保存配置失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testDatabaseConnection() {
        String url = dbUrlField.getText().trim();
        String user = dbUserField.getText().trim();
        String pass = new String(dbPasswordField.getPassword());
        String driver = dbDriverField.getText().trim();

        try {
            if (!driver.isEmpty()) Class.forName(driver);
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                if (conn != null && !conn.isClosed()) {
                    JOptionPane.showMessageDialog(this, "数据库连接成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "无法建立连接", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "数据库驱动类未找到: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "连接数据库失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void changeAdminPassword() {
        String name = adminNameField.getText().trim();
        String newPwd = new String(adminNewPasswordField.getPassword());
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "请输入员工姓名", "验证失败", JOptionPane.WARNING_MESSAGE); return; }
        if (newPwd.isEmpty()) { JOptionPane.showMessageDialog(this, "请输入新密码", "验证失败", JOptionPane.WARNING_MESSAGE); return; }

        // 在不修改表结构前提下，通过查找姓名匹配来更新密码（若存在多个同名，会更新第一个匹配）
        for (Staff s : userService.getAllStaff()) {
            if (name.equals(s.getName())) {
                s.setPassword(newPwd);
                boolean ok = userService.updateStaff(s);
                if (ok) JOptionPane.showMessageDialog(this, "密码修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                else JOptionPane.showMessageDialog(this, "密码修改失败", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "未找到指定姓名的员工", "提示", JOptionPane.WARNING_MESSAGE);
    }
}
