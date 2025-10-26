package com.carrental.gui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义日期选择器组件
 * 模仿Windows风格的日期选择器
 */
public class DatePicker extends JPanel {
    private static final long serialVersionUID = 1L;

    // 格式器
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月");

    // UI 常量
    private static final Font LABEL_FONT = new Font(Font.DIALOG, Font.BOLD, 12);
    private static final Font HEADER_FONT = new Font(Font.DIALOG, Font.BOLD, 14);
    private static final Font BUTTON_FONT = new Font(Font.DIALOG, Font.PLAIN, 11);
    private static final Font CONTROL_BUTTON_FONT = new Font(Font.DIALOG, Font.BOLD, 12);
    private static final Font YEAR_BUTTON_FONT = new Font(Font.DIALOG, Font.BOLD, 10);

    private static final Color TODAY_COLOR = new Color(135, 206, 250); // 浅蓝
    private static final Color SELECTED_COLOR = new Color(0, 123, 255); // 蓝
    private static final Color GRAY_TEXT = Color.GRAY;
    private static final Color BLACK_TEXT = Color.BLACK;
    private static final Color WHITE_BG = Color.WHITE;

    private static final Dimension CALENDAR_BUTTON_SIZE = new Dimension(30, 25);
    private static final Dimension CONTROL_BUTTON_SIZE = new Dimension(30, 25);
    private static final Dimension POPUP_SIZE = new Dimension(280, 220);

    // 组件
    private final JTextField textField = new JTextField();
    private final JButton calendarButton = new JButton("📅");
    private final JPopupMenu popupMenu = new JPopupMenu();
    private final JPanel calendarPanel = new JPanel(new BorderLayout());
    private final JLabel monthYearLabel = new JLabel("", JLabel.CENTER);
    private final JPanel dayPanel = new JPanel(new GridLayout(0, 7, 2, 2));
    private final List<JButton> dayButtons = new ArrayList<>();

    // 状态
    private LocalDate selectedDate;
    private LocalDate currentDate;

    public DatePicker() {
        initComponents();
        setupLayout();
        setupEvents();
        setCurrentDate(LocalDate.now());
    }

    private void initComponents() {
        setLayout(new BorderLayout());

    // TextField
    // 不强制固定像素宽度，使用列数让布局管理器决定宽度，从而在不同对话框中可以完整显示
    textField.setEditable(false);
    textField.setColumns(12);

        // Calendar Button
        calendarButton.setPreferredSize(CALENDAR_BUTTON_SIZE);
        calendarButton.setFont(BUTTON_FONT);
        calendarButton.setToolTipText("选择日期");

        // Popup
        popupMenu.setPreferredSize(POPUP_SIZE);
        calendarPanel.setBorder(new LineBorder(Color.GRAY, 1));

        // Month/Year Label
        monthYearLabel.setFont(HEADER_FONT);
        monthYearLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        monthYearLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        monthYearLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setCurrentDate(LocalDate.now());
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                monthYearLabel.setForeground(Color.BLUE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                monthYearLabel.setForeground(BLACK_TEXT);
            }
        });

        // Weekday Headers
        String[] weekDays = {"日", "一", "二", "三", "四", "五", "六"};
        for (String day : weekDays) {
            JLabel label = new JLabel(day, JLabel.CENTER);
            label.setFont(LABEL_FONT);
            label.setForeground(Color.BLUE);
            dayPanel.add(label);
        }

        // Day Buttons (6 weeks * 7 days)
        for (int i = 0; i < 42; i++) {
            JButton btn = createDayButton();
            dayButtons.add(btn);
            dayPanel.add(btn);
        }

        // Control Panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton prevYear = createControlButton("<<", YEAR_BUTTON_FONT);
        JButton prevMonth = createControlButton("◀", CONTROL_BUTTON_FONT);
        JButton nextMonth = createControlButton("▶", CONTROL_BUTTON_FONT);
        JButton nextYear = createControlButton(">>", YEAR_BUTTON_FONT);

        controlPanel.add(prevYear);
        controlPanel.add(prevMonth);
        controlPanel.add(monthYearLabel);
        controlPanel.add(nextMonth);
        controlPanel.add(nextYear);

        calendarPanel.add(controlPanel, BorderLayout.NORTH);
        calendarPanel.add(dayPanel, BorderLayout.CENTER);
        popupMenu.add(calendarPanel);

        // Actions
        prevMonth.addActionListener(e -> changeMonth(-1));
        nextMonth.addActionListener(e -> changeMonth(1));
        prevYear.addActionListener(e -> changeYear(-1));
        nextYear.addActionListener(e -> changeYear(1));
    }

    private JButton createDayButton() {
        JButton btn = new JButton();
        btn.setPreferredSize(new Dimension(30, 25));
        btn.setFont(BUTTON_FONT);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setContentAreaFilled(true);
        return btn;
    }

    private JButton createControlButton(String text, Font font) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(CONTROL_BUTTON_SIZE);
        btn.setFont(font);
        return btn;
    }

    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(textField, BorderLayout.CENTER);
        mainPanel.add(calendarButton, BorderLayout.EAST);
        add(mainPanel);
    }

    private void setupEvents() {
        calendarButton.addActionListener(e -> toggleCalendar());
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCalendar();
            }
        });

        // 使用索引避免闭包问题（但 Java 8 中 Lambda 捕获是安全的）
        for (int i = 0; i < dayButtons.size(); i++) {
            final int index = i;
            dayButtons.get(i).addActionListener(e -> onDayClicked(index));
        }
    }

    private void toggleCalendar() {
        if (popupMenu.isVisible()) {
            popupMenu.setVisible(false);
        } else {
            showCalendar();
        }
    }

    private void showCalendar() {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }
        updateCalendar();
        popupMenu.show(calendarButton, 0, calendarButton.getHeight());
    }

    private void onDayClicked(int index) {
        JButton btn = dayButtons.get(index);
        String text = btn.getText().trim();
        if (text.isEmpty()) return;

        // 判断是否为当前月的日期（通过颜色不可靠，改用逻辑）
        YearMonth currentYM = YearMonth.from(currentDate);
        int daysInMonth = currentYM.lengthOfMonth();

        // 计算第一天是周几（周日=0）
        LocalDate firstDay = currentYM.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 :
                firstDay.getDayOfWeek().getValue(); // MON=1 ... SAT=6

        boolean isCurrentMonth = (index >= firstDayOfWeek) && (index < firstDayOfWeek + daysInMonth);
        if (!isCurrentMonth) return;

        try {
            int day = Integer.parseInt(text);
            LocalDate date = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day);
            setSelectedDate(date);
            popupMenu.setVisible(false);
        } catch (NumberFormatException ignored) {
            // 忽略无效输入
        }
    }

    private void updateCalendar() {
        if (currentDate == null) return;

        monthYearLabel.setText(currentDate.format(MONTH_YEAR_FORMATTER));

        // Reset all buttons
        for (JButton btn : dayButtons) {
            btn.setText("");
            btn.setBackground(WHITE_BG);
            btn.setForeground(BLACK_TEXT);
            btn.setEnabled(true);
        }

        YearMonth yearMonth = YearMonth.from(currentDate);
        LocalDate firstDay = yearMonth.atDay(1);
        int firstDayOfWeek = firstDay.getDayOfWeek() == DayOfWeek.SUNDAY ? 0 :
                firstDay.getDayOfWeek().getValue(); // MON=1 ... SAT=6

        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate today = LocalDate.now();

        // 当前月
        for (int day = 1; day <= daysInMonth; day++) {
            int idx = firstDayOfWeek + day - 1;
            JButton btn = dayButtons.get(idx);
            btn.setText(String.valueOf(day));
            LocalDate date = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), day);

            if (date.equals(today)) {
                btn.setBackground(TODAY_COLOR);
                btn.setForeground(BLACK_TEXT);
            } else if (selectedDate != null && date.equals(selectedDate)) {
                btn.setBackground(SELECTED_COLOR);
                btn.setForeground(BLACK_TEXT); // 修复：选中日期字体为黑色，保证可见
            } else {
                btn.setBackground(WHITE_BG);
                btn.setForeground(BLACK_TEXT);
            }
        }

        // 上个月
        YearMonth prevMonth = yearMonth.minusMonths(1);
        int prevDays = prevMonth.lengthOfMonth();
        for (int i = firstDayOfWeek - 1; i >= 0; i--) {
            int day = prevDays - (firstDayOfWeek - 1 - i);
            JButton btn = dayButtons.get(i);
            btn.setText(String.valueOf(day));
            btn.setForeground(GRAY_TEXT);
            btn.setBackground(WHITE_BG);
        }

        // 下个月
        int nextStart = firstDayOfWeek + daysInMonth;
        for (int i = nextStart; i < 42; i++) {
            int day = i - nextStart + 1;
            JButton btn = dayButtons.get(i);
            btn.setText(String.valueOf(day));
            btn.setForeground(GRAY_TEXT);
            btn.setBackground(WHITE_BG);
        }
    }

    private void changeMonth(int months) {
        ensureCurrentDate();
        currentDate = currentDate.plusMonths(months);
        updateCalendar();
    }

    private void changeYear(int years) {
        ensureCurrentDate();
        currentDate = currentDate.plusYears(years);
        updateCalendar();
    }

    private void ensureCurrentDate() {
        if (currentDate == null) {
            currentDate = LocalDate.now();
        }
    }

    public void setSelectedDate(LocalDate date) {
        this.selectedDate = date;
        textField.setText(date == null ? "" : date.format(DATE_FORMATTER));
    }

    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    public void setCurrentDate(LocalDate date) {
        this.currentDate = date;
        if (selectedDate == null) {
            setSelectedDate(date);
        }
        updateCalendar();
    }

    public boolean isValidDate() {
        return selectedDate != null;
    }

    public void clear() {
        selectedDate = null;
        textField.setText("");
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        calendarButton.setEnabled(enabled);
    }
}