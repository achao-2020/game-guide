<template>
  <div class="dashboard">
    <el-card class="welcome-card">
      <h2>欢迎使用游戏攻略知识库系统</h2>
      <p>当前用户：{{ userStore.username }}</p>
      <p>用户角色：{{ userStore.role }}</p>
      <el-divider />
      <div class="stats">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-card">
              <el-icon class="stat-icon" color="#409EFF"><Grid /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.games }}</div>
                <div class="stat-label">游戏数量</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <el-icon class="stat-icon" color="#67C23A"><Document /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.guides }}</div>
                <div class="stat-label">攻略数量</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <el-icon class="stat-icon" color="#E6A23C"><Folder /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.categories }}</div>
                <div class="stat-label">分类数量</div>
              </div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-card">
              <el-icon class="stat-icon" color="#F56C6C"><PriceTag /></el-icon>
              <div class="stat-info">
                <div class="stat-value">{{ stats.tags }}</div>
                <div class="stat-label">标签数量</div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { gameAPI } from '@/api/game'
import { guideAPI } from '@/api/guide'
import { categoryAPI } from '@/api/category'
import { tagAPI } from '@/api/tag'

const userStore = useUserStore()

const stats = ref({
  games: 0,
  guides: 0,
  categories: 0,
  tags: 0
})

const loadStats = async () => {
  try {
    const [games, guides, categories, tags] = await Promise.all([
      gameAPI.getAll(),
      guideAPI.getAll(),
      categoryAPI.getAll(),
      tagAPI.getAll()
    ])
    
    stats.value = {
      games: games?.length || 0,
      guides: guides?.length || 0,
      categories: categories?.length || 0,
      tags: tags?.length || 0
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-card h2 {
  margin: 0 0 20px 0;
  color: #333;
}

.welcome-card p {
  margin: 10px 0;
  color: #666;
  font-size: 16px;
}

.stats {
  margin-top: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 8px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 48px;
  margin-right: 20px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}
</style>



