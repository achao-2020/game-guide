<template>
  <div class="search-page">
    <!-- 搜索头部 -->
    <div class="search-header">
      <div class="search-bar">
        <el-input
          v-model="inputKeyword"
          placeholder="搜索游戏或攻略内容..."
          size="large"
          class="main-search-input"
          clearable
          @keyup.enter="doSearch"
        >
          <template #prefix>
            <el-icon class="search-icon"><Search /></el-icon>
          </template>
          <template #append>
            <el-button type="primary" @click="doSearch" :loading="guidesLoading || gamesLoading">
              搜索
            </el-button>
          </template>
        </el-input>
      </div>
      <div v-if="keyword" class="search-stats">
        关键词
        <span class="keyword-badge">{{ keyword }}</span>
        的搜索结果：找到 <strong>{{ totalGames }}</strong> 个游戏、<strong>{{ totalGuides }}</strong> 篇攻略
      </div>
    </div>

    <!-- 游戏搜索结果 -->
    <section class="results-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Grid /></el-icon>
          游戏结果
          <span class="count-badge">{{ totalGames }}</span>
        </h2>
      </div>

      <div v-loading="gamesLoading" class="results-content">
        <div v-if="games.length > 0" class="games-grid">
          <div
            v-for="game in games"
            :key="game.id"
            class="game-card"
            @click="goToGame(game.id)"
          >
            <div class="game-cover">
              <img :src="game.coverImage" :alt="game.name" />
              <div class="game-cover-overlay">
                <el-icon :size="28"><ArrowRight /></el-icon>
              </div>
            </div>
            <div class="game-info">
              <h3 class="game-name">{{ game.name }}</h3>
              <p class="game-desc">{{ game.description }}</p>
            </div>
          </div>
        </div>
        <div v-else-if="!gamesLoading && keyword" class="empty-state">
          <el-icon :size="48" class="empty-icon"><Grid /></el-icon>
          <p>未找到相关游戏</p>
        </div>
      </div>

      <el-pagination
        v-if="gamePagination.total > gamePagination.pageSize"
        v-model:current-page="gamePagination.pageNum"
        v-model:page-size="gamePagination.pageSize"
        :total="gamePagination.total"
        :page-sizes="[6, 12, 24]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadGames"
        @current-change="loadGames"
        class="pagination"
      />
    </section>

    <!-- 攻略全文搜索结果 -->
    <section class="results-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Document /></el-icon>
          攻略结果
          <span class="count-badge">{{ totalGuides }}</span>
        </h2>
        <div class="section-tip">
          <el-icon><InfoFilled /></el-icon>
          基于全文索引，支持内容关键词高亮
        </div>
      </div>

      <div v-loading="guidesLoading" class="results-content">
        <div v-if="guides.length > 0" class="guides-list">
          <div
            v-for="guide in guides"
            :key="guide.guideId"
            class="guide-card"
            @click="goToGuide(guide.guideId)"
          >
            <div class="guide-card-left">
              <div class="guide-game-tag">
                <el-icon><Grid /></el-icon>
                {{ guide.gameName || '未知游戏' }}
              </div>
              <h3 class="guide-title" v-html="highlightKeyword(guide.title)"></h3>
              <!-- ts_headline 高亮摘要 -->
              <p class="guide-headline" v-html="guide.headline || '暂无摘要'"></p>
            </div>
            <div class="guide-card-right">
              <span class="guide-time">{{ formatTime(guide.updatedAt) }}</span>
              <el-button type="primary" size="small" round>查看详情</el-button>
            </div>
          </div>
        </div>
        <div v-else-if="!guidesLoading && keyword" class="empty-state">
          <el-icon :size="48" class="empty-icon"><Document /></el-icon>
          <p>未找到相关攻略</p>
        </div>
      </div>

      <el-pagination
        v-if="guidePagination.total > guidePagination.pageSize"
        v-model:current-page="guidePagination.pageNum"
        v-model:page-size="guidePagination.pageSize"
        :total="guidePagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadGuides"
        @current-change="loadGuides"
        class="pagination"
      />
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Search, Grid, Document, ArrowRight, InfoFilled } from '@element-plus/icons-vue'
import { gameAPI } from '@/api/game'
import { guideAPI } from '@/api/guide'

const route  = useRoute()
const router = useRouter()

const keyword      = ref('')
const inputKeyword = ref('')
const gamesLoading  = ref(false)
const guidesLoading = ref(false)
const games  = ref([])
const guides = ref([])

const gamePagination = reactive({ pageNum: 1, pageSize: 6,  total: 0 })
const guidePagination = reactive({ pageNum: 1, pageSize: 10, total: 0 })

const totalGames  = computed(() => gamePagination.total)
const totalGuides = computed(() => guidePagination.total)

// ---- 搜索触发 ----
const doSearch = () => {
  const kw = inputKeyword.value.trim()
  if (!kw) return
  router.push({ path: '/search', query: { keyword: kw } })
}

// ---- 加载游戏 ----
const loadGames = async () => {
  if (!keyword.value) return
  gamesLoading.value = true
  try {
    const data = await gameAPI.search({
      keyword: keyword.value,
      pageNum: gamePagination.pageNum,
      pageSize: gamePagination.pageSize
    })
    games.value = data.list || []
    gamePagination.total = data.total || 0
  } catch (e) {
    console.error('搜索游戏失败:', e)
  } finally {
    gamesLoading.value = false
  }
}

// ---- 加载攻略全文搜索 ----
const loadGuides = async () => {
  if (!keyword.value) return
  guidesLoading.value = true
  try {
    const data = await guideAPI.fullTextSearch({
      keyword: keyword.value,
      pageNum: guidePagination.pageNum,
      pageSize: guidePagination.pageSize
    })
    guides.value = data.list || []
    guidePagination.total = data.total || 0
  } catch (e) {
    console.error('全文搜索攻略失败:', e)
  } finally {
    guidesLoading.value = false
  }
}

// ---- 关键词高亮（标题） ----
const highlightKeyword = (text) => {
  if (!text || !keyword.value) return text
  const escaped = keyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  const reg = new RegExp(`(${escaped})`, 'gi')
  return text.replace(reg, '<em>$1</em>')
}

// ---- 格式化时间 ----
const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

// ---- 跳转 ----
const goToGame  = (id) => router.push(`/game/${id}`)
const goToGuide = (id) => router.push(`/guide/${id}`)

// ---- 监听路由变化 ----
const runSearch = (kw) => {
  if (!kw) return
  keyword.value      = kw
  inputKeyword.value = kw
  gamePagination.pageNum  = 1
  guidePagination.pageNum = 1
  loadGames()
  loadGuides()
}

watch(() => route.query.keyword, (val) => { if (val) runSearch(val) })

onMounted(() => {
  const kw = route.query.keyword
  if (kw) runSearch(kw)
})
</script>

<style scoped>
.search-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* ---- 搜索头部 ---- */
.search-header {
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.search-bar {
  max-width: 700px;
  margin: 0 auto;
}

.main-search-input :deep(.el-input__wrapper) {
  border-radius: 12px 0 0 12px;
  box-shadow: 0 0 0 1px #dcdfe6;
  font-size: 16px;
}

.main-search-input :deep(.el-input-group__append) {
  border-radius: 0 12px 12px 0;
  background: #667eea;
  border-color: #667eea;
}

.main-search-input :deep(.el-input-group__append .el-button) {
  color: white;
  font-weight: 600;
  padding: 0 28px;
}

.search-stats {
  text-align: center;
  margin-top: 16px;
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.keyword-badge {
  display: inline-block;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 2px 12px;
  border-radius: 20px;
  font-weight: 600;
  margin: 0 4px;
  font-size: 14px;
}

/* ---- 结果区块 ---- */
.results-section {
  background: white;
  border-radius: 16px;
  padding: 28px 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f0f2f5;
}

.section-title {
  font-size: 20px;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: center;
  gap: 10px;
  margin: 0;
}

.section-title .el-icon {
  color: #667eea;
}

.count-badge {
  background: #667eea;
  color: white;
  font-size: 13px;
  font-weight: 600;
  padding: 2px 10px;
  border-radius: 20px;
  min-width: 28px;
  text-align: center;
}

.section-tip {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #909399;
}

/* ---- 游戏卡片 ---- */
.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 20px;
}

.game-card {
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.3s;
}

.game-card:hover {
  border-color: #667eea;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.18);
  transform: translateY(-4px);
}

.game-cover {
  position: relative;
  width: 100%;
  height: 160px;
  overflow: hidden;
  background: #f0f2f5;
}

.game-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.game-card:hover .game-cover img {
  transform: scale(1.08);
}

.game-cover-overlay {
  position: absolute;
  inset: 0;
  background: rgba(102, 126, 234, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  opacity: 0;
  transition: opacity 0.3s;
}

.game-card:hover .game-cover-overlay {
  opacity: 1;
}

.game-info {
  padding: 14px 16px;
}

.game-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.game-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 42px;
}

/* ---- 攻略卡片 ---- */
.guides-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.guide-card {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
  padding: 20px 24px;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  cursor: pointer;
  transition: all 0.25s;
  background: #fafbfd;
}

.guide-card:hover {
  background: white;
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.14);
  transform: translateX(4px);
}

.guide-card-left {
  flex: 1;
  min-width: 0;
}

.guide-game-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  padding: 3px 10px;
  border-radius: 20px;
  margin-bottom: 10px;
  font-weight: 500;
}

.guide-title {
  font-size: 17px;
  font-weight: 600;
  color: #222;
  margin: 0 0 10px;
  line-height: 1.4;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 标题关键词高亮 */
.guide-title :deep(em) {
  font-style: normal;
  color: #667eea;
  font-weight: 700;
  background: rgba(102, 126, 234, 0.1);
  padding: 0 2px;
  border-radius: 3px;
}

.guide-headline {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* ts_headline 高亮 em 标签 */
.guide-headline :deep(em) {
  font-style: normal;
  color: #e6562a;
  font-weight: 700;
  background: rgba(230, 86, 42, 0.08);
  padding: 0 2px;
  border-radius: 3px;
}

.guide-card-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  flex-shrink: 0;
}

.guide-time {
  font-size: 12px;
  color: #aaa;
  white-space: nowrap;
}

/* ---- 空状态 ---- */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 0;
  color: #c0c4cc;
  gap: 12px;
  font-size: 15px;
}

.empty-icon {
  opacity: 0.4;
}

/* ---- 分页 ---- */
.pagination {
  margin-top: 24px;
  display: flex;
  justify-content: center;
}

.results-content {
  min-height: 120px;
}
</style>
