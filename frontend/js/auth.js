const proxied = location.port === "8080" || location.port === "80";
window.API = {
  consumer: proxied ? "/consumer-api" : "http://localhost:8081",
  provider: proxied ? "/provider-api" : "http://localhost:8082",
  order: proxied ? "/order-api" : "http://localhost:8083",
};

window.auth = {
  save(data) {
    localStorage.setItem("fdms_auth", JSON.stringify(data));
  },
  get() {
    try { return JSON.parse(localStorage.getItem("fdms_auth") || "null"); } catch { return null; }
  },
  token() {
    return this.get()?.token;
  },
  clear() {
    localStorage.removeItem("fdms_auth");
  },
  require(role) {
    const session = this.get();
    if (!session?.token) {
      location.href = "login.html";
      return null;
    }
    if (role && session.role !== role) {
      location.href = "login.html";
      return null;
    }
    return session;
  }
};

window.api = async function api(base, path, options = {}) {
  const headers = { "Content-Type": "application/json", ...(options.headers || {}) };
  const token = window.auth.token();
  if (token) headers.Authorization = "Bearer " + token;
  const res = await fetch(base + path, { ...options, headers });
  const text = await res.text();
  const body = text ? JSON.parse(text) : null;
  if (!res.ok) throw new Error(body?.error || "Request failed");
  return body;
};

window.toast = function toast(el, message, ok = false) {
  if (!el) return;
  el.textContent = message || "";
  el.style.color = ok ? "#9be7c4" : "#ffb4b4";
};
