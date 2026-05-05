const API_BASE = 'http://localhost:8080'

export const getImageUrl = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return API_BASE + url
}
