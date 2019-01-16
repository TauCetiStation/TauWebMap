var map = L.map('map', {
    zoomControl: false,
    attributionControl: false,
    minZoom: 3,
    maxZoom: 6,
    maxBounds: [[0, 0], [-255, 255]],
    crs: L.CRS.Simple
}).setView([-128, 128], 4);

fetch('/revision', {method: 'GET'}).then(function (response) {
    if (!response.ok) {
        document.getElementById('revision-error').style.display = 'initial';
        throw Error();
    }
    return response.text();
}).then(function (currentRevision) {
    console.log('Current map revision: ' + currentRevision);
    fetch('/revision/history', {method: 'GET'}).then(function (response) {
        return response.text();
    }).then(function (history) {
        var baseLayer = createTileLayer(currentRevision);
        var stations = { Current: baseLayer };

        history.split('\n').forEach(function (historyLine) {
            var historyLineParams = historyLine.split(' ');
            stations[historyLineParams[0]] = createTileLayer(historyLineParams[1]);
        });

        map.addLayer(baseLayer);
        L.control.layers(stations).addTo(map);
    });
});

L.control.zoom({position: 'topleft'}).addTo(map);

function createTileLayer(id) {
    return L.tileLayer('/tiles/{id}/{z}/{y}/{x}', {id: id, maxNativeZoom: 5, maxZoom: 6});
}