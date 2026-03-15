import request from '@/utils/request'

export const guideAPI = {
  // ========== 后台管理接口 ==========

  // 获取攻略列表（分页）
  getList(params) {
    return request.get('/guides', { params })
  },
  
  // 获取所有攻略（不分页）
  getAll() {
    return request.get('/guides/all')
  },
  
  // 获取攻略详情
  getById(guideId) {
    return request.get(`/guides/${guideId}`)
  },
  
  // 搜索攻略
  search(params) {
    return request.get('/guides/search', { params })
  },
  
  // 新建攻略
  create(data) {
    return request.post('/guides', data)
  },
  
  // 编辑攻略
  update(guideId, data) {
    return request.put(`/guides/${guideId}`, data)
  },
  
  // 删除攻略
  delete(guideId) {
    return request.delete(`/guides/${guideId}`)
  },

  // ========== 前台公开接口 ==========

  // 前台：获取攻略列表（分页）
  getPublishedList(params) {
    return request.get('/guides/public/list', { params })
  },

  // 前台：获取攻略详情
  getPublishedById(guideId) {
    return request.get(`/guides/public/${guideId}`)
  },

  // 前台：搜索攻略
  searchPublished(params) {
    return request.get('/guides/public/search', { params })
  },

  // 前台：全文搜索攻略（PostgreSQL tsvector，含高亮摘要）
  fullTextSearch(params) {
    return request.get('/public/guides/fulltext', { params })
  }
}
