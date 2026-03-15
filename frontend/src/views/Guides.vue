<template>
  <div class="guides-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>攻略列表</span>
          <div class="header-actions">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索攻略标题"
              style="width: 300px; margin-right: 10px"
              clearable
              @clear="handleSearch"
            >
              <template #append>
                <el-button @click="handleSearch">
                  <el-icon><Search /></el-icon>
                </el-button>
              </template>
            </el-input>
            <el-button v-if="userStore.isAdmin" type="primary" @click="handleAdd">
              <el-icon><Plus /></el-icon>
              新增攻略
            </el-button>
          </div>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="gameName" label="游戏" min-width="120" />
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="tags" label="标签" min-width="200">
          <template #default="{ row }">
            <span v-if="!row.tags || row.tags.length === 0" class="text-muted">—</span>
            <el-tag
              v-for="tag in row.tags"
              :key="tag.id"
              size="small"
              style="margin-right: 4px; margin-bottom: 2px"
            >
              {{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" min-width="180">
          <template #default="{ row }">
            {{ formatTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column v-if="userStore.isAdmin" label="操作" min-width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { guideAPI } from '@/api/guide'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const searchKeyword = ref('')

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const formatTime = (time) => {
  if (!time) return ''
  return new Date(time).toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadData = async () => {
  loading.value = true
  try {
    const data = await guideAPI.getList({
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  pagination.pageNum = 1
  if (!searchKeyword.value) {
    loadData()
    return
  }
  loading.value = true
  try {
    const data = await guideAPI.search({
      keyword: searchKeyword.value,
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize
    })
    tableData.value = data.list || []
    pagination.total = data.total || 0
  } catch (error) {
    console.error('搜索失败:', error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  router.push('/admin/guides/add')
}

const handleEdit = (row) => {
  router.push(`/admin/guides/edit/${row.id}`)
}

const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除攻略「${row.title}」吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    type: 'warning'
    }
  ).then(async () => {
    try {
      await guideAPI.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {
    // 取消删除
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.guides-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  align-items: center;
}

.text-muted {
  color: #c0c4cc;
}
</style>
