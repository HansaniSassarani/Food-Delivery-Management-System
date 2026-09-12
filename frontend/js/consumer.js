const session = auth.require("CONSUMER");
if (!session) throw new Error("redirect");

document.getElementById("who").textContent = session.email;
document.getElementById("hello").textContent = "Welcome, " + (session.organizationName || "customer");
document.getElementById("logout").onclick = () => { auth.clear(); location.href = "login.html"; };

const views = ["request", "requests", "quotes", "orders", "track", "profile", "notes"];
document.querySelectorAll(".side button[data-view]").forEach(btn => {
  btn.onclick = () => {
    document.querySelectorAll(".side button[data-view]").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");
    views.forEach(v => document.getElementById(v).classList.toggle("hidden", v !== btn.dataset.view));
    if (btn.dataset.view === "requests") loadRequests();
    if (btn.dataset.view === "quotes") loadQuotes();
    if (btn.dataset.view === "orders") loadOrders();
    if (btn.dataset.view === "track") loadTrack();
    if (btn.dataset.view === "profile") loadProfile();
    if (btn.dataset.view === "notes") loadNotes();
  };
});

document.getElementById("createReq").onclick = async () => {
  const msg = document.getElementById("reqMsg");
  try {
    await api(API.consumer, "/api/meal-requests", {
      method: "POST",
      body: JSON.stringify({
        quantity: Number(document.getElementById("qty").value),
        deliveryDate: document.getElementById("ddate").value,
        deliveryAddress: document.getElementById("daddr").value,
        foodDescription: document.getElementById("fdesc").value,
        latitude: 6.9271,
        longitude: 79.8612
      })
    });
    toast(msg, "Meal request created", true);
  } catch (err) {
    toast(msg, err.message);
  }
};

async function loadRequests() {
  const list = await api(API.consumer, "/api/meal-requests");
  document.getElementById("requests").innerHTML = list.map(r => `
    <article class="card item">
      <h4>#${r.id} · ${r.status}</h4>
      <div class="muted">${r.foodDescription} · qty ${r.quantity}</div>
      <div class="muted">${r.deliveryAddress} · ${r.deliveryDate}</div>
      <button class="btn secondary" onclick="cancelReq(${r.id})">Cancel</button>
    </article>`).join("") || "<p class='muted'>No requests yet.</p>";
}

window.cancelReq = async (id) => {
  await api(API.consumer, "/api/meal-requests/" + id, { method: "DELETE" });
  loadRequests();
};

async function loadQuotes() {
  const requests = await api(API.consumer, "/api/meal-requests");
  const blocks = [];
  for (const r of requests) {
    const quotes = await api(API.provider, "/api/quotations/meal-request/" + r.id);
    blocks.push(`<article class="card item"><h4>Request #${r.id} · ${r.status}</h4>
      ${quotes.map(q => `<p>Quote #${q.id} · Rs. ${q.price} · ${q.estimatedTime} min · ${q.status}
      ${q.status === "PENDING" ? `<button class="btn" onclick="acceptQuote(${q.id})">Accept</button>` : ""}</p>`).join("") || "<p class='muted'>No quotations yet.</p>"}
    </article>`);
  }
  document.getElementById("quotes").innerHTML = blocks.join("") || "<p class='muted'>Create a meal request first.</p>";
}

window.acceptQuote = async (id) => {
  await api(API.order, "/api/orders/accept-quotation", { method: "POST", body: JSON.stringify({ quotationId: id }) });
  loadQuotes();
};

async function loadOrders() {
  const orders = await api(API.order, "/api/orders");
  document.getElementById("orders").innerHTML = orders.map(o => `
    <article class="card item">
      <h4>Order #${o.id} · ${o.orderStatus}</h4>
      <div class="muted">Amount Rs. ${o.totalAmount} · ETA ${o.estimatedMinutes || "-"} min</div>
    </article>`).join("") || "<p class='muted'>No orders yet.</p>";
}

async function loadTrack() {
  const orders = await api(API.order, "/api/orders");
  const select = document.getElementById("trackOrder");
  select.innerHTML = orders.map(o => `<option value="${o.id}">Order #${o.id} · ${o.orderStatus}</option>`).join("");
  MapKit.init("map");
  select.onchange = () => refreshTrack(select.value);
  if (orders[0]) refreshTrack(orders[0].id);
}

async function refreshTrack(id) {
  const data = await api(API.order, "/api/orders/" + id + "/track");
  const d = data.delivery;
  MapKit.pin("provider", d.pickupLatitude, d.pickupLongitude, "Pickup");
  MapKit.pin("customer", d.deliveryLatitude, d.deliveryLongitude, "Customer");
  MapKit.pin("rider", d.currentLatitude, d.currentLongitude, "Rider");
  MapKit.fit();
  document.getElementById("trackMeta").textContent =
    `${data.delivery.deliveryStatus} · ${data.distanceKm} km · ~${data.estimatedMinutes} min`;
}

async function loadProfile() {
  const p = await api(API.consumer, "/api/consumers/profile");
  document.getElementById("pName").value = p.organizationName;
  document.getElementById("pContact").value = p.contactNo;
  document.getElementById("pAddr").value = p.address;
}

document.getElementById("saveProfile").onclick = async () => {
  try {
    await api(API.consumer, "/api/consumers/profile", {
      method: "PUT",
      body: JSON.stringify({
        organizationName: document.getElementById("pName").value,
        contactNo: document.getElementById("pContact").value,
        address: document.getElementById("pAddr").value
      })
    });
    toast(document.getElementById("profMsg"), "Saved", true);
  } catch (err) {
    toast(document.getElementById("profMsg"), err.message);
  }
};

async function loadNotes() {
  const notes = await api(API.order, "/api/notifications");
  document.getElementById("notes").innerHTML = notes.map(n => `
    <article class="card item"><h4>${n.notificationType}</h4><div class="muted">${n.message}</div></article>
  `).join("") || "<p class='muted'>No notifications.</p>";
}
