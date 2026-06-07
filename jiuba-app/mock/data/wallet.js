export const mockWallet = {
  balance: 2850.45,
  fundRecords: [
    { id: 1, type: 'recharge', title: '账户充值', amount: 500, time: '2026-06-07 10:00:00' },
    { id: 2, type: 'consume', title: '订单消费', amount: -86, time: '2026-06-06 20:16:00' },
    { id: 3, type: 'withdraw', title: '提现', amount: -200, time: '2026-06-05 14:30:00' }
  ],
  withdrawRecords: [
    { id: 1, amount: 200, status: 'success', time: '2026-06-05 14:30:00', account: '微信钱包' }
  ],
  withdrawAccount: { type: 'wechat', name: '微信钱包', account: '***8800' }
}
