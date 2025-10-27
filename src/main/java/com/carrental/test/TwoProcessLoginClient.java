package com.carrental.test;

import com.carrental.entity.Staff;
import com.carrental.service.UserService;
import com.carrental.util.AppLogger;

/**
 * 简单客户端用于在独立 JVM 进程中尝试登录，并在成功后保持一段时间以检测并发登录。
 * 用法: java -cp bin com.carrental.test.TwoProcessLoginClient <username> <password> <holdSeconds>
 */
public class TwoProcessLoginClient {
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: TwoProcessLoginClient <username> <password> <holdSeconds>");
            return;
        }
        String username = args[0];
        String password = args[1];
        int hold = Integer.parseInt(args[2]);

        UserService userService = new UserService();
        AppLogger.info("尝试登录: " + username);
        Staff staff = userService.login(username, password);
        if (staff != null) {
            AppLogger.info("登录成功: " + staff.getName() + "，保持 " + hold + " 秒...");
            try {
                Thread.sleep(hold * 1000L);
            } finally {
                boolean ok = userService.logout(username);
                AppLogger.info("已登出: " + username + " -> " + ok);
            }
        } else {
            if (userService.wasLastLoginBlocked()) {
                AppLogger.info("登录被拒绝: 当前用户已在另一台设备登录");
            } else {
                AppLogger.info("登录失败: 用户名或密码错误/其他原因");
            }
        }
    }
}
