var benchrest = require('bench-rest');

const flows = [
    {
        main: [{
            get: 'http://5.189.152.84:8080/db/performance/index'
        },]
    },
    {
        main: [{
            get: 'http://5.189.152.84:8080/db/performance/'
        },]
    }
];

const runOptions = {
    limit: 1000,
    iterations: 100
};

flows.forEach(flow => {
    benchrest(flow, runOptions)
        .on('error', function (err, ctxName) {
            // console.error('Failed in %s with err: ', ctxName, err);
        })
        .on('end', function (stats, errorCount) {
            console.log('\n');
            console.log(URL ${ flow.main[0].get });
            console.log('error count: ', errorCount);
            console.log(min ${ stats.main.histogram.min.toFixed(5) });
            console.log(avg ${ stats.main.histogram.mean.toFixed(5) });
            console.log(max ${ stats.main.histogram.max.toFixed(5) });
            console.log(\nsum ${ stats.main.histogram.sum.toFixed(5) });
        });
})