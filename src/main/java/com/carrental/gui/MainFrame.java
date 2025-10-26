package com.carrental.gui;

import com.carrental.entity.Staff;
import com.carrental.entity.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 主界面
 * 系统的主要功能入口
 */
public class MainFrame extends JFrame {
    private Staff currentStaff;
    private JTabbedPane tabbedPane;


    private  User currentUser;

    public MainFrame(Staff staff) {
        this.currentStaff = staff;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupFrame();
    }
    public MainFrame(User user) {
        this.currentUser = user;
        initializeComponentsForUser();
        setupLayoutForUser();
        setupEventHandlers();
        setupFrame();

    }
    private void initializeComponentsForUser() {
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("车辆信息", new CarManagementPanelForUser()); // 只读或者可租借
    }


    /**
     * 初始化组件
     */
    private void initializeComponents() {
        tabbedPane = new JTabbedPane();

        // 根据权限显示不同的标签页
        if (currentStaff.getRole() >= 2) {
            // 员工权限及以上
            tabbedPane.addTab("车辆管理", new CarManagementPanel());
            tabbedPane.addTab("租车管理", new RentManagementPanel());
        }

        if (currentStaff.getRole() >= 6) {
            // 经理权限及以上
            tabbedPane.addTab("用户管理", new UserManagementPanel());
            tabbedPane.addTab("员工管理", new StaffManagementPanel());
        }

        if (currentStaff.getRole() >= 9) {
            // 董事长权限
            tabbedPane.addTab("财务报表", new FinancialReportPanel());
            tabbedPane.addTab("系统设置", new SystemSettingsPanel());
        }
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // 中间标签页
        add(tabbedPane, BorderLayout.CENTER);

        // 底部状态栏
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupLayoutForUser() {
        setLayout(new BorderLayout());

        // 顶部面板
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("汽车出租管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // 用户信息
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userLabel = new JLabel("当前用户: " + currentUser.getName());
        JButton logoutButton = new JButton("退出登录");
        logoutButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        userPanel.add(userLabel);
        userPanel.add(logoutButton);
        topPanel.add(userPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * 创建顶部面板
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.setBackground(new Color(240, 240, 240));

        // 左侧标题
        JLabel titleLabel = new JLabel("汽车出租管理系统");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.WEST);

        // 右侧用户信息
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel userLabel = new JLabel("当前用户: " + currentStaff.getName() + " (" + currentStaff.getPosition() + ")");
        userLabel.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        userPanel.add(userLabel);

        JButton logoutButton = new JButton("退出登录");
        logoutButton.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要退出登录吗？", "确认退出", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    dispose();
                    new LoginFrame().setVisible(true);
                }
            }
        });
        userPanel.add(logoutButton);

        topPanel.add(userPanel, BorderLayout.EAST);

        return topPanel;
    }

    /**
     * 创建底部面板
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bottomPanel.setBackground(new Color(240, 240, 240));

        JLabel statusLabel = new JLabel("系统运行正常 | 数据库连接正常");
        statusLabel.setFont(new Font("微软雅黑", Font.PLAIN, 10));
        statusLabel.setForeground(Color.GRAY);
        bottomPanel.add(statusLabel);

        return bottomPanel;
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        // 窗口关闭事件
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                int result = JOptionPane.showConfirmDialog(MainFrame.this,
                        "确定要退出系统吗？", "确认退出", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }

    /**
     * 设置窗口属性
     */
    private void setupFrame() {
        setTitle("汽车出租管理系统 - 主界面");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 600));

        // 设置图标
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // 忽略图标加载失败
        }
    }

    /**
     * 获取当前登录员工
     * @return 当前员工
     */
    public Staff getCurrentStaff() {
        return currentStaff;
    }
}
