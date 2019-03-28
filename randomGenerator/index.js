
const faker = require('faker');
const ObjectsToCsv = require('objects-to-csv');
const lorem = require("lorem-ipsum").loremIpsum;
const amount = 1000000;

let artists = [{ name: "name", year: "year", country: "country" }];
let albums = [{ name: "name", year: "year", artist: "artist" }];
let artistNames = [];
let artistNamesMatches = {};

function str_hash(str) {
    var hash = 0, i, chr;
    if (str.length === 0) return hash;
    for (i = 0; i < str.length; i++) {
        chr = str.charCodeAt(i);
        hash = ((hash << 5) - hash) + chr;
        hash |= 0; // Convert to 32bit integer
    }
    return hash;
};

function getArtistName() {
    let name = faker.name.findName()
    let hash = str_hash(name);
    let match = artistNames[hash];
    if (match === undefined) {
        artistNames[hash] = name;
        return name;
    }
    if (artistNamesMatches[hash] === undefined) {
        artistNamesMatches[hash] = 1;
    } else {
        artistNamesMatches[hash]++;
    }
    name += " & ";
    do {
        let num = getRandomArbitrary(33, 126);
        if (num == 44 || num == 92) num++;
        name += String.fromCharCode(num);
        hash = str_hash(name);
        match = artistNames[hash];
        if (match === undefined) {
            artistNames[hash] = name;
            return name;
        }
    } while (true);
}


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
        let name = getArtistName();
        let year = artistDate[index];
        artists.push({
            name: name,
            year: year,
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
        let name = lorem({
            count: albumWordCount[index],
            units: "words"
        });
        let year = albumDate[index];
        albums.push({
            name: name,
            artist: "",
            year: year
        });
    }
    console.log("album base done");

    buildReference();
}

function buildReference() {

    let artistsPerAlbum = getRandomRange(1, 3, amount);

    for (let index = 1; index < amount + 1; index++) {
        const artistsForThisAlbum = artistsPerAlbum[index - 1];
        const album = albums[index];

        let artistIndexes = getRandomRange(1, amount, artistsForThisAlbum);
        let albumArtist = [];

        for (let index2 = 0; index2 < artistsForThisAlbum; index2++) {
            const artistIndex = artistIndexes[index2];
            let artistName = artists[artistIndex].name;
            albumArtist.push(artistName);
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

        let num = 0;
        let i = 0;
        let keys = Object.keys(artistNamesMatches);
        keys.forEach((element) => {
            if (artistNamesMatches[element] > num) {
                num = artistNamesMatches[element];
                i = element;
            }
        });
        console.log("most used name '" + artistNames[i] + "' with: " + num);


    })();
}

// start
genArtists();