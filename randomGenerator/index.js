
const faker = require('faker');
const ObjectsToCsv = require('objects-to-csv');
const lorem = require("lorem-ipsum").loremIpsum;
const amount = 1000000;

let artists = [{ name: "name", year: "year", country: "country" }];
let albums = [{ name: "name", year: "year", artist: "artist" }];


function getRandomArbitrary(min, max) {
    return Math.floor(Math.random() * (max - min) + min);
}

function getRandomRange(min, max, num) {
    let numbers = [];
    for (let index = 0; index < num; index++) {
        numbers.push(getRandomArbitrary(min, max));
    }
    return numbers;
}


function genArtists() {

    let artistDate = getRandomRange(1700, 2019, amount);
    for (let index = 0; index < amount; index++) {
        artists.push({
            name: faker.name.findName(),
            year: artistDate[index],
            country: "??"
        });
    }
    console.log("artists done");
    genAlbums();
}

function genAlbums() {

    let albumDate = getRandomRange(1700, 2019, amount);
    let albumWordCount = getRandomRange(1, 5, amount);
    for (let index = 0; index < amount; index++) {
        albums.push({
            name: lorem({
                count: albumWordCount[index],
                units: "words"
            }),
            artist: "",
            year: albumDate[index]
        });
    }

    console.log("album base done");

    buildReference();
}

function buildReference() {

    let artistsPerAlbum = getRandomRange(1, 3, amount);
    for (let index = 0; index < amount; index++) {
        const artistsForThisAlbum = artistsPerAlbum[index];
        const album = albums[index];

        let artistIndexes = getRandomRange(1, amount, artistsForThisAlbum);

        let albumArtist = [];

        for (let index2 = 0; index2 < artistsForThisAlbum; index2++) {
            const artistIndex = artistIndexes[index2];
            albumArtist.push(artists[artistIndex].name);
        }

        album.artist = albumArtist.join(',');
    }

    console.log("album fully done")
    writeToFile();
}

function writeToFile() {
    // If you use "await", code must be inside an asynchronous function:
    (async () => {
        let csvArtist = new ObjectsToCsv(artists);
        let csvAlbum = new ObjectsToCsv(albums);
        console.log("start writing");

        // Save to file:
        await csvArtist.toDisk('./artist.csv');
        await csvAlbum.toDisk('./album.csv');

        console.log("done");
    })();
}

// start
genArtists();