<template>
  <div class="guide-detail-page" v-loading="loading">
    <div v-if="guide" class="guide-container">
      <!-- 攻略头部 -->
      <div class="guide-header">
        <div class="breadcrumb">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item :to="{ path: `/game/${guide.gameId}` }">
              {{ guide.gameName }}
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ guide.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <h1 class="guide-title">{{ guide.title }}</h1>
        
        <div class="guide-meta">
          <el-tag type="primary">{{ guide.gameName }}</el-tag>
          <el-tag v-if="guide.categoryName" type="success">{{ guide.categoryName }}</el-tag>
          <span class="meta-item">
            <el-icon><Calendar /></el-icon>
            {{ formatTime(guide.createdAt) }}
          </span>
        </div>
        
        <div class="guide-tags">
          <el-tag
            v-for="tag in guide.tags"
            :key="tag.id"
            effect="plain"
          >
            {{ tag.name }}
          </el-tag>
        </div>
      </div>

      <!-- 攻略内容 -->
      <div class="guide-content">
        <div class="content-body" v-html="formatContent(guide.content)"></div>
      </div>

      <!-- 相关攻略 -->
      <div class="related-guides" v-if="relatedGuides.length > 0">
        <h2 class="section-title">相关攻略</h2>
        <div class="guides-grid">
          <div
            v-for="item in relatedGuides"
            :key="item.id"
            class="guide-card"
            @click="goToGuide(item.id)"
          >
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-content">{{ item.content }}</p>
            <div class="card-footer">
              <el-tag size="small" type="info">{{ item.gameName }}</el-tag>
              <span class="card-time">{{ formatTime(item.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Calendar } from '@element-plus/icons-vue'
import { guideAPI } from '@/api/guide'
import { marked } from 'marked'
import hljs from 'highlight.js'

// 配置 marked
marked.setOptions({
  highlight: function(code, lang) {
    if (lang && hljs.getLanguage(lang)) {
      try {
        return hljs.highlight(code, { language: lang }).value
      } catch (err) {
        console.error(err)
      }
    }
    return hljs.highlightAuto(code).value
  },
  breaks: true,
  gfm: true
})

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const guide = ref(null)
const relatedGuides = ref([])

const loadGuide = async () => {
  loading.value = true
  try {
    const id = route.params.id
    guide.value = await guideAPI.getById(id)
    
    // 加载相关攻略
    if (guide.value.gameId) {
      const data = await guideAPI.search({
        gameId: guide.value.gameId,
        pageNum: 1,
        pageSize: 4
      })
      relatedGuides.value = (data.list || []).filter(item => item.id !== guide.value.id)
    }
  } catch (error) {
    console.error('加载攻略失败:', error)
  } finally {
    loading.value = false
  }
}

const goToGuide = (id) => {
  router.push(`/guide/${id}`)
  // 重新加载数据
  loadGuide()
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const formatContent = (content) => {
  if (!content) return ''
  // 使用 marked 解析 Markdown
  return marked.parse(content)
}

onMounted(() => {
  loadGuide()
})
</script>

<style scoped>
.guide-detail-page {
  width: 100%;
}

.guide-container {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.guide-header {
  padding: 32px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-bottom: 1px solid #e8e8e8;
}

.breadcrumb {
  margin-bottom: 16px;
}

.guide-title {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 16px;
  line-height: 1.4;
}

.guide-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
}

.guide-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.guide-content {
  padding: 32px;
}

.content-body {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  word-wrap: break-word;
}

/* Markdown 样式 */
.content-body :deep(h1),
.content-body :deep(h2),
.content-body :deep(h3),
.content-body :deep(h4),
.content-body :deep(h5),
.content-body :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.4;
  color: #333;
}

.content-body :deep(h1) {
  font-size: 28px;
  border-bottom: 2px solid #e8e8e8;
  padding-bottom: 8px;
}

.content-body :deep(h2) {
  font-size: 24px;
  border-bottom: 1px solid #e8e8e8;
  padding-bottom: 6px;
}

.content-body :deep(h3) {
  font-size: 20px;
}

.content-body :deep(h4) {
  font-size: 18px;
}

.content-body :deep(p) {
  margin-bottom: 16px;
  line-height: 1.8;
}

.content-body :deep(ul),
.content-body :deep(ol) {
  margin-bottom: 16px;
  padding-left: 24px;
}

.content-body :deep(li) {
  margin-bottom: 8px;
  line-height: 1.8;
}

.content-body :deep(blockquote) {
  margin: 16px 0;
  padding: 12px 20px;
  background: #f5f7fa;
  border-left: 4px solid #667eea;
  color: #666;
}

.content-body :deep(code) {
  padding: 2px 6px;
  background: #f5f7fa;
  border-radius: 4px;
  font-family: 'Courier New', Courier, monospace;
  font-size: 14px;
  color: #e83e8c;
}

.content-body :deep(pre) {
  margin: 16px 0;
  padding: 16px;
  background: #282c34;
  border-radius: 8px;
  overflow-x: auto;
}

.content-body :deep(pre code) {
  padding: 0;
  background: transparent;
  color: #abb2bf;
  font-size: 14px;
  line-height: 1.6;
}

.content-body :deep(table) {
  width: 100%;
  margin: 16px 0;
  border-collapse: collapse;
  border: 1px solid #e8e8e8;
}

.content-body :deep(th),
.content-body :deep(td) {
  padding: 12px;
  border: 1px solid #e8e8e8;
  text-align: left;
}

.content-body :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
}

.content-body :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 16px 0;
}

.content-body :deep(a) {
  color: #667eea;
  text-decoration: none;
}

.content-body :deep(a:hover) {
  text-decoration: underline;
}

.content-body :deep(hr) {
  margin: 24px 0;
  border: none;
  border-top: 1px solid #e8e8e8;
}

.related-guides {
  padding: 32px;
  background: #fafafa;
  border-top: 1px solid #e8e8e8;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin-bottom: 24px;
}

.guides-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.guide-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e8e8e8;
}

.guide-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-2px);
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-time {
  font-size: 12px;
  color: #999;
}
</style>

<style>
/* 引入 highlight.js 样式 */
@import 'highlight.js/styles/atom-one-dark.css';
</style>

