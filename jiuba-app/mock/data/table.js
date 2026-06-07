export const mockTableInteraction = {
  tableName: 'A.红桌',
  status: 'booking',
  statusText: '预约中',
  updateTime: '2026-06-07 18:30',
  basePoints: 20,
  maxPoints: 40,
  occupiedSeats: 4,
  totalSeats: 9,
  gameName: '云享对弈',
  seats: [
    { id: 'A1', occupied: true, nickname: '小明', avatar: 'https://cdn.uviewui.com/uview/album/1.jpg' },
    { id: 'A2', occupied: false },
    { id: 'A3', occupied: true, nickname: 'Leo', avatar: 'https://cdn.uviewui.com/uview/album/4.jpg' },
    { id: 'A4', occupied: true, nickname: '我', avatar: 'https://cdn.uviewui.com/uview/album/1.jpg', isMe: true },
    { id: 'A5', occupied: false },
    { id: 'A6', occupied: true, nickname: '少女', avatar: 'https://cdn.uviewui.com/uview/album/2.jpg' },
    { id: 'A7', occupied: false },
    { id: 'A8', occupied: false }
  ],
  mySeat: 'A4'
}
