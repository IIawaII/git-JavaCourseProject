package com.carrental.gui;

import javax.swing.*;
import java.awt.*;

/**
 * 系统设置面板
 * 提供系统设置相关功能
 */
public class SystemSettingsPanel extends JPanel {
    
    public SystemSettingsPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // 初始化组件
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JLabel label = new JLabel("系统设置功能开发中...", SwingConstants.CENTER);
        label.setFont(new java.awt.Font("微软雅黑", java.awt.Font.BOLD, 16));
        add(label, BorderLayout.CENTER);
    }
}
