const session = auth.require("DELIVERY");
if (!session) throw new Error("redirect");
document.getElementById("who").textContent = session.email;
document.getElementById("logout").onclick = () => { auth.clear(); location.href = "login.html"; };

const views = ["jobs", "current", "history", "profile", "notes"];
document.querySelectorAll(".side button[data-view]").forEach(btn => {
  btn.onclick = () => {
    document.querySelectorAll(".side button[data-view]").forEach(b => b.classList.remove("active"));
    btn.classList.add("active");
    views.forEach(v => document.getElementById(v).classList.toggle("hidden", v !== btn.dataset.view));
    if (btn.dataset.view === "jobs") loadJobs();
    if (btn.dataset.view === "current") loadCurrent();
    if (btn.dataset.view === "history") loadHistory();
    if (btn.dataset.view === "profile") loadProfile();
    if (btn.dataset.view === "notes") loadNotes();
  };
});

async function loadJobs() {
  const jobs = await api(API.order, "/api/delivery/requests");
  document.getElementById("jobs").innerHTML = jobs.map(d => `
    <article class="card item">
      <h4>Delivery #${d.id} · order ${d.orderId}</h4>
      <div class="muted">${d.deliveryStatus}</div>
      <button class="btn" onclick="acceptJob(${d.id})">Accept</button>
    </article>`).join("") || "<p class='muted'>No open delivery requests. Stay AVAILABLE to receive jobs.</p>";
}
window.acceptJob = async (id) => {
  await api(API.order, "/api/delivery/" + id + "/accept", { method: "POST" });
  loadJobs();
};

async function loadCurrent() {
  const mine = await api(API.order, "/api/delivery/mine");
  const current = mine.find(d => d.deliveryStatus !== "DELIVERED");
  MapKit.init("map");
  if (!current) {
    document.getElementById("curMeta").textContent = "No active delivery.";
    document.getElementById("curActions").innerHTML = "";
    return;
  }
  MapKit.pin("pickup", current.pickupLatitude, current.pickupLongitude, "Pickup");
  MapKit.pin("drop", current.deliveryLatitude, current.deliveryLongitude, "Customer");
  MapKit.pin("me", current.currentLatitude, current.currentLongitude, "You");
  MapKit.fit();
  document.getElementById("curMeta").textContent = "Delivery #" + current.id + " · " + current.deliveryStatus;
  document.getElementById("curActions").innerHTML = `
    <button class="btn secondary" onclick="setDel(${current.id}, 'PICKED_UP')">Picked up</button>
    <button class="btn secondary" onclick="setDel(${current.id}, 'ON_THE_WAY')">On the way</button>
    <button class="btn" onclick="setDel(${current.id}, 'DELIVERED')">Delivered</button>
    <button class="btn secondary" onclick="ping(${current.id})">Share GPS</button>`;
  window._current = current;
}

window.setDel = async (id, status) => {
  await api(API.order, "/api/delivery/" + id + "/status", { method: "PUT", body: JSON.stringify({ status }) });
  loadCurrent();
};

window.ping = async (id) => {
  const loc = await new Promise(resolve => {
    if (!navigator.geolocation) return resolve({ latitude: 6.9271, longitude: 79.8612 });
    navigator.geolocation.getCurrentPosition(
      p => resolve({ latitude: p.coords.latitude, longitude: p.coords.longitude }),
      () => resolve({ latitude: 6.9361, longitude: 79.8540 })
    );
  });
  await api(API.order, "/api/delivery/" + id + "/location", { method: "PUT", body: JSON.stringify(loc) });
  loadCurrent();
};

async function loadHistory() {
  const mine = await api(API.order, "/api/delivery/mine");
  document.getElementById("history").innerHTML = mine.map(d => `
    <article class="card item"><h4>Delivery #${d.id}</h4><div class="muted">${d.deliveryStatus}</div></article>
  `).join("") || "<p class='muted'>No history.</p>";
}

async function loadProfile() {
  const p = await api(API.order, "/api/delivery/profile");
  document.getElementById("st").value = p.availabilityStatus;
}
document.getElementById("saveSt").onclick = async () => {
  try {
    await api(API.order, "/api/delivery/status", {
      method: "PUT",
      body: JSON.stringify({ status: document.getElementById("st").value })
    });
    toast(document.getElementById("stMsg"), "Updated", true);
  } catch (err) {
    toast(document.getElementById("stMsg"), err.message);
  }
};

async function loadNotes() {
  const notes = await api(API.order, "/api/notifications");
  document.getElementById("notes").innerHTML = notes.map(n => `
    <article class="card item"><h4>${n.notificationType}</h4><div class="muted">${n.message}</div></article>
  `).join("") || "<p class='muted'>No notifications.</p>";
}

loadJobs();
