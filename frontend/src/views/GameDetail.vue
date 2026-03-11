<template>
  <div class="game-detail-page" v-loading="loading">
    <div v-if="game" class="game-container">
      <!-- 游戏头部 -->
      <div class="game-header">
        <div class="game-cover">
          <img :src="game.coverImage" :alt="game.name" />
        </div>
        <div class="game-info">
          <h1 class="game-title">{{ game.name }}</h1>
          <p class="game-description">{{ game.description }}</p>
          <div class="game-meta">
            <span class="meta-item">
              <el-icon><Calendar /></el-icon>
              发布时间：{{ formatTime(game.createdAt) }}
            </span>
          </div>
        </div>
      </div>

      <!-- 攻略列表 -->
      <div class="guides-section">
        <div class="section-header">
          <h2 class="section-title">相关攻略</h2>
          <el-select v-model="selectedCategory" placeholder="选择分类" clearable @change="loadGuides">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </div>

        <div class="guides-list" v-loading="guidesLoading">
          <div
            v-for="guide in guides"
            :key="guide.id"
            class="guide-item"
            @click="goToGuide(guide.id)"
          >
            <div class="guide-main">
              <h3 class="guide-title">{{ guide.title }}</h3>
              <div class="guide-content" v-html="renderMarkdown(guide.content)"></div>
              <div class="guide-footer">
                <div class="guide-tags">
                  <el-tag
                    v-for="tag in guide.tags"
                    :key="tag.id"
                    size="small"
                    effect="plain"
                  >
                    {{ tag.name }}
                  </el-tag>
                </div>
                <span class="guide-time">{{ formatTime(guide.createdAt) }}</span>
              </div>
            </div>
          </div>

          <el-empty v-if="!guidesLoading && guides.length === 0" description="暂无攻略" />
        </div>

        <el-pagination
          v-if="pagination.total > 0"
          v-model:current-page="pagination.pageNum"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @size-change="loadGuides"
          @current-change="loadGuides"
          style="margin-top: 24px; justify-content: center"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Calendar } from '@element-plus/icons-vue'
import { gameAPI } from '@/api/game'
import { guideAPI } from '@/api/guide'
import { categoryAPI } from '@/api/category'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const guidesLoading = ref(false)
const game = ref(null)
const guides = ref([])
const categories = ref([])
const selectedCategory = ref(null)

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

// 渲染 Markdown 内容
const renderMarkdown = (content) => {
  if (!content) return ''
  return marked(content)
}

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const loadGame = async () => {
  loading.value = true
  try {
    const id = route.params.id
    game.value = await gameAPI.getById(id)
  } catch (error) {
    console.error('加载游戏失败:', error)
  } finally {
    loading.value = false
  }
}

const loadGuides = async () => {
  guidesLoading.value = true
  try {
    const params = {
      gameId: route.params.id,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    }
    if (selectedCategory.value) {
      params.categoryId = selectedCategory.value
    }
    const data = await guideAPI.search(params)
    guides.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载攻略失败:', error)
  } finally {
    guidesLoading.value = false
  }
}

const loadCategories = async () => {
  try {
    categories.value = await categoryAPI.getAll()
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const goToGuide = (id) => {
  router.push(`/guide/${id}`)
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadGame()
  loadGuides()
  loadCategories()
})
</script>

<style scoped>
.game-detail-page {
  width: 100%;
}

.game-container {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.game-header {
  display: flex;
  gap: 32px;
  padding: 32px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.game-cover {
  width: 300px;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
}

.game-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.game-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.game-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 16px;
}

.game-description {
  font-size: 16px;
  line-height: 1.8;
  margin-bottom: 24px;
  opacity: 0.95;
}

.game-meta {
  display: flex;
  gap: 24px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  opacity: 0.9;
}

.guides-section {
  padding: 32px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.guides-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.guide-item {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.guide-item:hover {
  background: white;
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

.guide-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 12px;
}

.guide-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.guide-content :deep(h1),
.guide-content :deep(h2),
.guide-content :deep(h3),
.guide-content :deep(h4),
.guide-content :deep(h5),
.guide-content :deep(h6) {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  display: inline;
}

.guide-content :deep(p) {
  margin: 0;
  display: inline;
}

.guide-content :deep(code) {
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  font-size: 85%;
  padding: 0.2em 0.4em;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.guide-content :deep(pre) {
  display: none;
}

.guide-content :deep(ul),
.guide-content :deep(ol) {
  margin: 0;
  padding-left: 0;
  display: inline;
}

.guide-content :deep(li) {
  display: inline;
}

.guide-content :deep(blockquote) {
  display: none;
}

.guide-content :deep(img) {
  display: none;
}

.guide-content :deep(table) {
  display: none;
}

.guide-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.guide-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.guide-time {
  font-size: 12px;
  color: #999;
}
</style>

