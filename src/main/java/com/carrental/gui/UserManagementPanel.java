package com.carrental.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 用户管理面板
 * 提供用户相关的管理功能
 */
public class UserManagementPanel extends JPanel {
    
    public UserManagementPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // 初始化组件
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel label = new JLabel("用户管理功能开发中...", SwingConstants.CENTER);
        label.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 16));
        add(label, BorderLayout.CENTER);
    }
}
