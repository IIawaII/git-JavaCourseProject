package com.carrental.test;

import com.carrental.entity.Staff;
import com.carrental.service.UserService;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 并发登录测试类
 * 测试MySQL锁机制是否能有效防止同一用户重复登录
 */
public class ConcurrentLoginTest {
    private static final String TEST_USERNAME = "super213";
    private static final String TEST_PASSWORD = "213";
    private static final int THREAD_COUNT = 10;
    private static final int TEST_ROUNDS = 3;
    
    public static void main(String[] args) {
        System.out.println("开始并发登录测试...");
        System.out.println("测试用户: " + TEST_USERNAME);
        System.out.println("并发线程数: " + THREAD_COUNT);
        System.out.println("测试轮数: " + TEST_ROUNDS);
        System.out.println("=====================================");
        
        for (int round = 1; round <= TEST_ROUNDS; round++) {
            System.out.println("\n第 " + round + " 轮测试:");
            testConcurrentLogin();
            
            // 等待一段时间再进行下一轮测试
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("\n=====================================");
        System.out.println("并发登录测试完成！");
    }
    
    /**
     * 执行并发登录测试
     */
    private static void testConcurrentLogin() {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        
        // 创建多个并发登录任务
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i + 1;
            executor.submit(() -> {
                try {
                    // 等待所有线程准备就绪
                    startLatch.await();
                    
                    UserService userService = new UserService();
                    Staff staff = userService.login(TEST_USERNAME, TEST_PASSWORD);
                    
                    if (staff != null) {
                        successCount.incrementAndGet();
                        System.out.println("线程 " + threadId + ": 登录成功 - " + staff.getName());
                        
                        // 模拟用户操作一段时间
                        Thread.sleep(1000);
                        
                        // 登出
                        if (userService.logout(TEST_USERNAME)) {
                            System.out.println("线程 " + threadId + ": 登出成功");
                        } else {
                            System.out.println("线程 " + threadId + ": 登出失败");
                        }
                    } else {
                        failureCount.incrementAndGet();
                        System.out.println("线程 " + threadId + ": 登录失败（可能用户已登录）");
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("线程 " + threadId + " 被中断");
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.err.println("线程 " + threadId + " 发生异常: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }
        
        try {
            // 启动所有线程
            startLatch.countDown();
            
            // 等待所有线程完成
            boolean finished = endLatch.await(30, TimeUnit.SECONDS);
            
            if (!finished) {
                System.err.println("测试超时！");
            }
            
            // 输出测试结果
            System.out.println("测试结果:");
            System.out.println("  成功登录次数: " + successCount.get());
            System.out.println("  失败登录次数: " + failureCount.get());
            System.out.println("  预期结果: 只有1个线程应该成功登录");
            
            if (successCount.get() == 1) {
                System.out.println("  ✓ 测试通过！MySQL锁机制工作正常");
            } else {
                System.out.println("  ✗ 测试失败！可能存在并发问题");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("测试被中断");
        } finally {
            executor.shutdown();
        }
    }
}
