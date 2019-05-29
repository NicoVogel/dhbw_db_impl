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
  limit: 6,
  iterations: 1000
};

flows.forEach(flow => {
  benchrest(flow, runOptions)
    .on('error', function (err, ctxName) {
      // console.error('Failed in %s with err: ', ctxName, err);
    })
    .on('end', function (stats, errorCount) {
      console.log('\n');
      console.log(`URL ${flow.main[0].get}`);
      console.log('error count: ', errorCount);
      console.log(`min ${stats.main.histogram.min.toFixed(5)}`);
      console.log(`avg ${stats.main.histogram.mean.toFixed(5)}`);
      console.log(`max ${stats.main.histogram.max.toFixed(5)}`);
      console.log(`\nsum ${stats.main.histogram.sum.toFixed(5)}`);
    });
})




/*
5000

URL http://5.189.152.84:8080/db/performance/index
error count:  0
min 165.54725
avg 340.74779
max 1314.04079

sum 1703738.96501


URL http://5.189.152.84:8080/db/performance/
error count:  0
min 87.85505
avg 1430.00560
max 7625.97066

sum 7150028.01171
*/