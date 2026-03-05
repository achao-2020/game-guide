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
  }
}

