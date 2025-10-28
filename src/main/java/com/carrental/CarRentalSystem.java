package com.carrental;
import com.carrental.gui.LoginFrame;

/**
 * 汽车出租管理系统主类
 * 
 * @author 开发者
 * @version 1.0
 * @since 2024
 */
public class CarRentalSystem {
    
    /**
     * 主方法
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 设置系统外观
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("设置外观失败: " + e.getMessage());
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
                    System.err.println("启动系统失败: " + e.getMessage());
                    e.printStackTrace();
                    javax.swing.JOptionPane.showMessageDialog(null, 
                        "系统启动失败: " + e.getMessage(), 
                        "错误", 
                        javax.swing.JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
}