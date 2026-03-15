<template>
  <div class="home-page">
    <!-- 轮播横幅 -->
    <section class="hero-section">
      <el-carousel height="400px" :interval="5000">
        <el-carousel-item v-for="game in featuredGames" :key="game.id">
          <div class="hero-item" :style="{ backgroundImage: `url(${game.coverImage})` }">
            <div class="hero-content">
              <h1 class="hero-title">{{ game.name }}</h1>
              <p class="hero-desc">{{ game.description }}</p>
              <el-button type="primary" size="large" @click="goToGame(game.id)">
                查看攻略
              </el-button>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 热门游戏 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">热门游戏</h2>
        <el-button text @click="router.push('/games-list')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="games-grid" v-loading="gamesLoading">
        <div
          v-for="game in games"
          :key="game.id"
          class="game-card"
          @click="goToGame(game.id)"
        >
          <div class="game-cover">
            <img :src="game.coverImage" :alt="game.name" />
          </div>
          <div class="game-info">
            <h3 class="game-name">{{ game.name }}</h3>
            <p class="game-desc">{{ game.description }}</p>
          </div>
        </div>
      </div>
    </section>

    <!-- 最新攻略 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">最新攻略</h2>
        <el-button text @click="router.push('/guides-list')">
          查看更多 <el-icon><ArrowRight /></el-icon>
        </el-button>
      </div>
      <div class="guides-list" v-loading="guidesLoading">
        <div
          v-for="guide in guides"
          :key="guide.id"
          class="guide-card"
          @click="goToGuide(guide.id)"
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
            <span class="guide-time">{{ formatTime(guide.createdAt) }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 分类导航 -->
    <section class="section">
      <div class="section-header">
        <h2 class="section-title">攻略分类</h2>
      </div>
      <div class="categories-grid" v-loading="categoriesLoading">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-card"
          @click="goToCategory(category.id)"
        >
          <el-icon :size="32" class="category-icon"><Folder /></el-icon>
          <h3 class="category-name">{{ category.name }}</h3>
          <p class="category-desc">{{ category.description }}</p>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Folder } from '@element-plus/icons-vue'
import { gameAPI } from '@/api/game'
import { guideAPI } from '@/api/guide'
import { categoryAPI } from '@/api/category'

const router = useRouter()

const games = ref([])
const guides = ref([])
const categories = ref([])
const featuredGames = ref([])

const gamesLoading = ref(false)
const guidesLoading = ref(false)
const categoriesLoading = ref(false)

const loadGames = async () => {
  gamesLoading.value = true
  try {
    const data = await gameAPI.getList({ pageNum: 1, pageSize: 8 })
    games.value = data.list || []
    featuredGames.value = (data.list || []).slice(0, 3)
  } catch (error) {
    console.error('加载游戏失败:', error)
  } finally {
    gamesLoading.value = false
  }
}

const loadGuides = async () => {
  guidesLoading.value = true
  try {
    // 首页只展示已发布攻略
    const data = await guideAPI.getPublishedList({ pageNum: 1, pageSize: 6 })
    guides.value = data.list || []
  } catch (error) {
    console.error('加载攻略失败:', error)
  } finally {
    guidesLoading.value = false
  }
}

const loadCategories = async () => {
  categoriesLoading.value = true
  try {
    const data = await categoryAPI.getAll()
    categories.value = data || []
  } catch (error) {
    console.error('加载分类失败:', error)
  } finally {
    categoriesLoading.value = false
  }
}

const goToGame = (id) => {
  router.push(`/game/${id}`)
}

const goToGuide = (guideId) => {
  router.push(`/guide/${guideId}`)
}

const goToCategory = (id) => {
  router.push(`/guides-list?categoryId=${id}`)
}

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadGames()
  loadGuides()
  loadCategories()
})
</script>

<style scoped>
.home-page {
  width: 100%;
}

.hero-section {
  margin-bottom: 48px;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
}

.hero-item {
  width: 100%;
  height: 400px;
  background-size: cover;
  background-position: center;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, rgba(0, 0, 0, 0.3), rgba(0, 0, 0, 0.6));
}

.hero-content {
  position: relative;
  z-index: 1;
  text-align: center;
  color: white;
  max-width: 600px;
  padding: 0 24px;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 16px;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.hero-desc {
  font-size: 18px;
  margin-bottom: 32px;
  opacity: 0.95;
  text-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.section {
  margin-bottom: 48px;
  background: white;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  position: relative;
  padding-left: 16px;
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.game-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e8e8e8;
}

.game-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 32px rgba(102, 126, 234, 0.2);
  border-color: #667eea;
}

.game-cover {
  width: 100%;
  height: 180px;
  overflow: hidden;
  background: #f5f5f5;
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
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
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
  cursor: pointer;
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
}

.guide-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 12px;
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

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.category-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 24px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  color: white;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
}

.category-icon {
  margin-bottom: 12px;
  opacity: 0.9;
}

.category-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.category-desc {
  font-size: 14px;
  opacity: 0.9;
  line-height: 1.6;
}
</style>

