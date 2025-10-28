'''
MacOS专用字体设置
MacOS 系统中使用的中文字体路径
适用于MacOS15版本
可直接复制到代码中使用
'''
import matplotlib.pyplot as plt
from matplotlib import font_manager
# 设置字体路径
font_path = '/System/Library/Fonts/STHeiti Medium.ttc'

# 加载字体
my_font = font_manager.FontProperties(fname=font_path)

# 设置为默认字体
plt.rcParams['font.family'] = my_font.get_name()
plt.rcParams['axes.unicode_minus'] = False  # 正确显示负号

import mysql.connector
import matplotlib.pyplot as plt

# 连接到数据库
conn = mysql.connector.connect(
    host="localhost",       # 数据库主机地址
    user="root",            # 用户名
    password="12345678",    # 密码
    database="car_rental_system"  # 数据库名
)

cursor = conn.cursor()

# 查询最近30天的利润数据
query = """
    SELECT DATE(return_date) AS date,
           SUM(pay_the_amount) - SUM(return_amount) AS profit
    FROM rent_information
    WHERE return_date >= CURDATE() - INTERVAL 30 DAY
    GROUP BY DATE(return_date)
    ORDER BY DATE(return_date);
"""
cursor.execute(query)

# 获取查询结果
results = cursor.fetchall()

# 提取日期和利润
dates = [row[0] for row in results]
profits = [row[1] for row in results]

# 绘制折线图
plt.plot(dates, profits, label="Profit", color="b", marker='o')
plt.xlabel("Date")
plt.ylabel("Profit")
plt.title("最近30天利润变化图")
plt.xticks(rotation=45)  # 旋转X轴的日期标签
plt.legend()
plt.tight_layout()  # 自动调整布局以防止标签重叠
plt.savefig('/Volumes/HIKSEMI/GitHub_project/java_car_rent_system/git-JavaCourseProject/python/plot/recent_day_profit_plot.png')

# 关闭数据库连接
cursor.close()
conn.close()