export function isAdmin() {
  return localStorage.getItem('roleKey') === 'admin'
}

export function isAdminOrSelf(userId) {
  const currentUserId = localStorage.getItem('userId')
  const currentUsername = localStorage.getItem('username')
  return isAdmin() || currentUserId == userId || currentUsername === 'admin'
}
