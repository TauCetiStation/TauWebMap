'use strict';

let map = L.map('map', {
    zoomControl: false,
    attributionControl: false,
    minZoom: 3,
    maxZoom: 6,
    maxBounds: [[0, 0], [-255, 255]],
    crs: L.CRS.Simple
}).setView([-128, 128], 4);

L.control.zoom({position: 'topleft'}).addTo(map);
L.control.attribution({position: 'bottomright', prefix: '<a href="https://taucetistation.org/">TauCeti</a>'}).addAttribution('Made with ♥').addTo(map);

fetch('/revision', {method: 'GET'}).then(response => {
    if (!response.ok)
        throw Error('Unable to get map revision');
    return response.text();
}).then(handleRevision).catch(reason => {
    console.log(reason);
    document.getElementById('revision-error').style.display = 'initial';
});

function handleRevision(currentRevision) {
    console.log(`Current map revision: ${currentRevision}`);

    fetch('/revision/history', {method: 'GET'}).then(response => response.text()).then(history => {
        let baseLayer = createTileLayer(currentRevision);
        let stations = {Current: baseLayer};

        history.split('\n').forEach(historyLine => {
            let historyLineParams = historyLine.split(' ');
            stations[historyLineParams[0]] = createTileLayer(historyLineParams[1]);
        });

        map.addLayer(baseLayer);
        L.control.layers(stations).addTo(map);
    });
}

function createTileLayer(id) {
    return L.tileLayer('/tiles/{id}/{z}/{y}/{x}', {id: id, maxNativeZoom: 5, maxZoom: 6});
}
