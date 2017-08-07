var argv = require('minimist')(process.argv.slice(2));
console.dir(argv);

if (!argv.beep)
  throw new Error('This parameter does not exist');
else
  console.log(argv.beep);

// console.log(process.argv);
// var myArgs = process.argv.slice(2);
 // console.log('myArgs: ', myArgs);
