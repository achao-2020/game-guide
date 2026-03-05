#!/bin/bash

echo "===================================="
echo "游戏攻略知识库系统 - 数据库初始化"
echo "===================================="
echo ""

echo "[1/2] 初始化基础表结构..."
psql -U postgres -d game_guide -f database/init.sql
if [ $? -ne 0 ]; then
    echo "错误: 基础表初始化失败"
    exit 1
fi

echo "[2/2] 初始化用户表..."
psql -U postgres -d game_guide -f database/user.sql
if [ $? -ne 0 ]; then
    echo "错误: 用户表初始化失败"
    exit 1
fi

echo ""
echo "===================================="
echo "数据库初始化完成！"
echo "===================================="
echo ""
echo "默认账号:"
echo "  管理员: admin / admin123"
echo "  普通用户: user / admin123"
echo ""



