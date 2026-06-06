import { get, post } from '@/utils/request'

export function getHomeIndex() {
  return get('/home/index')
}

export function login(data) {
  return post('/user/login', data, { showLoading: true, loadingText: '登录中...' })
}

export function getUserInfo() {
  return get('/user/info')
}
