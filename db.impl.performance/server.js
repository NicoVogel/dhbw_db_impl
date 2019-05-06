'use strict';

//https://itnext.io/how-to-use-netflixs-eureka-and-spring-cloud-for-service-registry-8b43c8acdf4e
//https://nodejs.org/en/docs/guides/nodejs-docker-webapp/

const randomGen = require('./randomGen');

const express = require('express');
const Eureka = require('eureka-js-client').Eureka;

const PORT = 3000;
const HOST = '0.0.0.0';
const app = express();

const client = new Eureka({
    instance: {
        app: 'performance',
        hostName: 'localhost',
        ipAddr: HOST,
        statusPageUrl: 'http://0.0.0.0:3000',
        vipAddress: 'performance',
        port: {
            $: PORT,
            '@enabled': 'true'
        },
        dataCenterInfo: {
            '@class': 'com.netflix.appinfo.InstanceInfo$DefaultDataCenterInfo',
            name: 'Performance'
        },
        registerWithEureka: true,
        fetchRegistry: true
    },
    eureka: {
        host: 'http://eureka',
        port: 8761,
        servicePath: '/eureka'
    }
});

client.logger.level('debug');
// client.start(error => {
//     console.log(error || 'NodeJS Eureka Started!');

//     app.get('/', (req, res) => {
//         res.send('Hello from NodeJS Eureka Client\n');
//         res.end();
//     });

//     app.post('/', () => {
//         randomGen.startgen(10);
//     });
// });

app.listen(PORT, HOST);

console.log(`Running on http://${HOST}:${PORT}`);
randomGen.startgen(10);