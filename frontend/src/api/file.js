import request from '@/utils/request'

export const fileAPI = {
  // 上传文件
  upload(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 图片转链：将第三方 HTTP 图片上传到本地存储并返回本地 URL
  rehost(url) {
    return request.post('/files/rehost', { url })
  }
}

