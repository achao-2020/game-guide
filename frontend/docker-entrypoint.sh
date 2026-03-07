#!/bin/sh

# 设置默认的后端地址
BACKEND_URL=${BACKEND_URL:-"192.168.2.108:8080"}

echo "Backend URL: $BACKEND_URL"

# 替换 nginx 配置中的环境变量
envsubst '${BACKEND_URL}' < /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

# 启动 nginx
nginx -g 'daemon off;'

