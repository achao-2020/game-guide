<template>
  <div class="search-page">
    <div class="search-header">
      <h1 class="search-title">
        搜索结果：<span class="keyword">{{ keyword }}</span>
      </h1>
      <p class="search-stats">
        找到 {{ totalGames }} 个游戏，{{ totalGuides }} 个攻略
      </p>
    </div>

    <!-- 游戏搜索结果 -->
    <section class="results-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Grid /></el-icon>
          游戏结果
        </h2>
      </div>
      
      <div v-loading="gamesLoading" class="results-content">
        <div v-if="games.length > 0" class="games-grid">
          <div
            v-for="game in games"
            :key="game.id"
            class="game-card"
          >
            <div class="game-cover">
              <img :src="game.coverImage" :alt="game.name" />
            </div>
            <div class="game-info">
              <h3 class="game-name">{{ game.name }}</h3>
              <p class="game-desc">{{ game.description }}</p>
              <el-button type="primary" size="small" @click="goToGame(game.id)">
                查看详情
              </el-button>
            </div>
          </div>
        </div>
        <el-empty v-else description="未找到相关游戏" />
      </div>

      <el-pagination
        v-if="gamePagination.total > 0"
        v-model:current-page="gamePagination.pageNum"
        v-model:page-size="gamePagination.pageSize"
        :total="gamePagination.total"
        :page-sizes="[6, 12, 24]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadGames"
        @current-change="loadGames"
        style="margin-top: 24px; justify-content: center"
      />
    </section>

    <!-- 攻略搜索结果 -->
    <section class="results-section">
      <div class="section-header">
        <h2 class="section-title">
          <el-icon><Document /></el-icon>
          攻略结果
        </h2>
      </div>
      
      <div v-loading="guidesLoading" class="results-content">
        <div v-if="guides.length > 0" class="guides-list">
          <div
            v-for="guide in guides"
            :key="guide.id"
            class="guide-card"
          >
            <div class="guide-header">
              <h3 class="guide-title">{{ guide.title }}</h3>
              <el-tag type="info" size="small">{{ guide.gameName }}</el-tag>
            </div>
            <p class="guide-content">{{ guide.content }}</p>
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
              <div class="guide-actions">
                <span class="guide-time">{{ formatTime(guide.createdAt) }}</span>
                <el-button type="primary" size="small" @click="goToGuide(guide.id)">
                  查看详情
                </el-button>
              </div>
            </div>
          </div>
        </div>
        <el-empty v-else description="未找到相关攻略" />
      </div>

      <el-pagination
        v-if="guidePagination.total > 0"
        v-model:current-page="guidePagination.pageNum"
        v-model:page-size="guidePagination.pageSize"
        :total="guidePagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadGuides"
        @current-change="loadGuides"
        style="margin-top: 24px; justify-content: center"
      />
    </section>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Grid, Document } from '@element-plus/icons-vue'
import { gameAPI } from '@/api/game'
import { guideAPI } from '@/api/guide'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const gamesLoading = ref(false)
const guidesLoading = ref(false)
const games = ref([])
const guides = ref([])

const gamePagination = reactive({
  pageNum: 1,
  pageSize: 6,
  total: 0
})

const guidePagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const totalGames = computed(() => gamePagination.total)
const totalGuides = computed(() => guidePagination.total)

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
  } catch (error) {
    console.error('搜索游戏失败:', error)
  } finally {
    gamesLoading.value = false
  }
}

const loadGuides = async () => {
  if (!keyword.value) return
  
  guidesLoading.value = true
  try {
    const data = await guideAPI.search({
      keyword: keyword.value,
      pageNum: guidePagination.pageNum,
      pageSize: guidePagination.pageSize
    })
    guides.value = data.list || []
    guidePagination.total = data.total || 0
  } catch (error) {
    console.error('搜索攻略失败:', error)
  } finally {
    guidesLoading.value = false
  }
}

const goToGame = (id) => {
  router.push(`/game/${id}`)
}

const goToGuide = (id) => {
  router.push(`/guide/${id}`)
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

// 监听路由变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    gamePagination.pageNum = 1
    guidePagination.pageNum = 1
    loadGames()
    loadGuides()
  }
}, { immediate: true })

onMounted(() => {
  keyword.value = route.query.keyword || ''
  if (keyword.value) {
    loadGames()
    loadGuides()
  }
})
</script>

<style scoped>
.search-page {
  width: 100%;
}

.search-header {
  background: white;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  text-align: center;
}

.search-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 12px;
}

.keyword {
  color: #667eea;
}

.search-stats {
  font-size: 16px;
  color: #666;
}

.results-section {
  background: white;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-header {
  margin-bottom: 24px;
}

.section-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  display: flex;
  align-items: center;
  gap: 12px;
}

.results-content {
  min-height: 200px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 24px;
}

.game-card {
  background: #fafafa;
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.game-card:hover {
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
  transform: translateY(-4px);
}

.game-cover {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #e8e8e8;
}

.game-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.game-card:hover .game-cover img {
  transform: scale(1.1);
}

.game-info {
  padding: 16px;
}

.game-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.game-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 44px;
}

.guides-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.guide-card {
  background: #fafafa;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.guide-card:hover {
  background: white;
  border-color: #667eea;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.15);
}

.guide-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
}

.guide-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.guide-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
  margin-bottom: 12px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.guide-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.guide-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  flex: 1;
}

.guide-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.guide-time {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}
</style>

