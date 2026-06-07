export const mockStore = {
  id: 1,
  name: '云享酒吧 · 静安店',
  address: '上海市静安区南京西路1266号',
  tableNo: 'A-08',
  dineType: '堂食'
}

export const mockOrders = [
  {
    id: 'ORD20260607001',
    status: 'making',
    statusText: '制作中',
    pickupCode: 'A108',
    queueAhead: 3,
    storeName: '云享酒吧 · 静安店',
    createTime: '2026-06-07 18:30:00',
    payAmount: 128.0,
    items: [
      { name: 'IPA 精酿啤酒', spec: '500ml', image: 'https://cdn.uviewui.com/uview/album/2.jpg', price: 38, quantity: 2 },
      { name: '香脆薯条', spec: '大份', image: 'https://cdn.uviewui.com/uview/album/5.jpg', price: 28, quantity: 1 }
    ]
  },
  {
    id: 'ORD20260606002',
    status: 'pending_pickup',
    statusText: '待出餐',
    pickupCode: 'A086',
    queueAhead: 0,
    makingCount: 2,
    pendingCount: 5,
    storeName: '云享酒吧 · 静安店',
    createTime: '2026-06-06 20:15:00',
    payAmount: 86.0,
    items: [
      { name: '小麦白啤', spec: '500ml', image: 'https://cdn.uviewui.com/uview/album/3.jpg', price: 42, quantity: 2 }
    ]
  },
  {
    id: 'ORD20260605003',
    status: 'completed',
    statusText: '已完成',
    storeName: '云享酒吧 · 静安店',
    createTime: '2026-06-05 19:00:00',
    payAmount: 156.0,
    items: [
      { name: '黑啤世涛', spec: '500ml', image: 'https://cdn.uviewui.com/uview/album/4.jpg', price: 42, quantity: 2 },
      { name: 'IPA 精酿啤酒', spec: '500ml', image: 'https://cdn.uviewui.com/uview/album/2.jpg', price: 38, quantity: 2 }
    ]
  },
  {
    id: 'ORD20260604004',
    status: 'cancelled',
    statusText: '已取消',
    storeName: '云享酒吧 · 静安店',
    createTime: '2026-06-04 17:30:00',
    payAmount: 68.0,
    refundNote: '订单已取消，款项已原路退回',
    items: [
      { name: '香脆薯条', spec: '大份', image: 'https://cdn.uviewui.com/uview/album/5.jpg', price: 28, quantity: 1 }
    ]
  }
]

export const mockOrderDetail = {
  id: 'ORD20260606002',
  status: 'pending_pickup',
  statusText: '等待出餐中',
  estimatedTime: '预计 5-10 分钟',
  pickupCode: 'A086',
  makingCount: 2,
  pendingCount: 5,
  store: {
    name: '云享酒吧 · 静安店',
    address: '上海市静安区南京西路1266号'
  },
  items: [
    { name: '小麦白啤', spec: '500ml · 冰镇', image: 'https://cdn.uviewui.com/uview/album/3.jpg', price: 42, quantity: 2 }
  ],
  subtotal: 84,
  packingFee: 2,
  discount: 0,
  totalAmount: 86,
  payAmount: 86,
  payMethod: '微信支付',
  remark: '少冰',
  createTime: '2026-06-06 20:15:32'
}
