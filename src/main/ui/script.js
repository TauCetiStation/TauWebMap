var map = L.map('map', {
    zoomControl: false,
    attributionControl: false,
    minZoom: 3,
    maxZoom: 6,
    maxBounds: [[0, 0], [-250, 250]],
    crs: L.CRS.Simple
}).setView([-128, 128], 4);

fetch('/revision', {method: 'GET'}).then(function (response) {
    return response.text();
}).then(function (revision) {
    console.log("Current map revision: " + revision);
    map.addLayer(L.tileLayer('/tiles/{id}/{z}/{y}/{x}', {id: revision, maxNativeZoom: 5, maxZoom: 6}));
});

L.control.zoom({position: 'topleft'}).addTo(map);
