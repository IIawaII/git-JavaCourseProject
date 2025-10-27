package com.carrental;

import com.carrental.gui.LoginFrame;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 汽车出租管理系统主类
 * 
 * @author 开发者
 * @version 1.0
 * @since 2024
 */
public class CarRentalSystem {
    private static final Logger LOGGER = Logger.getLogger(CarRentalSystem.class.getName());
    
    /**
     * 主方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 设置系统外观
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "设置外观失败: " + e.getMessage(), e);
        }
        
        // 在应用启动时清理由于异常断开导致的陈旧登录状态
        try {
            new com.carrental.dao.StaffDAO().cleanupStaleLogins();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "清理陈旧登录时出错: " + e.getMessage(), e);
        }

        // 在事件分发线程中启动GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    // 显示登录界面
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.setVisible(true);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "启动系统失败: " + e.getMessage(), e);
                    javax.swing.JOptionPane.showMessageDialog(null,
                        "系统启动失败: " + e.getMessage(),
                        "错误",
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}