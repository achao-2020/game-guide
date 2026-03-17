<template>
  <!-- 悬浮按钮 -->
  <div class="ai-float-btn" @click="openChat" title="AI 攻略助手">
    <el-icon :size="24"><ChatDotRound /></el-icon>
    <span class="btn-label">AI 助手</span>
  </div>

  <!-- 对话浮窗 -->
  <teleport to="body">
    <transition name="chat-slide">
      <div v-if="visible" class="ai-chat-panel">
        <!-- 头部 -->
        <div class="chat-header">
          <div class="chat-title">
            <el-icon :size="18"><ChatDotRound /></el-icon>
            <span>AI 攻略助手</span>
            <el-tag size="small" type="success" effect="dark">GLM-4</el-tag>
          </div>
          <div class="chat-header-actions">
            <el-tooltip content="清空对话">
              <el-icon class="action-icon" @click="clearMessages"><Delete /></el-icon>
            </el-tooltip>
            <el-icon class="action-icon close-icon" @click="visible = false"><Close /></el-icon>
          </div>
        </div>

        <!-- 消息列表 -->
        <div class="chat-messages" ref="messagesRef">
          <!-- 欢迎语 -->
          <div v-if="messages.length === 0" class="welcome-msg">
            <div class="welcome-avatar">
              <el-icon :size="32"><ChatDotRound /></el-icon>
            </div>
            <p class="welcome-text">你好！我是游戏攻略 AI 助手。</p>
            <p class="welcome-sub">你可以问我任何关于游戏攻略的问题，我会从攻略库中检索相关内容为你解答。</p>
            <div class="quick-questions">
              <span
                v-for="q in quickQuestions"
                :key="q"
                class="quick-q"
                @click="sendQuickQuestion(q)"
              >{{ q }}</span>
            </div>
          </div>

          <!-- 消息气泡 -->
          <div
            v-for="(msg, idx) in messages"
            :key="idx"
            :class="['msg-item', msg.role]"
          >
            <div class="msg-avatar">
              <el-icon v-if="msg.role === 'assistant'" :size="16"><ChatDotRound /></el-icon>
              <el-icon v-else :size="16"><User /></el-icon>
            </div>
            <div class="msg-bubble">
              <div class="msg-content" v-html="renderContent(msg.content)"></div>
              <div v-if="msg.role === 'assistant' && msg.loading" class="typing-indicator">
                <span></span><span></span><span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="chat-input-area">
          <el-input
            v-model="inputText"
            type="textarea"
            :rows="2"
            placeholder="输入你的问题，按 Enter 发送（Shift+Enter 换行）"
            resize="none"
            @keydown.enter.exact.prevent="handleSend"
            :disabled="streaming"
            class="chat-input"
          />
          <el-button
            type="primary"
            :loading="streaming"
            :disabled="!inputText.trim()"
            @click="handleSend"
            class="send-btn"
          >
            <el-icon v-if="!streaming"><Promotion /></el-icon>
            <span v-if="streaming">生成中...</span>
          </el-button>
        </div>
        <div class="chat-footer-tip">AI 回答基于攻略库内容，仅供参考</div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ChatDotRound, Delete, Close, User, Promotion } from '@element-plus/icons-vue'

const visible    = ref(false)
const inputText  = ref('')
const streaming  = ref(false)
const messages   = ref([])
const messagesRef = ref(null)

const quickQuestions = [
  '新手入门有什么推荐？',
  '角色培养顺序怎么安排？',
  '深境螺旋怎么打满星？',
  '装备词条怎么选择？'
]

const openChat = () => { visible.value = true }

const clearMessages = () => { messages.value = [] }

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const renderContent = (text) => {
  if (!text) return ''
  // 简单 Markdown：粗体、代码块、换行
  return text
    .replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;')
    .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
    .replace(/`([^`]+)`/g, '<code>$1</code>')
    .replace(/\n/g, '<br/>')
}

const sendQuickQuestion = (q) => {
  inputText.value = q
  handleSend()
}

const handleSend = async () => {
  const question = inputText.value.trim()
  if (!question || streaming.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content: question })
  inputText.value = ''
  await scrollToBottom()

  // 添加 AI 占位消息
  const aiMsg = { role: 'assistant', content: '', loading: true }
  messages.value.push(aiMsg)
  streaming.value = true
  await scrollToBottom()

  // 使用 fetch + ReadableStream 实现 SSE（比 EventSource 支持更好）
  const url = `/api/public/ai/chat?question=${encodeURIComponent(question)}`
  try {
    const resp = await fetch(url, {
      headers: { Accept: 'text/event-stream' }
    })
    const reader = resp.body.getReader()
    const decoder = new TextDecoder('utf-8')
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const lines = buffer.split('\n')
      buffer = lines.pop() // 保留不完整的行

      for (const line of lines) {
        if (line.startsWith('data:')) {
          const data = line.slice(5).trim()
          if (data === '[DONE]') {
            aiMsg.loading = false
            streaming.value = false
            break
          }
          if (data) {
            aiMsg.content += data
            await scrollToBottom()
          }
        } else if (line.startsWith('event:error')) {
          aiMsg.loading = false
          streaming.value = false
        }
      }
    }
  } catch (e) {
    aiMsg.content = '请求失败，请检查网络或稍后重试。'
    console.error('AI 请求异常:', e)
  } finally {
    aiMsg.loading = false
    streaming.value = false
    await scrollToBottom()
  }
}
</script>

<style scoped>
/* ---- 悬浮按钮 ---- */
.ai-float-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 10px 18px;
  border-radius: 24px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.45);
  transition: all 0.25s;
  user-select: none;
}
.ai-float-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.55);
}

/* ---- 浮窗 ---- */
.ai-chat-panel {
  position: fixed;
  bottom: 24px;
  right: 24px;
  width: 400px;
  height: 580px;
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 16px 48px rgba(0,0,0,0.18);
  display: flex;
  flex-direction: column;
  z-index: 9999;
  overflow: hidden;
  border: 1px solid rgba(102,126,234,0.15);
}

/* 动画 */
.chat-slide-enter-active, .chat-slide-leave-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}
.chat-slide-enter-from, .chat-slide-leave-to {
  opacity: 0;
  transform: translateY(30px) scale(0.95);
}

/* ---- 头部 ---- */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  flex-shrink: 0;
}
.chat-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  font-weight: 700;
}
.chat-header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}
.action-icon {
  cursor: pointer;
  opacity: 0.85;
  transition: opacity 0.2s;
  font-size: 18px;
}
.action-icon:hover { opacity: 1; }

/* ---- 消息区 ---- */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  background: #f7f8fc;
}
.chat-messages::-webkit-scrollbar { width: 4px; }
.chat-messages::-webkit-scrollbar-thumb {
  background: #d0d4e8;
  border-radius: 2px;
}

/* ---- 欢迎区 ---- */
.welcome-msg {
  text-align: center;
  padding: 20px 10px;
}
.welcome-avatar {
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin: 0 auto 14px;
}
.welcome-text {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}
.welcome-sub {
  font-size: 13px;
  color: #888;
  line-height: 1.7;
  margin-bottom: 16px;
}
.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}
.quick-q {
  font-size: 12px;
  background: white;
  border: 1px solid #d0d4e8;
  color: #667eea;
  padding: 5px 12px;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s;
}
.quick-q:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* ---- 消息气泡 ---- */
.msg-item {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}
.msg-item.user { flex-direction: row-reverse; }

.msg-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  font-size: 14px;
}
.msg-item.assistant .msg-avatar {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
}
.msg-item.user .msg-avatar {
  background: #e8e8e8;
  color: #666;
}

.msg-bubble {
  max-width: 78%;
}
.msg-item.user .msg-bubble { align-items: flex-end; }

.msg-content {
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.7;
  word-break: break-word;
}
.msg-item.assistant .msg-content {
  background: white;
  color: #333;
  border-radius: 4px 14px 14px 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.msg-item.user .msg-content {
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border-radius: 14px 4px 14px 14px;
}
.msg-content :deep(code) {
  background: rgba(0,0,0,0.08);
  padding: 1px 5px;
  border-radius: 4px;
  font-size: 13px;
  font-family: monospace;
}
.msg-item.user .msg-content :deep(code) {
  background: rgba(255,255,255,0.2);
}

/* 打字动画 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 8px 14px;
}
.typing-indicator span {
  width: 7px;
  height: 7px;
  background: #aab;
  border-radius: 50%;
  animation: bounce 1.2s infinite;
}
.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }
@keyframes bounce {
  0%, 60%, 100% { transform: translateY(0); }
  30% { transform: translateY(-6px); }
}

/* ---- 输入区 ---- */
.chat-input-area {
  display: flex;
  gap: 8px;
  padding: 12px 14px 8px;
  border-top: 1px solid #eef0f8;
  background: white;
  flex-shrink: 0;
}
.chat-input { flex: 1; }
.chat-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
}
.send-btn {
  align-self: flex-end;
  border-radius: 12px;
  padding: 8px 14px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  border: none;
}
.send-btn:hover {
  opacity: 0.9;
}

.chat-footer-tip {
  text-align: center;
  font-size: 11px;
  color: #bbb;
  padding: 4px 0 10px;
  background: white;
}
</style>

