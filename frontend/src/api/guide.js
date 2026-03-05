import request from '@/utils/request'

export const guideAPI = {
  // 获取攻略列表（分页）
  getList(params) {
    return request.get('/guides', { params })
  },
  
  // 获取所有攻略
  getAll() {
    return request.get('/guides/all')
  },
  
  // 获取攻略详情
  getById(id) {
    return request.get(`/guides/${id}`)
  },
  
  // 搜索攻略
  search(params) {
    return request.get('/guides/search', { params })
  },
  
  // 创建攻略
  create(data) {
    return request.post('/guides', data)
  },
  
  // 更新攻略
  update(id, data) {
    return request.put(`/guides/${id}`, data)
  },
  
  // 删除攻略
  delete(id) {
    return request.delete(`/guides/${id}`)
  }
}



