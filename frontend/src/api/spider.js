import request from '@/utils/request'

export const spiderAPI = {
  // 预览爬取（不保存）
  preview(params) {
    return request.get('/spider/preview', { params })
  },

  // 批量保存爬取结果
  save(items) {
    return request.post('/spider/save', items)
  },

  // 分页查询已爬取攻略
  list(params) {
    return request.get('/spider/list', { params })
  },

  // 查询单条详情
  getById(id) {
    return request.get(`/spider/${id}`)
  },

  // 删除已爬取攻略
  deleteById(id) {
    return request.delete(`/spider/${id}`)
  }
}
