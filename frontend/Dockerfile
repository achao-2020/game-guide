# 使用本地已构建的 dist 目录
FROM nginx:alpine

# 复制构建产物
COPY dist /usr/share/nginx/html

# 复制 Nginx 配置模板
COPY nginx.conf /etc/nginx/conf.d/default.conf.template

# 复制启动脚本
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 80

# 使用启动脚本
ENTRYPOINT ["/docker-entrypoint.sh"]