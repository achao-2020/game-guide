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

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip />
        <el-table-column prop="gameName" label="游戏" width="120" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="tags" label="标签" width="200">
          <template #default="{ row }">
            <el-tag
              v-for="tag in row.tags"
              :key="tag.id"
              size="small"
              style="margin-right: 5px"
            >
              {{ tag.name }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="viewCount" label="浏览量" width="100" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column v-if="userStore.isAdmin" label="操作" width="180" fixed="right">
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="游戏" prop="gameId">
          <el-select v-model="form.gameId" placeholder="请选择游戏" style="width: 100%">
            <el-option
              v-for="game in games"
              :key="game.id"
              :label="game.name"
              :value="game.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入攻略标题" />
        </el-form-item>
        <el-form-item label="标签" prop="tagIds">
          <el-select
            v-model="form.tagIds"
            multiple
            placeholder="请选择标签"
            style="width: 100%"
          >
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input
            v-model="form.content"
            type="textarea"
            :rows="10"
            placeholder="请输入攻略内容（支持Markdown）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { guideAPI } from '@/api/guide'
import { gameAPI } from '@/api/game'
import { categoryAPI } from '@/api/category'
import { tagAPI } from '@/api/tag'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增攻略')
const formRef = ref(null)
const tableData = ref([])
const searchKeyword = ref('')

const games = ref([])
const categories = ref([])
const tags = ref([])

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const form = reactive({
  id: null,
  gameId: null,
  categoryId: null,
  title: '',
  content: '',
  tagIds: []
})

const rules = {
  gameId: [{ required: true, message: '请选择游戏', trigger: 'change' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  title: [{ required: true, message: '请输入攻略标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入攻略内容', trigger: 'blur' }]
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

const loadOptions = async () => {
  try {
    const [gamesData, categoriesData, tagsData] = await Promise.all([
      gameAPI.getAll(),
      categoryAPI.getAll(),
      tagAPI.getAll()
    ])
    games.value = gamesData || []
    categories.value = categoriesData || []
    tags.value = tagsData || []
  } catch (error) {
    console.error('加载选项失败:', error)
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增攻略'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑攻略'
  form.id = row.id
  form.gameId = row.gameId
  form.categoryId = row.categoryId
  form.title = row.title
  form.content = row.content
  form.tagIds = row.tags?.map(tag => tag.id) || []
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该攻略吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await guideAPI.delete(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitLoading.value = true
      try {
        const data = {
          gameId: form.gameId,
          categoryId: form.categoryId,
          title: form.title,
          content: form.content,
          tagIds: form.tagIds
        }
        
        if (form.id) {
          await guideAPI.update(form.id, data)
          ElMessage.success('更新成功')
        } else {
          await guideAPI.create(data)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        loadData()
      } catch (error) {
        console.error('提交失败:', error)
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const resetForm = () => {
  form.id = null
  form.gameId = null
  form.categoryId = null
  form.title = ''
  form.content = ''
  form.tagIds = []
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

onMounted(() => {
  loadData()
  loadOptions()
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
</style>



