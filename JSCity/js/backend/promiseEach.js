var Promise = require('bluebird');

function foo(item, ms){ // note bluebird has a delay method
    return Promise.delay(ms, item)
            .then(console.log(item))
        // return new Promise (function (resolve, reject){
            // (console.log(item))
            // resolve();
        // });
}

var items = ['one', 'two', 'three', 4, 5, 6];

// Promise.map(items, function(item, i){
//     // console.log(i);
//     return foo(item, (items.length - i) * 1000)
//     // return i;
// },{concurrency: 1})
// .then (function (result){
//   console.log('Finish\t' + result);
// })

Promise.each(items, function(item, i){
    // console.log(i);
    return foo(item, (items.length - i) * 500)
    // return i;
})
.then (function (result){
  console.log('Finish\t' + result);
})