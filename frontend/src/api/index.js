import request from '../utils/request'

// 登录
export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export function getUserInfo(id) {
  return request({
    url: `/user/info/${id}`,
    method: 'get'
  })
}

// 用户管理
export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

export function getUserById(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

export function getUserRoles(id) {
  return request({
    url: `/system/user/${id}/roles`,
    method: 'get'
  })
}

export function assignUserRoles(id, roleIds) {
  return request({
    url: `/system/user/${id}/roles`,
    method: 'put',
    data: roleIds
  })
}

// 角色管理
export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getAllRoles() {
  return request({
    url: '/system/role/all',
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}

export function getRoleMenus(id) {
  return request({
    url: `/system/role/${id}/menus`,
    method: 'get'
  })
}

export function assignRoleMenus(id, menuIds) {
  return request({
    url: `/system/role/${id}/menus`,
    method: 'put',
    data: menuIds
  })
}

// 菜单管理
export function getMenuList(params) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params
  })
}

export function getMenuTree() {
  return request({
    url: '/system/menu/tree',
    method: 'get'
  })
}

export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

export function deleteMenu(id) {
  return request({
    url: `/system/menu/${id}`,
    method: 'delete'
  })
}

// 小区管理
export function getCommunityList(params) {
  return request({
    url: '/community/list',
    method: 'get',
    params
  })
}

export function getAllCommunities() {
  return request({
    url: '/community/all',
    method: 'get'
  })
}

export function addCommunity(data) {
  return request({
    url: '/community',
    method: 'post',
    data
  })
}

export function updateCommunity(data) {
  return request({
    url: '/community',
    method: 'put',
    data
  })
}

export function deleteCommunity(id) {
  return request({
    url: `/community/${id}`,
    method: 'delete'
  })
}

export function getCommunityStatistics() {
  return request({
    url: '/community/statistics',
    method: 'get'
  })
}

// 楼栋管理
export function getBuildingList(params) {
  return request({
    url: '/community/building/list',
    method: 'get',
    params
  })
}

export function getAllBuildings(communityId) {
  return request({
    url: '/community/building/all',
    method: 'get',
    params: { communityId }
  })
}

export function addBuilding(data) {
  return request({
    url: '/community/building',
    method: 'post',
    data
  })
}

export function updateBuilding(data) {
  return request({
    url: '/community/building',
    method: 'put',
    data
  })
}

export function deleteBuilding(id) {
  return request({
    url: `/community/building/${id}`,
    method: 'delete'
  })
}

// 单元管理
export function getUnitList(params) {
  return request({
    url: '/community/unit/list',
    method: 'get',
    params
  })
}

export function getAllUnits(buildingId) {
  return request({
    url: '/community/unit/all',
    method: 'get',
    params: { buildingId }
  })
}

export function addUnit(data) {
  return request({
    url: '/community/unit',
    method: 'post',
    data
  })
}

export function updateUnit(data) {
  return request({
    url: '/community/unit',
    method: 'put',
    data
  })
}

export function deleteUnit(id) {
  return request({
    url: `/community/unit/${id}`,
    method: 'delete'
  })
}

// 房屋管理
export function getRoomList(params) {
  return request({
    url: '/community/room/list',
    method: 'get',
    params
  })
}

export function getAllRooms(unitId) {
  return request({
    url: '/community/room/all',
    method: 'get',
    params: { unitId }
  })
}

export function addRoom(data) {
  return request({
    url: '/community/room',
    method: 'post',
    data
  })
}

export function updateRoom(data) {
  return request({
    url: '/community/room',
    method: 'put',
    data
  })
}

export function deleteRoom(id) {
  return request({
    url: `/community/room/${id}`,
    method: 'delete'
  })
}

export function getRoomStatistics() {
  return request({
    url: '/community/room/statistics/status',
    method: 'get'
  })
}

// 业主管理
export function getOwnerList(params) {
  return request({
    url: '/owner/list',
    method: 'get',
    params
  })
}

export function getAllOwners() {
  return request({
    url: '/owner/all',
    method: 'get'
  })
}

export function getOwnerById(id) {
  return request({
    url: `/owner/${id}`,
    method: 'get'
  })
}

export function addOwner(data) {
  return request({
    url: '/owner',
    method: 'post',
    data
  })
}

export function updateOwner(data) {
  return request({
    url: '/owner',
    method: 'put',
    data
  })
}

export function deleteOwner(id) {
  return request({
    url: `/owner/${id}`,
    method: 'delete'
  })
}

export function searchOwner(keyword) {
  return request({
    url: '/owner/search',
    method: 'get',
    params: { keyword }
  })
}

// 人房绑定
export function getOwnerRoomList(params) {
  return request({
    url: '/owner/room/list',
    method: 'get',
    params
  })
}

export function getOwnerRooms(ownerId) {
  return request({
    url: `/owner/room/owner/${ownerId}`,
    method: 'get'
  })
}

export function addOwnerRoom(data) {
  return request({
    url: '/owner/room',
    method: 'post',
    data
  })
}

export function deleteOwnerRoom(id) {
  return request({
    url: `/owner/room/${id}`,
    method: 'delete'
  })
}
