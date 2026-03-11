<template>
  <div class="guide-edit-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑攻略' : '新增攻略' }}</span>
          <div class="header-actions">
            <el-button @click="handleCancel">取消</el-button>
            <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
              保存
            </el-button>
          </div>
        </div>
      </template>

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
          <div class="markdown-editor">
            <el-tabs v-model="activeTab" class="editor-tabs">
              <el-tab-pane label="Markdown编辑" name="edit">
                <el-input
                  v-model="form.content"
                  type="textarea"
                  :rows="20"
                  placeholder="请输入攻略内容（支持Markdown）"
                  class="markdown-textarea"
                />
              </el-tab-pane>
              <el-tab-pane label="富文本编辑" name="richtext">
                <div class="richtext-editor-wrapper">
                  <div ref="quillEditor" class="quill-editor"></div>
                  <div class="editor-actions">
                    <el-button type="primary" size="small" @click="convertToMarkdown">
                      转换为Markdown
                    </el-button>
                    <el-button size="small" @click="clearRichText">
                      清空
                    </el-button>
                  </div>
                </div>
              </el-tab-pane>
              <el-tab-pane label="预览" name="preview">
                <div class="markdown-preview" v-html="renderedContent"></div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, nextTick, watch, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElLoading } from 'element-plus'
import { guideAPI } from '@/api/guide'
import { gameAPI } from '@/api/game'
import { categoryAPI } from '@/api/category'
import { tagAPI } from '@/api/tag'
import { fileAPI } from '@/api/file'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import Quill from 'quill'
import 'quill/dist/quill.snow.css'
import TurndownService from 'turndown'

const route = useRoute()
const router = useRouter()
const submitLoading = ref(false)
const formRef = ref(null)
const activeTab = ref('edit')
const quillEditor = ref(null)
let quillInstance = null
const turndownService = new TurndownService({
  headingStyle: 'atx',
  codeBlockStyle: 'fenced'
})

// 用于将 Markdown 转换为 HTML 的临时容器
const markdownToHtml = (markdown) => {
  if (!markdown) return ''
  return marked(markdown)
}

const games = ref([])
const categories = ref([])
const tags = ref([])

const isEdit = computed(() => !!route.params.id)

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

const renderedContent = computed(() => {
  if (!form.content) return '<p class="empty-tip">暂无内容</p>'
  return marked(form.content)
})

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
    ElMessage.error('加载选项失败')
  }
}

const loadGuideData = async () => {
  if (!route.params.id) return
  
  try {
    const data = await guideAPI.getById(route.params.id)
    form.id = data.id
    form.gameId = data.gameId
    form.categoryId = data.categoryId
    form.title = data.title
    form.content = data.content
    form.tagIds = data.tags?.map(tag => tag.id) || []
  } catch (error) {
    console.error('加载攻略数据失败:', error)
    ElMessage.error('加载攻略数据失败')
  }
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
        
        if (isEdit.value) {
          await guideAPI.update(route.params.id, data)
          ElMessage.success('更新成功')
        } else {
          await guideAPI.create(data)
          ElMessage.success('创建成功')
        }
        router.push('/admin/guides')
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      } finally {
        submitLoading.value = false
      }
    }
  })
}

const handleCancel = () => {
  router.push('/admin/guides')
}

const uploadImage = async (file) => {
  try {
    const response = await fileAPI.upload(file)
    return response.url || response.data?.url || response
  } catch (error) {
    console.error('图片上传失败:', error)
    ElMessage.error('图片上传失败')
    throw error
  }
}

const initQuillEditor = () => {
  if (quillEditor.value && !quillInstance) {
    quillInstance = new Quill(quillEditor.value, {
      theme: 'snow',
      modules: {
        toolbar: {
          container: [
            [{ 'header': [1, 2, 3, 4, 5, 6, false] }],
            ['bold', 'italic', 'underline', 'strike'],
            [{ 'list': 'ordered'}, { 'list': 'bullet' }],
            [{ 'indent': '-1'}, { 'indent': '+1' }],
            [{ 'color': [] }, { 'background': [] }],
            [{ 'align': [] }],
            ['blockquote', 'code-block'],
            ['link', 'image'],
            ['clean']
          ],
          handlers: {
            image: imageHandler
          }
        }
      },
      placeholder: '请输入攻略内容（支持复制粘贴图片）...'
    })
    
    // 如果是编辑模式且有内容，将 Markdown 转换为 HTML 加载到编辑器
    if (isEdit.value && form.content) {
      const html = markdownToHtml(form.content)
      quillInstance.root.innerHTML = html
    }
    
    // 监听 text-change 事件，检测粘贴的 Base64 图片
    quillInstance.on('text-change', (delta, oldDelta, source) => {
      if (source === 'user') {
        handleQuillChange(delta, oldDelta)
      }
    })
  }
}

// 处理 Quill 编辑器的变化，检测粘贴的 Base64 图片
const handleQuillChange = async (delta, oldDelta) => {
  // 检查是否有新增的图片
  const newOps = delta.ops
  const oldOps = oldDelta.ops
  
  // 简单的检测：如果新增了图片操作，检查是否是 Base64
  for (let i = 0; i < newOps.length; i++) {
    const op = newOps[i]
    if (op.insert && typeof op.insert === 'object' && op.insert.image) {
      const imageUrl = op.insert.image
      // 如果是 Base64 格式，上传它
      if (isBase64Url(imageUrl)) {
        // 延迟处理，避免干扰编辑器状态
        setTimeout(async () => {
          try {
            const newUrl = await uploadThirdPartyImage(imageUrl)
            // 替换编辑器中的图片 URL
            const html = quillInstance.root.innerHTML
            const newHtml = html.split(`src="${imageUrl}"`).join(`src="${newUrl}"`)
            quillInstance.root.innerHTML = newHtml
          } catch (error) {
            console.error('上传粘贴的图片失败:', error)
          }
        }, 100)
      }
    }
  }
}

// 自定义图片上传处理
const imageHandler = () => {
  const input = document.createElement('input')
  input.setAttribute('type', 'file')
  input.setAttribute('accept', 'image/*')
  input.click()
  
  input.onchange = async () => {
    const file = input.files[0]
    if (file) {
      await handleImageUpload(file)
    }
  }
}

// 处理图片上传
const handleImageUpload = async (file) => {
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('只能上传图片文件')
    return
  }
  
  // 验证文件大小（限制为5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }
  
  ElMessage.info('图片上传中...')
  
  try {
    const imageUrl = await uploadImage(file)
    
    // 获取当前光标位置
    const range = quillInstance.getSelection(true)
    
    // 插入图片
    quillInstance.insertEmbed(range.index, 'image', imageUrl)
    
    // 移动光标到图片后面
    quillInstance.setSelection(range.index + 1)
    
    ElMessage.success('图片上传成功')
  } catch (error) {
    console.error('图片上传失败:', error)
  }
}

// 处理粘贴事件（已移除，改用 text-change 事件）

// 检查 URL 是否是 Base64 格式
const isBase64Url = (url) => {
  return url.startsWith('data:image/')
}

// 将 Base64 数据 URL 转换为 File 对象
const base64ToFile = (dataUrl, fileName = 'image.png') => {
  // 提取 MIME 类型
  const mimeMatch = dataUrl.match(/data:([^;]+)/)
  const mimeType = mimeMatch ? mimeMatch[1] : 'image/png'
  
  // 提取 Base64 数据
  const base64Data = dataUrl.split(',')[1]
  
  // 将 Base64 转换为二进制数据
  const binaryString = atob(base64Data)
  const bytes = new Uint8Array(binaryString.length)
  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i)
  }
  
  // 创建 Blob 和 File 对象
  const blob = new Blob([bytes], { type: mimeType })
  return new File([blob], fileName, { type: mimeType })
}

// 将 Base64 图片 URL 转换为 File 对象并上传
const uploadThirdPartyImage = async (imageUrl) => {
  try {
    let file
    
    // 处理 Base64 格式的图片
    if (isBase64Url(imageUrl)) {
      file = base64ToFile(imageUrl)
    } else {
      // 不处理第三方 HTTP(S) 图片，直接返回原 URL
      return imageUrl
    }
    
    // 上传文件
    const uploadedUrl = await uploadImage(file)
    return uploadedUrl
  } catch (error) {
    console.error('上传 Base64 图片失败:', error)
    // 如果上传失败，返回原 URL
    return imageUrl
  }
}

const convertToMarkdown = async () => {
  if (!quillInstance) return
  
  const html = quillInstance.root.innerHTML
  if (!html || html === '<p><br></p>') {
    ElMessage.warning('富文本编辑器内容为空')
    return
  }
  
  try {
    // 将 HTML 转换为 Markdown
    const markdown = turndownService.turndown(html)
    form.content = markdown
    activeTab.value = 'edit'
    ElMessage.success('已转换为Markdown格式')
  } catch (error) {
    console.error('转换失败:', error)
    ElMessage.error('转换失败，请检查内容格式')
  }
}

const clearRichText = () => {
  if (quillInstance) {
    quillInstance.setText('')
    ElMessage.success('已清空富文本内容')
  }
}

// 组件卸载时清理事件监听
const cleanupQuillEditor = () => {
  if (quillInstance) {
    quillInstance = null
  }
}

// 监听 tab 切换，初始化富文本编辑器
watch(activeTab, async (newVal) => {
  if (newVal === 'richtext') {
    await nextTick()
    // 如果编辑器还没初始化，初始化它
    if (!quillInstance) {
      initQuillEditor()
    }
  }
})

onMounted(() => {
  loadOptions()
  if (isEdit.value) {
    loadGuideData()
  }
})

onBeforeUnmount(() => {
  cleanupQuillEditor()
})
</script>

<style scoped>
.guide-edit-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.markdown-editor {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.editor-tabs {
  width: 100%;
}

.markdown-textarea {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  font-size: 14px;
}

.markdown-textarea :deep(textarea) {
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
  line-height: 1.6;
}

.markdown-preview {
  min-height: 500px;
  padding: 20px;
  background: #fff;
  overflow-y: auto;
  max-height: 600px;
}

.markdown-preview :deep(h1),
.markdown-preview :deep(h2),
.markdown-preview :deep(h3),
.markdown-preview :deep(h4),
.markdown-preview :deep(h5),
.markdown-preview :deep(h6) {
  margin-top: 24px;
  margin-bottom: 16px;
  font-weight: 600;
  line-height: 1.25;
}

.markdown-preview :deep(h1) {
  font-size: 2em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-preview :deep(h2) {
  font-size: 1.5em;
  border-bottom: 1px solid #eaecef;
  padding-bottom: 0.3em;
}

.markdown-preview :deep(h3) {
  font-size: 1.25em;
}

.markdown-preview :deep(p) {
  margin-bottom: 16px;
  line-height: 1.6;
}

.markdown-preview :deep(code) {
  background-color: rgba(27, 31, 35, 0.05);
  border-radius: 3px;
  font-size: 85%;
  margin: 0;
  padding: 0.2em 0.4em;
  font-family: 'Consolas', 'Monaco', 'Courier New', monospace;
}

.markdown-preview :deep(pre) {
  background-color: #f6f8fa;
  border-radius: 6px;
  padding: 16px;
  overflow: auto;
  line-height: 1.45;
  margin-bottom: 16px;
}

.markdown-preview :deep(pre code) {
  background-color: transparent;
  padding: 0;
  margin: 0;
  font-size: 100%;
  display: block;
}

.markdown-preview :deep(ul),
.markdown-preview :deep(ol) {
  padding-left: 2em;
  margin-bottom: 16px;
}

.markdown-preview :deep(li) {
  margin-bottom: 4px;
}

.markdown-preview :deep(blockquote) {
  padding: 0 1em;
  color: #6a737d;
  border-left: 0.25em solid #dfe2e5;
  margin-bottom: 16px;
}

.markdown-preview :deep(table) {
  border-collapse: collapse;
  width: 100%;
  margin-bottom: 16px;
}

.markdown-preview :deep(table th),
.markdown-preview :deep(table td) {
  padding: 6px 13px;
  border: 1px solid #dfe2e5;
}

.markdown-preview :deep(table th) {
  font-weight: 600;
  background-color: #f6f8fa;
}

.markdown-preview :deep(img) {
  max-width: 100%;
  box-sizing: border-box;
}

.markdown-preview :deep(a) {
  color: #0366d6;
  text-decoration: none;
}

.markdown-preview :deep(a:hover) {
  text-decoration: underline;
}

.empty-tip {
  color: #909399;
  text-align: center;
  padding: 50px 0;
}

.richtext-editor-wrapper {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: #fff;
}

.quill-editor {
  min-height: 400px;
  background: #fff;
}

.quill-editor :deep(.ql-container) {
  min-height: 400px;
  font-size: 14px;
  line-height: 1.6;
}

.quill-editor :deep(.ql-editor) {
  min-height: 400px;
  padding: 15px;
}

.quill-editor :deep(.ql-toolbar) {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
  background: #fafafa;
  border-bottom: 1px solid #dcdfe6;
}

.editor-actions {
  padding: 10px 15px;
  border-top: 1px solid #dcdfe6;
  background: #fafafa;
  display: flex;
  gap: 10px;
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
}
</style>

