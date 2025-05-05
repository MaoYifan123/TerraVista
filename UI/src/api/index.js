import request from '@/utils/request'

// 用户相关接口
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function register(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data
  })
}

export function getUserInfo() {
  return request({
    url: '/user/info',
    method: 'get'
  })
}

// 地图相关接口
export function getMapData(params) {
  return request({
    url: '/map/data',
    method: 'get',
    params
  })
}

export function updateMapData(data) {
  return request({
    url: '/map/data',
    method: 'put',
    data
  })
}

// 其他接口可以根据需要添加 