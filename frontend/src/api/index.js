import router from '../router'

const BASE = '/api'

function headers() {
  const token = localStorage.getItem('token')
  return {
    'Content-Type': 'application/json',
    ...(token ? { Authorization: `Bearer ${token}` } : {})
  }
}

async function request(method, path, body) {
  const res = await fetch(BASE + path, {
    method,
    headers: headers(),
    ...(body !== undefined ? { body: JSON.stringify(body) } : {})
  })
  if (res.status === 401) {
    localStorage.clear()
    router.push('/auth')
    return null
  }
  return res.json()
}

export const authApi = {
  sendCode:       (email)                 => request('POST', '/auth/email/send', { email }),
  login:          (email, code)           => request('POST', '/auth/login',       { email, code }),
  loginByPassword:(email, password)       => request('POST', '/auth/login/password', { email, password }),
  register:       (email, code, nickname) => request('POST', '/auth/register',    { email, code, nickname }),
  logout:         ()                      => request('POST', '/auth/logout')
}

export const userApi = {
  me:             ()              => request('GET',  '/users/me'),
  update:         (data)          => request('PUT',  '/users/me',          data),
  updatePassword: (oldPassword, newPassword) => request('PUT', '/users/me/password', { oldPassword, newPassword }),
  resetPassword:  (email, code, newPassword) => request('PUT', '/users/password/reset', { email, code, newPassword }),
  search:         (keyword)       => request('GET',  `/users/search?keyword=${encodeURIComponent(keyword)}`)
}
