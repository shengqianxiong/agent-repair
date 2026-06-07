export const mockCategories = [
  { id: 1, name: '精酿', icon: 'beer' },
  { id: 2, name: '梦幻德州', icon: 'star' },
  { id: 3, name: '精致小吃', icon: 'gift' },
  { id: 4, name: '经典啤酒', icon: 'bag' },
  { id: 5, name: '清爽软饮', icon: 'cup' },
  { id: 6, name: '特调鸡尾', icon: 'wine' }
]

export const mockProducts = [
  {
    id: 101,
    categoryId: 1,
    name: 'IPA 精酿啤酒',
    desc: '浓郁果香，微苦回甘',
    image: 'https://cdn.uviewui.com/uview/album/2.jpg',
    price: 38,
    memberPrice: 32,
    stock: 99
  },
  {
    id: 102,
    categoryId: 1,
    name: '小麦白啤',
    desc: '清爽顺滑，泡沫细腻',
    image: 'https://cdn.uviewui.com/uview/album/3.jpg',
    price: 35,
    memberPrice: 30,
    stock: 99
  },
  {
    id: 103,
    categoryId: 1,
    name: '黑啤世涛',
    desc: '醇厚咖啡香，巧克力尾韵',
    image: 'https://cdn.uviewui.com/uview/album/4.jpg',
    price: 42,
    memberPrice: 36,
    stock: 50
  },
  {
    id: 201,
    categoryId: 3,
    name: '香脆薯条',
    desc: '现炸金黄，搭配特调酱料',
    image: 'https://cdn.uviewui.com/uview/album/5.jpg',
    price: 28,
    memberPrice: 24,
    stock: 99
  }
]

export const mockCart = {
  items: [
    { id: 1, productId: 101, name: 'IPA 精酿啤酒', spec: '500ml', image: 'https://cdn.uviewui.com/uview/album/2.jpg', price: 38, quantity: 1 },
    { id: 2, productId: 102, name: '小麦白啤', spec: '500ml', image: 'https://cdn.uviewui.com/uview/album/3.jpg', price: 42, quantity: 1 }
  ],
  totalAmount: 80,
  discountAmount: 12,
  count: 2
}
