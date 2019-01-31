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
L.control.attribution({
    position: 'bottomleft',
    prefix: ''
}).addAttribution('<b><a href="https://collage.taucetistation.org/#2017" target="_blank">Collage</a></b> | <b><a href="https://map-archive.taucetistation.org/#2017" target="_blank">Archive</a></b>').addTo(map);

let stations = {};
let overlays = {};

window.REVISIONS.split(',').forEach(revisionLine => {
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

map.on('baselayerchange', e => {
    let currentOverlay = overlays[currentDate];
    let nextOverlay = overlays[e.name];

    for (const layerName in currentOverlay) {
        if (map.hasLayer(currentOverlay[layerName])) {
            map.addLayer(nextOverlay[layerName]);
        }

        map.removeLayer(currentOverlay[layerName]);
        layerControl.removeLayer(currentOverlay[layerName]);
        layerControl.addOverlay(nextOverlay[layerName], layerName);
    }

    document.getElementsByName('leaflet-base-layers').forEach(el => {
        // Workaround for weird bug. When we change base layer, radiobutton, even if in markup it's checked, remains unselected.
        if (el.checked) setTimeout(() => el.click());
    });

    currentDate = e.name;
});

function createLayer(id, name) {
    return L.tileLayer(`/tiles/{id}/${name}/{z}/{y}/{x}?v=${window.VERSION}`, {
        id: id,
        maxNativeZoom: 5,
        maxZoom: 6
    })
}
