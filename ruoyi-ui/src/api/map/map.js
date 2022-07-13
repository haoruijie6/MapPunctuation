import request from '@/utils/request'

// 查询线路图信息列表
export function listInformation(query) {
  return request({
    url: '/map/',
    method: 'get',
    params: query
  })
}

// 删除线路图信息
export function deleteInformation(id) {
  return request({
    url: '/map/' + id,
    method: 'delete'
  })
}
