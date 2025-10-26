# 并发登录控制实现说明

## 概述

本实现使用MySQL的`GET_LOCK()`和`RELEASE_LOCK()`函数来实现并发登录控制，确保同一时间只有一个staff用户能够登录系统。

## 技术实现

### 1. MySQL锁机制

- 使用`GET_LOCK(lock_name, timeout)`获取命名锁
- 使用`RELEASE_LOCK(lock_name)`释放锁
- 锁名称格式：`staff_login_lock_{username}`

### 2. 登录状态标记

由于不能修改数据库表结构，使用以下方案标记登录状态：
- 将staff表的`role`字段设为负数表示已登录
- 登出时将`role`字段恢复为正数
- 在`mapResultSetToStaff`方法中确保返回的role始终为正数

### 3. 事务处理

- 使用手动事务提交模式确保锁的原子性
- 登录和登出操作都在事务中执行
- 异常时自动回滚事务

## 核心代码修改

### StaffDAO.java
- 修改`login()`方法，添加MySQL锁机制
- 添加`logout()`方法，清理登录状态
- 添加锁管理方法：`acquireLock()`, `releaseLock()`
- 添加状态管理方法：`isStaffAlreadyLoggedIn()`, `markStaffAsLoggedIn()`, `markStaffAsLoggedOut()`

### UserService.java
- 添加`logout()`方法

### MainFrame.java
- 修改登出按钮事件处理器，调用登出服务

### LoginFrame.java
- 更新错误提示信息，包含重复登录提示

## 测试

运行`ConcurrentLoginTest`类来测试并发登录控制：

```bash
# 编译测试类
javac -cp ".:mysql-connector-j-9.5.0.jar:src/main/java" src/main/java/com/carrental/test/ConcurrentLoginTest.java

# 运行测试
java -cp ".:mysql-connector-j-9.5.0.jar:src/main/java" com.carrental.test.ConcurrentLoginTest
```

## 自动恢复机制

系统具备智能的自动恢复机制：

1. **异常状态检测**：当检测到用户处于"已登录"状态时，系统会自动检查MySQL锁是否存在
2. **自动重置**：如果锁不存在（说明之前的会话已断开），系统会自动重置登录状态
3. **重试机制**：如果首次获取锁失败，系统会尝试自动恢复后重新获取锁
4. **无用户干预**：整个过程对用户透明，无需手动操作

## 工作原理

1. **登录过程**：
   - 获取用户专属的MySQL锁
   - 检查用户是否已登录（role < 0）
   - 验证用户名和密码
   - 标记用户为已登录状态（role设为负数）
   - 释放锁

2. **登出过程**：
   - 获取用户专属的MySQL锁
   - 清除登录状态标记（role恢复为正数）
   - 释放锁

3. **并发控制**：
   - 同一用户的登录和登出操作通过MySQL锁串行化
   - 不同用户之间不会相互影响
   - 锁超时时间为5秒，避免死锁

## 注意事项

1. **数据库连接**：确保MySQL服务器支持`GET_LOCK()`函数
2. **锁超时**：如果获取锁超时，系统会自动尝试恢复
3. **异常处理**：确保异常情况下锁能被正确释放
4. **自动恢复**：系统具备智能的自动恢复机制，无需手动干预

## 优势

1. **无框架依赖**：纯Java 8 + MySQL实现
2. **不修改表结构**：利用现有字段实现状态标记
3. **高并发安全**：基于数据库锁的原子操作
4. **简单可靠**：逻辑清晰，易于维护
5. **智能恢复**：自动处理异常状态，用户体验友好

## 限制

1. **状态标记方案**：使用role字段的符号位，可能影响权限判断逻辑
2. **锁超时**：如果网络延迟高，可能导致登录失败
3. **单点故障**：依赖MySQL服务器，如果数据库不可用则无法登录
