const API_BASE = '';

function escapeHTML(str) {
  if (typeof str !== 'string') return str;
  return str.replace(/[&<>'"]/g, tag => ({
    '&': '&amp;',
    '<': '&lt;',
    '>': '&gt;',
    "'": '&#39;',
    '"': '&quot;'
  }[tag] || tag));
}

function logout() {
  localStorage.removeItem('jwt_token');
  window.location.href = '/login.html';
}

async function fetchWithAuth(url, options = {}) {
  const token = localStorage.getItem('jwt_token');
  if (!token && !window.location.href.includes('login.html') && !window.location.href.includes('cadastro.html')) {
    window.location.href = '/login.html';
    return null;
  }
  const headers = options.headers || {};
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  options.headers = headers;
  const res = await fetch(url, options);
  if (res.status === 401 || res.status === 403) {
    logout();
    return null;
  }
  if (!res.ok) {
    const banner = document.getElementById('error-banner');
    if (banner) banner.style.display = 'block';
    throw new Error('Erro na API');
  }
  return await res.json();
}
