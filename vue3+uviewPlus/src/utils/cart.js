const CART_KEY = 'local_cart'

export function getLocalCart() {
  try {
    return uni.getStorageSync(CART_KEY) || []
  } catch {
    return []
  }
}

export function saveLocalCart(list) {
  uni.setStorageSync(CART_KEY, list)
}

export function addToLocalCart(product, quantity = 1) {
  const cart = getLocalCart()
  const idx = cart.findIndex((item) => item.productId === product.id)
  if (idx >= 0) {
    cart[idx].quantity += quantity
  } else {
    cart.push({
      id: Date.now(),
      productId: product.id,
      productName: product.name,
      image: product.image,
      unitPrice: product.price,
      quantity
    })
  }
  saveLocalCart(cart)
  return cart
}

export function updateLocalCartItem(id, quantity) {
  const cart = getLocalCart()
  const idx = cart.findIndex((item) => item.id === id)
  if (idx >= 0) {
    if (quantity <= 0) {
      cart.splice(idx, 1)
    } else {
      cart[idx].quantity = quantity
    }
  }
  saveLocalCart(cart)
  return cart
}

export function removeLocalCartItem(id) {
  const cart = getLocalCart().filter((item) => item.id !== id)
  saveLocalCart(cart)
  return cart
}

export function getCartSummary(cart) {
  const count = cart.reduce((sum, item) => sum + item.quantity, 0)
  const total = cart.reduce((sum, item) => sum + item.quantity * Number(item.unitPrice || 0), 0)
  return { count, total }
}
