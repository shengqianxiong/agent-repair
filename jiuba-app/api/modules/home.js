import { get } from '@/utils/request.js'

/** 首页初始化（欢迎卡、服务入口、精选活动） */
export const getHomeIndex = () => get('/app/home/init')
export const getActivityList = (params) => get('/app/activity/list', params)
export const getActivityDetail = (id) => get(`/app/activity/detail/${id}`)
