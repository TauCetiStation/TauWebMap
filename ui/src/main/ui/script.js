'use strict';

const OVERLAYS_DEF = [{
    name: 'Pipes',
    value: 'pipes'
}, {
    name: 'Power net',
    value: 'power'
}, {
    name: 'Disposal',
    value: 'dispo'
}];

let map = L.map('map', {
    zoomControl: false,
    attributionControl: false,
    minZoom: 3,
    maxZoom: 6,
    maxBounds: [[0, 0], [-255, 255]],
    crs: L.CRS.Simple
}).setView([-128, 128], 4);

L.control.zoom({position: 'topleft'}).addTo(map);
L.control.attribution({
    position: 'bottomright',
    prefix: '<a href="https://taucetistation.org/">TauCeti</a>'
}).addAttribution('Made with ♥').addTo(map);

fetch('/revisions', {method: 'GET'}).then(response => response.text()).then(revisions => {
    console.log(`Current map revisions:\n${revisions}`);

    let stations = {};
    let overlays = {};

    revisions.split('\n').forEach(revisionLine => {
        let revArr = revisionLine.split(' ');
        let date = revArr[0];
        let revision = revArr[1];

        stations[date] = createLayer(revision, 'tiles');
        overlays[date] = {};

        OVERLAYS_DEF.forEach(overlay => {
            overlays[date][overlay.name] = createLayer(revision, overlay.value);
        });
    });

    let currentDate = Object.keys(stations)[0];

    map.addLayer(stations[currentDate]);
    let layerControl = L.control.layers(stations, overlays[currentDate]).addTo(map);

    map.on('baselayerchange', function (e) {
        let currentOverlay = overlays[currentDate];
        Object.keys(currentOverlay).forEach(layerName => {
            map.removeLayer(currentOverlay[layerName]);
            layerControl.removeLayer(currentOverlay[layerName]);
            layerControl.addOverlay(overlays[e.name][layerName], layerName);
        });
        currentDate = e.name;
    });
});

function createLayer(id, name) {
    return L.tileLayer(`/tiles/{id}/${name}/{z}/{y}/{x}?v=${window.VERSION}`, {
        id: id,
        maxNativeZoom: 5,
        maxZoom: 6
    })
}
