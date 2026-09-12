const session = auth.require("PROVIDER");
if (!session) throw new Error("redirect");
document.getElementById("who").textContent = session.email;
document.getElementById("logout").onclick = () => { auth.clear(); location.href = "login.html"; };

const views = ["menu", "open", "quotes", "capacity", "orders", "notes"];
document.querySelectorAll(".side button[data-view]").forEach(btn => {
  btn.onclick = () => {
    document.querySelectorAll(".side button[data-view]").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");
    views.forEach(v => document.getElementById(v).classList.toggle("hidden", v !== btn.dataset.view));
    if (btn.dataset.view === "menu") loadFood();
    if (btn.dataset.view === "open") loadOpen();
    if (btn.dataset.view === "quotes") loadQuotes();
    if (btn.dataset.view === "capacity") loadCap();
    if (btn.dataset.view === "orders") loadOrders();
    if (btn.dataset.view === "notes") loadNotes();
  };
});

document.getElementById("addFood").onclick = async () => {
  try {
    await api(API.provider, "/api/food-items", {
      method: "POST",
      body: JSON.stringify({
        name: document.getElementById("fname").value,
        description: document.getElementById("fdesc").value,
        price: Number(document.getElementById("fprice").value),
        availableQuantity: Number(document.getElementById("fqty").value)
      })
    });
    toast(document.getElementById("foodMsg"), "Saved", true);
    loadFood();
  } catch (err) {
    toast(document.getElementById("foodMsg"), err.message);
  }
};

async function loadFood() {
  const items = await api(API.provider, "/api/food-items");
  document.getElementById("foodList").innerHTML = items.map(i => `
    <article class="card item">
      <h4>${i.name} · Rs. ${i.price}</h4>
      <div class="muted">${i.description || ""} · qty ${i.availableQuantity}</div>
      <button class="btn secondary" onclick="delFood(${i.id})">Delete</button>
    </article>`).join("") || "<p class='muted'>No items yet.</p>";
}
window.delFood = async (id) => {
  await api(API.provider, "/api/food-items/" + id, { method: "DELETE" });
  loadFood();
};

async function loadOpen() {
  const requests = await api(API.provider, "/api/quotations/available");
  document.getElementById("open").innerHTML = (requests || []).map(r => `
    <article class="card item">
      <h4>Request #${r.id} · ${r.status}</h4>
      <div class="muted">${r.foodDescription} · qty ${r.quantity}</div>
      <div class="muted">${r.deliveryAddress}</div>
      <label>Price</label><input id="p${r.id}" type="number" />
      <label>Estimated minutes</label><input id="t${r.id}" type="number" value="40" />
      <button class="btn" onclick="quote(${r.id})">Submit quotation</button>
    </article>`).join("") || "<p class='muted'>No open requests, or you are marked unavailable.</p>";
}
window.quote = async (id) => {
  await api(API.provider, "/api/quotations", {
    method: "POST",
    body: JSON.stringify({
      mealRequestId: id,
      price: Number(document.getElementById("p" + id).value),
      estimatedTime: Number(document.getElementById("t" + id).value)
    })
  });
  loadOpen();
};

async function loadQuotes() {
  const quotes = await api(API.provider, "/api/quotations/mine");
  document.getElementById("quotes").innerHTML = quotes.map(q => `
    <article class="card item">
      <h4>Quote #${q.id} · ${q.status}</h4>
      <div class="muted">Request ${q.mealRequestId} · Rs. ${q.price} · ${q.estimatedTime} min</div>
    </article>`).join("") || "<p class='muted'>No quotations yet.</p>";
}

async function loadCap() {
  const p = await api(API.provider, "/api/providers/profile");
  document.getElementById("cap").value = p.capacity;
  document.getElementById("avail").value = p.availabilityStatus;
}
document.getElementById("saveCap").onclick = async () => {
  try {
    await api(API.provider, "/api/providers/profile", {
      method: "PUT",
      body: JSON.stringify({
        capacity: Number(document.getElementById("cap").value),
        availabilityStatus: document.getElementById("avail").value
      })
    });
    toast(document.getElementById("capMsg"), "Updated", true);
  } catch (err) {
    toast(document.getElementById("capMsg"), err.message);
  }
};

async function loadOrders() {
  const orders = await api(API.order, "/api/orders");
  document.getElementById("orders").innerHTML = orders.map(o => `
    <article class="card item">
      <h4>Order #${o.id} · ${o.orderStatus}</h4>
      <div class="muted">Rs. ${o.totalAmount}</div>
      <button class="btn secondary" onclick="setOrder(${o.id}, 'PREPARING')">Preparing</button>
      <button class="btn secondary" onclick="setOrder(${o.id}, 'READY_FOR_PICKUP')">Ready</button>
    </article>`).join("") || "<p class='muted'>No orders yet.</p>";
}
window.setOrder = async (id, status) => {
  await api(API.order, "/api/orders/" + id + "/status", { method: "PUT", body: JSON.stringify({ status }) });
  loadOrders();
};

async function loadNotes() {
  const notes = await api(API.order, "/api/notifications");
  document.getElementById("notes").innerHTML = notes.map(n => `
    <article class="card item"><h4>${n.notificationType}</h4><div class="muted">${n.message}</div></article>
  `).join("") || "<p class='muted'>No notifications.</p>";
}

loadFood();
