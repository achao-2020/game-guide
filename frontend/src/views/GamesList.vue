<template>
  <div class="games-list-page">
    <div class="page-header">
      <h1 class="page-title">游戏列表</h1>
      <p class="page-desc">探索精彩的游戏世界</p>
    </div>

    <!-- 搜索和筛选 -->
    <div class="filter-section">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索游戏名称或描述"
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

    <!-- 游戏列表 -->
    <div class="games-content" v-loading="loading">
      <div v-if="games.length > 0" class="games-grid">
        <div
          v-for="game in games"
          :key="game.id"
          class="game-card"
          @click="goToGame(game.id)"
        >
          <div class="game-cover">
            <img :src="game.coverImage" :alt="game.name" />
            <div class="game-overlay">
              <el-button type="primary" size="large" round>
                查看详情
              </el-button>
            </div>
          </div>
          <div class="game-info">
            <h3 class="game-name">{{ game.name }}</h3>
            <p class="game-desc">{{ game.description }}</p>
          </div>
        </div>
      </div>
      <el-empty v-else description="暂无游戏数据" />
    </div>

    <!-- 分页 -->
    <el-pagination
      v-if="pagination.total > 0"
      v-model:current-page="pagination.pageNum"
      v-model:page-size="pagination.pageSize"
      :total="pagination.total"
      :page-sizes="[12, 24, 48]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadGames"
      @current-change="loadGames"
      class="pagination"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { gameAPI } from '@/api/game'

const router = useRouter()

const loading = ref(false)
const games = ref([])
const searchKeyword = ref('')

const pagination = reactive({
  pageNum: 1,
  pageSize: 12,
  total: 0
})

const loadGames = async () => {
  loading.value = true
  try {
    let data
    if (searchKeyword.value.trim()) {
      // 使用搜索接口
      data = await gameAPI.search({
        keyword: searchKeyword.value,
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      })
    } else {
      // 使用列表接口
      data = await gameAPI.getList({
        pageNum: pagination.pageNum,
        pageSize: pagination.pageSize
      })
    }
    games.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载游戏列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.pageNum = 1
  loadGames()
}

const goToGame = (id) => {
  router.push(`/game/${id}`)
}

onMounted(() => {
  loadGames()
})
</script>

<style scoped>
.games-list-page {
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

.search-input {
  max-width: 600px;
}

.games-content {
  background: white;
  border-radius: 16px;
  padding: 32px;
  margin-bottom: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  min-height: 400px;
}

.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.game-card {
  background: #fafafa;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid transparent;
}

.game-card:hover {
  border-color: #667eea;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.2);
  transform: translateY(-8px);
}

.game-cover {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #e8e8e8;
  position: relative;
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

.game-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.game-card:hover .game-overlay {
  opacity: 1;
}

.game-info {
  padding: 20px;
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
  min-height: 44px;
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

