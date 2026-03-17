import request from '@/utils/request'

export const aiAPI = {
  /**
   * 创建 SSE 流式连接
   * 返回原生 EventSource 对象，调用方负责关闭
   */
  createChatStream(question, gameId) {
    const token = localStorage.getItem('token')
    let url = `/api/public/ai/chat?question=${encodeURIComponent(question)}`
    if (gameId) url += `&gameId=${gameId}`
    // EventSource 不支持自定义 Header，借助 query param 传 token（后端已放行 /api/public/**）
    return new EventSource(url)
  }
}

