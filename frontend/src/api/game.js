import request from '@/utils/request'

export const gameAPI = {
  // 获取游戏列表（分页）
  getList(params) {
    return request.get('/games', { params })
  },
  
  // 获取所有游戏
  getAll() {
    return request.get('/games/all')
  },
  
  // 获取游戏详情
  getById(id) {
    return request.get(`/games/${id}`)
  },
  
  // 搜索游戏
  search(params) {
    return request.get('/games/search', { params })
  },
  
  // 创建游戏
  create(data) {
    return request.post('/games', data)
  },
  
  // 更新游戏
  update(id, data) {
    return request.put(`/games/${id}`, data)
  },
  
  // 删除游戏
  delete(id) {
    return request.delete(`/games/${id}`)
  }
}



