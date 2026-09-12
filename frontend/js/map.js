window.MapKit = {
  map: null,
  markers: {},
  route: null,
  init(id, center = [6.9271, 79.8612]) {
    if (this.map) {
      this.map.remove();
      this.markers = {};
    }
    this.map = L.map(id).setView(center, 13);
    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution: "&copy; OpenStreetMap"
    }).addTo(this.map);
    return this.map;
  },
  pin(key, lat, lng, label) {
    if (lat == null || lng == null) return;
    if (this.markers[key]) this.map.removeLayer(this.markers[key]);
    this.markers[key] = L.marker([lat, lng]).addTo(this.map).bindPopup(label);
  },
  fit() {
    const points = Object.values(this.markers).map(m => m.getLatLng());
    if (points.length) this.map.fitBounds(L.latLngBounds(points).pad(0.3));
  }
};
