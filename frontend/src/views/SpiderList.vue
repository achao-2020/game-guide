<template>
  <div class="spider-list">
    <div class="panel">
      <div class="panel-header">
        <h2 class="panel-title">已爬取攻略</h2>
        <el-button :icon="Refresh" circle :loading="listLoading" @click="loadList" />
      </div>

      <el-table
        :data="savedList"
        v-loading="listLoading"
        stripe
        style="width: 100%"
        empty-text="暂无已爬取攻略"
      >
        <el-table-column type="index" label="#" width="55" />
        <el-table-column prop="title" label="标题" min-width="220" show-overflow-tooltip />
        <el-table-column label="来源" min-width="220" show-overflow-tooltip>
          <template #default="{ row }">
            <a :href="row.sourceUrl" target="_blank" class="table-link">{{ row.sourceUrl }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="爬取时间" width="175" show-overflow-tooltip />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" @click="handleDetail(row.id)">详情</el-button>
            <el-button size="small" type="danger" @click="handleDeleteConfirm(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next"
          @current-change="loadList"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <!-- 详情 Drawer -->
    <el-drawer
      v-model="drawerVisible"
      title="攻略详情"
      direction="rtl"
      size="60%"
      :before-close="handleDrawerClose"
    >
      <div v-if="detailLoading" class="detail-loading">
        <el-skeleton :rows="8" animated />
      </div>
      <div v-else-if="detail" class="detail-wrap">
        <div class="detail-actions">
          <el-button type="success" @click="handleSaveAsGuide">保存为攻略</el-button>
        </div>
        <h2 class="detail-title">{{ detail.title }}</h2>
        <div class="detail-meta">
          <span>爬取时间：{{ detail.createTime }}</span>
          <a :href="detail.sourceUrl" target="_blank" class="table-link">查看原文</a>
        </div>
        <divider />
        <div class="detail-content" v-html="detail.content" />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { spiderAPI } from '@/api/spider'

const router = useRouter()

const listLoading = ref(false)
const savedList = ref([])
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const drawerVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

async function loadList() {
  listLoading.value = true
  try {
    const data = await spiderAPI.list({ pageNum: pageNum.value, pageSize: pageSize.value })
    savedList.value = data.list || []
    total.value = data.total || 0
  } catch (e) {
    // 拦截器统一处理
  } finally {
    listLoading.value = false
  }
}

function handleSizeChange(size) {
  pageSize.value = size
  pageNum.value = 1
  loadList()
}

async function handleDelete(id) {
  try {
    await spiderAPI.deleteById(id)
    ElMessage.success('删除成功')
    loadList()
  } catch (e) {
    // 拦截器统一处理
  }
}

function handleDeleteConfirm(row) {
  ElMessageBox.confirm(
    `确定要删除「${row.title}」吗？此操作不可恢复。`,
    '删除确认',
    {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => handleDelete(row.id)).catch(() => {})
}

async function handleDetail(id) {
  drawerVisible.value = true
  detail.value = null
  detailLoading.value = true
  try {
    detail.value = await spiderAPI.getById(id)
  } catch (e) {
    // 拦截器统一处理
  } finally {
    detailLoading.value = false
  }
}

function handleDrawerClose() {
  drawerVisible.value = false
  detail.value = null
}

function handleSaveAsGuide() {
  if (!detail.value) return
  router.push({
    path: '/admin/guides/add',
    state: {
      prefillTitle: detail.value.title,
      prefillContent: detail.value.content
    }
  })
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.spider-list {
  max-width: 1100px;
  margin: 0 auto;
}

.panel {
  background: #fff;
  border-radius: 8px;
  padding: 24px 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.panel-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.table-link {
  color: #409eff;
  text-decoration: none;
  font-size: 12px;
}

.table-link:hover {
  text-decoration: underline;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.detail-loading {
  padding: 20px;
}

.detail-actions {
  margin-bottom: 16px;
}

.detail-wrap {
  padding: 0 4px;
}

.detail-title {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 12px 0;
  line-height: 1.4;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 12px;
  color: #909399;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-content {
  font-size: 14px;
  color: #333;
  line-height: 1.8;
}

.detail-content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 8px 0;
}

.detail-content :deep(p) {
  margin: 8px 0;
}

.detail-content :deep(h1),
.detail-content :deep(h2),
.detail-content :deep(h3) {
  margin: 16px 0 8px;
  color: #1a1a2e;
}
</style>
