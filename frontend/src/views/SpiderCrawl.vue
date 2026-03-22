<template>
  <div class="spider-crawl">
    <div class="panel">
      <h2 class="panel-title">攻略爬取</h2>

      <el-form :model="form" label-width="140px" class="crawl-form">
        <el-form-item label="爬取 URL">
          <el-input
            v-model="form.site"
            placeholder="请输入要爬取的页面 URL，例如 https://www.vgover.com/post/xxxxx"
            clearable
          />
        </el-form-item>
        <el-form-item label="标题 Selector">
          <el-input
            v-model="form.titleSelector"
            placeholder="CSS 选择器，例如 head > title"
            clearable
          />
        </el-form-item>
        <el-form-item label="内容 Selector">
          <el-input
            v-model="form.contentSelector"
            placeholder="CSS 选择器，例如 article > div.content"
            clearable
          />
        </el-form-item>
        <el-form-item>
          <el-button
            type="primary"
            :loading="loading"
            :disabled="!form.site || !form.titleSelector || !form.contentSelector"
            @click="handlePreview"
          >
            {{ loading ? '爬取中...' : '开始爬取' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 爬取结果 -->
    <div v-if="results.length > 0" class="results-panel">
      <div class="results-header">
        <span class="results-title">爬取结果 <em>{{ results.length }}</em> 条</span>
        <div class="results-actions">
          <el-button type="success" :loading="saving" @click="handleSaveAll">
            全部保存
          </el-button>
        </div>
      </div>

      <div
        v-for="(item, index) in results"
        :key="index"
        class="result-card"
        :class="{ deleted: item._deleted, saved: item._saved }"
      >
        <div class="result-card-header">
          <span class="result-index">#{{ index + 1 }}</span>
          <span v-if="item._saved" class="badge badge-saved">已保存</span>
          <span v-else-if="item._deleted" class="badge badge-deleted">已删除</span>
          <div class="result-card-actions" v-if="!item._deleted && !item._saved">
            <el-button size="small" type="success" @click="handleSaveOne(index)">
              保存
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(index)">
              删除
            </el-button>
          </div>
        </div>

        <div class="result-title">{{ item.title }}</div>
        <div class="result-url">
          <a :href="item.sourceUrl" target="_blank">{{ item.sourceUrl }}</a>
        </div>
        <div class="result-content" v-html="item.content" />
      </div>
    </div>

    <div v-else-if="crawled && !loading" class="empty-tip">
      <el-empty description="未爬取到任何内容，请检查 URL 或 Selector 是否正确" />
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { spiderAPI } from '@/api/spider'

const form = reactive({
  site: '',
  titleSelector: 'head > title',
  contentSelector: 'body > div > div.vg-main.clearfix > div.vg-content > article > div.ss-html-container.J-photoSwiper'
})

const loading = ref(false)
const saving = ref(false)
const crawled = ref(false)
const results = ref([])

async function handlePreview() {
  if (!form.site || !form.titleSelector || !form.contentSelector) {
    ElMessage.warning('请填写完整的 URL 和 Selector')
    return
  }
  loading.value = true
  crawled.value = false
  results.value = []
  try {
    const data = await spiderAPI.preview({
      site: form.site,
      titleSelector: form.titleSelector,
      contentSelector: form.contentSelector
    })
    results.value = (data || []).map(item => ({ ...item, _deleted: false, _saved: false }))
    crawled.value = true
    if (results.value.length === 0) {
      ElMessage.warning('未爬取到内容，请检查 Selector')
    } else {
      ElMessage.success(`爬取成功，共 ${results.value.length} 条`)
    }
  } catch (e) {
    // 错误已由 request.js 拦截器统一处理
  } finally {
    loading.value = false
  }
}

function handleDelete(index) {
  results.value[index]._deleted = true
}

async function handleSaveOne(index) {
  const item = results.value[index]
  try {
    await spiderAPI.save([item])
    results.value[index]._saved = true
    ElMessage.success('保存成功')
  } catch (e) {
    // 错误已由 request.js 拦截器统一处理
  }
}

async function handleSaveAll() {
  const toSave = results.value.filter(item => !item._deleted && !item._saved)
  if (toSave.length === 0) {
    ElMessage.warning('没有可保存的条目')
    return
  }
  saving.value = true
  try {
    await spiderAPI.save(toSave)
    results.value.forEach(item => {
      if (!item._deleted && !item._saved) item._saved = true
    })
    ElMessage.success(`已保存 ${toSave.length} 条`)
  } catch (e) {
    // 错误已由 request.js 拦截器统一处理
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.spider-crawl {
  max-width: 960px;
  margin: 0 auto;
}

.panel {
  background: #fff;
  border-radius: 8px;
  padding: 28px 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0 0 24px 0;
  padding-bottom: 14px;
  border-bottom: 2px solid #409eff22;
}

.crawl-form {
  max-width: 760px;
}

.results-panel {
  background: #fff;
  border-radius: 8px;
  padding: 24px 32px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.results-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}

.results-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.results-title em {
  color: #409eff;
  font-style: normal;
  margin: 0 2px;
}

.result-card {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  padding: 16px 20px;
  margin-bottom: 16px;
  transition: opacity 0.3s, border-color 0.3s;
}

.result-card.deleted {
  opacity: 0.35;
  border-color: #f56c6c44;
  background: #fff5f5;
}

.result-card.saved {
  border-color: #67c23a44;
  background: #f0f9eb;
}

.result-card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.result-index {
  font-size: 12px;
  font-weight: 700;
  color: #909399;
  min-width: 28px;
}

.badge {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 3px;
  font-weight: 600;
}

.badge-saved {
  background: #67c23a22;
  color: #67c23a;
  border: 1px solid #67c23a44;
}

.badge-deleted {
  background: #f56c6c22;
  color: #f56c6c;
  border: 1px solid #f56c6c44;
}

.result-card-actions {
  margin-left: auto;
  display: flex;
  gap: 8px;
}

.result-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 4px;
}

.result-url {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
}

.result-url a {
  color: #409eff;
  text-decoration: none;
}

.result-url a:hover {
  text-decoration: underline;
}

.result-content {
  font-size: 13px;
  color: #555;
  line-height: 1.7;
  max-height: 600px;
  overflow-y: auto;
  border-top: 1px solid #f0f0f0;
  padding-top: 10px;
}

.empty-tip {
  background: #fff;
  border-radius: 8px;
  padding: 40px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  text-align: center;
}
</style>
