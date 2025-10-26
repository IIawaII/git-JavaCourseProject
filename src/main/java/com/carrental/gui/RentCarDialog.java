package com.carrental.gui;

import com.carrental.entity.Car;
import com.carrental.entity.User;
import com.carrental.service.CarService;
import com.carrental.service.RentService;
import com.carrental.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 租车对话框
 * 用于处理租车业务
 */
public class RentCarDialog extends JDialog {
    private RentService rentService;
    private CarService carService;
    private UserService userService;
    private JComboBox<Object> carComboBox;
    private JComboBox<Object> userComboBox;
    private DatePicker rentDatePicker;
    private DatePicker returnDatePicker;
    private JLabel rentAmountLabel;
    private JButton calculateButton;
    private JButton rentButton;
    private JButton cancelButton;
    private RentManagementPanel parentPanel;

    public RentCarDialog(RentManagementPanel parent) {
        super((JFrame) SwingUtilities.getWindowAncestor(parent), "租车", true);
        this.parentPanel = parent;
        this.rentService = new RentService();
        this.carService = new CarService();
        this.userService = new UserService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        setupDialog();
        loadData();
    }

    /**
     * 初始化组件
     */
    private void initializeComponents() {
    carComboBox = new JComboBox<>();
    userComboBox = new JComboBox<>();
    rentDatePicker = new DatePicker();
    returnDatePicker = new DatePicker();
    Dimension dateDim = new Dimension(140, 28);
    rentDatePicker.setPreferredSize(dateDim);
    returnDatePicker.setPreferredSize(dateDim);
    Font dateFont = new Font("微软雅黑", Font.PLAIN, 14);
    rentDatePicker.setFont(dateFont);
    returnDatePicker.setFont(dateFont);
        
        // 设置默认选中日期
        rentDatePicker.setSelectedDate(LocalDate.now());
        returnDatePicker.setSelectedDate(LocalDate.now().plusDays(1));
        rentAmountLabel = new JLabel("¥0.00");
        calculateButton = new JButton("计算租金");
        rentButton = new JButton("确认租车");
        cancelButton = new JButton("取消");

        Font font = new Font("微软雅黑", Font.PLAIN, 12);
        carComboBox.setFont(font);
        userComboBox.setFont(font);
        rentDatePicker.setFont(font);
        returnDatePicker.setFont(font);
        rentAmountLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        calculateButton.setFont(font);
        rentButton.setFont(font);
        cancelButton.setFont(font);
    }

    /**
     * 设置布局
     */
    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.EAST;

        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("选择车辆:"), gbc);
        gbc.gridx = 1;
        // 让第二列在水平方向上伸展，以完整显示下拉与日期组件
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(carComboBox, gbc);
        // 恢复默认约束以便下一行标签靠右对齐
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("选择用户:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        mainPanel.add(userComboBox, gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("租借日期:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
    mainPanel.add(rentDatePicker, gbc);
    rentDatePicker.repaint();
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("归还日期:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
    mainPanel.add(returnDatePicker, gbc);
    returnDatePicker.repaint();
    gbc.fill = GridBagConstraints.NONE;
    gbc.weightx = 0;
        row++;

        gbc.gridx = 0; gbc.gridy = row; mainPanel.add(new JLabel("预计租金:"), gbc);
        gbc.gridx = 1;
        JPanel rentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        rentPanel.add(rentAmountLabel);
        rentPanel.add(calculateButton);
        mainPanel.add(rentPanel, gbc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.add(rentButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * 设置事件处理器
     */
    private void setupEventHandlers() {
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateRent();
            }
        });
        
        rentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRent();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    /**
     * 设置对话框属性
     */
    private void setupDialog() {
        setSize(400, 300);
        setLocationRelativeTo(getParent());
        setResizable(false);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        carComboBox.removeAllItems();
        for (Car car : carService.getAvailableCars()) {
            carComboBox.addItem(new CarComboItem(car));
        }
        userComboBox.removeAllItems();
        for (User user : userService.getAllUsers()) {
            userComboBox.addItem(new UserComboItem(user));
        }
        // 默认日期
    rentDatePicker.setSelectedDate(LocalDate.now());
    returnDatePicker.setSelectedDate(LocalDate.now().plusDays(1));
    rentDatePicker.repaint();
    returnDatePicker.repaint();
    }

    // 下拉框显示友好对象
    private static class CarComboItem {
        private final Car car;
        public CarComboItem(Car car) { this.car = car; }
        public Car getCar() { return car; }
        @Override public String toString() {
            return car.getLicensePlateNumber() + " - " + car.getBrand() + " " + car.getModel();
        }
    }
    private static class UserComboItem {
        private final User user;
        public UserComboItem(User user) { this.user = user; }
        public User getUser() { return user; }
        @Override public String toString() {
            return user.getName() + " (" + user.getIdentityId() + ")";
        }
    }

    /**
     * 计算租金
     */
    private void calculateRent() {
        if (!validateInput()) return;
        try {
            CarComboItem carItem = (CarComboItem) carComboBox.getSelectedItem();
            LocalDate rentDate = rentDatePicker.getSelectedDate();
            LocalDate returnDate = returnDatePicker.getSelectedDate();
            BigDecimal rentAmount = carService.calculateRent(carItem.getCar().getCarId(), rentDate, returnDate);
            rentAmountLabel.setText("¥" + rentAmount.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "计算租金失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 执行租车
     */
    private void performRent() {
        if (!validateInput()) return;
        try {
            CarComboItem carItem = (CarComboItem) carComboBox.getSelectedItem();
            UserComboItem userItem = (UserComboItem) userComboBox.getSelectedItem();
            LocalDate rentDate = rentDatePicker.getSelectedDate();
            LocalDate returnDate = returnDatePicker.getSelectedDate();
            int staffId = 1; // TODO: 从登录状态获取
            BigDecimal rentAmount = carService.calculateRent(carItem.getCar().getCarId(), rentDate, returnDate);
            int result = JOptionPane.showConfirmDialog(this,
                "确认租车信息:\n" +
                "车辆: " + carItem.getCar().getLicensePlateNumber() + "\n" +
                "用户: " + userItem.getUser().getName() + "\n" +
                "租期: " + rentDate + " 至 " + returnDate + "\n" +
                "租金: ¥" + rentAmount + "\n\n确定要租车吗？",
                "确认租车", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                if (rentService.rentCar(carItem.getCar().getCarId(), userItem.getUser().getUserId(), staffId, rentDate, returnDate)) {
                    JOptionPane.showMessageDialog(this, "租车成功", "提示", JOptionPane.INFORMATION_MESSAGE);
                    parentPanel.refreshData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "租车失败", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "租车失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 验证输入
     * @return 是否有效
     */
    private boolean validateInput() {
        if (carComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "请选择车辆", "验证失败", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (userComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "请选择用户", "验证失败", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (!rentDatePicker.isValidDate()) {
            JOptionPane.showMessageDialog(this, "请选择有效的租借日期", "验证失败", JOptionPane.WARNING_MESSAGE);
            rentDatePicker.requestFocus();
            return false;
        }
        if (!returnDatePicker.isValidDate()) {
            JOptionPane.showMessageDialog(this, "请选择有效的归还日期", "验证失败", JOptionPane.WARNING_MESSAGE);
            returnDatePicker.requestFocus();
            return false;
        }
        LocalDate rentDate = rentDatePicker.getSelectedDate();
        LocalDate returnDate = returnDatePicker.getSelectedDate();
        if (rentDate.isAfter(returnDate)) {
            JOptionPane.showMessageDialog(this, "租借日期不能晚于归还日期", "验证失败", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }
}
