<template>
  <div class="guides-list-page">
    <div class="page-header">
      <h1 class="page-title">攻略列表</h1>
      <p class="page-desc">发现最全面的游戏攻略</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <div class="filter-row">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索攻略标题或内容"
          class="search-input"
          clearable
          @clear="handleSearch"
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #append>
            <el-button :icon="Search" @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
      </div>
      
      <div class="filter-row">
        <el-select
          v-model="selectedGame"
          placeholder="选择游戏"
          clearable
          filterable
          @change="handleFilter"
          class="filter-select"
        >
          <el-option
            v-for="game in games"
            :key="game.id"
            :label="game.name"
            :value="game.id"
          />
        </el-select>

        <el-select
          v-model="selectedCategory"
          placeholder="选择分类"
          clearable
          @change="handleFilter"
          class="filter-select"
        >
          <el-option
            v-for="category in categories"
            :key="category.id"
            :label="category.name"
            :value="category.id"
          />
        </el-select>

        <el-button @click="handleReset">重置筛选</el-button>
      </div>
    </div>

    <!-- 攻略列表 -->
    <div class="guides-content" v-loading="loading">
      <div v-if="guides.length > 0" class="guides-list">
        <div
          v-for="guide in guides"
          :key="guide.id"
          class="guide-card"
          @click="goToGuide(guide.id)"
        >
          <div class="guide-main">
            <div class="guide-header">
              <h3 class="guide-title">{{ guide.title }}</h3>
              <div class="guide-badges">
                <el-tag type="primary" size="small">{{ guide.gameName }}</el-tag>
                <el-tag v-if="guide.categoryName" type="success" size="small">
                  {{ guide.categoryName }}
                </el-tag>
              </div>
            </div>
            
            <p class="guide-content" v-html="renderMarkdown(guide.content)"></p>
            
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
              <div class="guide-meta">
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ guide.viewCount || 0 }}
                </span>
                <span class="meta-item">
                  <el-icon><Calendar /></el-icon>
                  {{ formatTime(guide.createdAt) }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="guide-action">
            <el-button type="primary" size="small" round>
              查看详情
            </el-button>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无攻略数据" />
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="pagination.total > 0"
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadGuides"
      @current-change="loadGuides"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, View, Calendar } from '@element-plus/icons-vue'
import { guideAPI } from '@/api/guide'
import { gameAPI } from '@/api/game'
import { categoryAPI } from '@/api/category'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const guides = ref([])
const games = ref([])
const categories = ref([])
const searchKeyword = ref('')
const selectedGame = ref(null)
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

const loadGuides = async () => {
  loading.value = true
  try {
    let data
    // 如果有搜索条件或筛选条件，使用搜索接口（仅已发布攻略）
    if (searchKeyword.value.trim() || selectedGame.value || selectedCategory.value) {
      const params = {
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      }
      if (searchKeyword.value.trim()) {
        params.keyword = searchKeyword.value
      }
      if (selectedGame.value) {
        params.gameId = selectedGame.value
      }
      if (selectedCategory.value) {
        params.categoryId = selectedCategory.value
      }
      data = await guideAPI.searchPublished(params)
    } else {
      // 否则使用列表接口（仅已发布攻略）
      data = await guideAPI.getPublishedList({
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      })
    }
    guides.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载攻略列表失败:', error)
  } finally {
    loading.value = false
  }
}

const loadGames = async () => {
  try {
    games.value = await gameAPI.getAll()
  } catch (error) {
    console.error('加载游戏列表失败:', error)
  }
}

const loadCategories = async () => {
  try {
    categories.value = await categoryAPI.getAll()
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadGuides()
}

const handleFilter = () => {
  pagination.pageNum = 1
  loadGuides()
}

const handleReset = () => {
  searchKeyword.value = ''
  selectedGame.value = null
  selectedCategory.value = null
  pagination.pageNum = 1
  loadGuides()
}

const goToGuide = (guideId) => {
  router.push(`/guide/${guideId}`)
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  // 从 URL 参数中读取 categoryId
  const categoryIdFromUrl = route.query.categoryId
  if (categoryIdFromUrl) {
    selectedCategory.value = Number(categoryIdFromUrl)
  }
  
  loadGuides()
  loadGames()
  loadCategories()
})
</script>

<style scoped>
.guides-list-page {
  width: 100%;
}

.page-header {
  background: white;
  border-radius: 16px;
  padding: 48px 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  text-align: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.page-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 12px;
}

.page-desc {
  font-size: 18px;
  opacity: 0.95;
}

.filter-section {
  background: white;
  border-radius: 16px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.filter-row {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-wrap: wrap;
}

.filter-row + .filter-row {
  margin-top: 16px;
}

.search-input {
  flex: 1;
  max-width: 600px;
}

.filter-select {
  width: 200px;
}

.guides-content {
  background: white;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  min-height: 400px;
}

.guides-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.guide-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
}

.guide-card:hover {
  background: white;
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateX(4px);
}

.guide-main {
  flex: 1;
  min-width: 0;
}

.guide-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 16px;
}

.guide-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.guide-badges {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.guide-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 16px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
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
  gap: 16px;
  flex-wrap: wrap;
}

.guide-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  flex: 1;
}

.guide-meta {
  display: flex;
  gap: 16px;
  align-items: center;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #999;
}

.guide-action {
  flex-shrink: 0;
}

.pagination {
  display: flex;
  justify-content: center;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}
</style>

