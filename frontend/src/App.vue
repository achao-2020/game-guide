<template>
  <router-view />
</template>

<script setup>
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

#app {
  width: 100%;
  height: 100vh;
}

/* 全局修复 Element Plus 图片预览层级问题 */
.el-image-viewer__wrapper {
  z-index: 999999 !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
}

.el-image-viewer__mask {
  z-index: 999998 !important;
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  right: 0 !important;
  bottom: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  background-color: rgba(0, 0, 0, 0.5) !important;
  pointer-events: auto !important;
}

.el-image-viewer__btn {
  z-index: 1000000 !important;
  position: absolute !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  opacity: 1 !important;
  visibility: visible !important;
  width: 44px !important;
  height: 44px !important;
  font-size: 24px !important;
  color: #fff !important;
  background-color: transparent !important;
  border-radius: 50% !important;
  cursor: pointer !important;
}

.el-image-viewer__btn:hover {
  background-color: rgba(0, 0, 0, 0.5) !important;
}

.el-image-viewer__canvas {
  z-index: 999999 !important;
  position: relative !important;
}

.el-image-viewer__close {
  z-index: 1000001 !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
  opacity: 1 !important;
  visibility: visible !important;
  position: absolute !important;
  top: 40px !important;
  right: 40px !important;
  width: 44px !important;
  height: 44px !important;
  font-size: 24px !important;
  color: #fff !important;
  background-color: rgba(0, 0, 0, 0.5) !important;
  border-radius: 50% !important;
  cursor: pointer !important;
}

.el-image-viewer__close:hover {
  background-color: rgba(0, 0, 0, 0.8) !important;
}

/* 工具栏按钮布局 */
.el-image-viewer__actions {
  z-index: 1000001 !important;
  position: absolute !important;
  left: 50% !important;
  top: 40px !important;
  transform: translateX(-50%) !important;
  display: flex !important;
  gap: 20px !important;
  background-color: transparent !important;
  padding: 0 !important;
  border-radius: 0 !important;
}

.el-image-viewer__actions .el-image-viewer__actions__inner {
  display: flex !important;
  gap: 20px !important;
  background-color: transparent !important;
}

/* 确保其他元素不会覆盖预览 */
.el-table,
.el-pagination,
.el-card,
.el-dialog {
  position: relative;
  z-index: auto !important;
}
</style>



