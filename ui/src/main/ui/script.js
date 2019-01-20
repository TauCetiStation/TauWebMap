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
L.control.attribution({
    position: 'bottomright',
    prefix: '<a href="https://taucetistation.org/">TauCeti</a>'
}).addAttribution('Made with ♥').addTo(map);

fetch('/revisions', {method: 'GET'}).then(response => {
    if (!response.ok)
        throw Error('Unable to get map revisions');
    return response.text();
}).then(handleRevisions).catch(reason => {
    console.log(reason);
    document.getElementById('revision-error').style.display = 'initial';
});

function handleRevisions(revisions) {
    console.log(`Current map revisions:\n ${revisions}`);

    let stations = {};

    revisions.split('\n').forEach(revisionLine => {
        let revision = revisionLine.split(' ');
        stations[revision[0]] = createTileLayer(revision[1]);
    });

    map.addLayer(stations[Object.keys(stations)[0]]);
    L.control.layers(stations).addTo(map);
}

function createTileLayer(id) {
    return L.tileLayer('/tiles/{id}/{z}/{y}/{x}', {id: id, maxNativeZoom: 5, maxZoom: 6});
}
