var Promise = require('bluebird');
var fs = Promise.promisifyAll(require('fs'));

var mysql       = require('mysql');
Promise.promisifyAll(require("mysql/lib/Connection").prototype);
Promise.promisifyAll(require("mysql/lib/Pool").prototype);

var path        = require('path');
var mime        = require('mime');
var config          = require('../config.json'),
    debug           = config.debug,
    CONST_ANONYMOUS = 'Anonymous function';
require('../lib/MooTools-Core-Server.js');


var argv = require('minimist')(process.argv.slice(2));


//Parse JSON
function parseCity(){

    var topPromises = [];

    //handle city
    if (city.hasOwnProperty('city')){
        var theCity = city.city;
        var p = new Promise(function(resolve, reject){
                    console.log('City exists ' + JSON.stringify(theCity));
                    var insert = 'INSERT INTO `tb_city`(`name`, `tooltip`) VALUES (?,?)';
                    var params = [theCity.name,theCity.tooltip];
                    var query   = mysql.format(insert, params);
                    // console.log(query);
                    resolve(query);
                });
        topPromises.push(p);
    }

    return topPromises;
}


//Parse JSON
function parseDistricts(){

    var topPromises = [];

    // handle districts
    if (city.hasOwnProperty('districts')){
    //   console.log('Districts: ' + city.districts.length);
        city.districts.forEach( function(district){
             if (district.hasOwnProperty('city')){//district belongs to a city)
                var p = new Promise(function(resolve, reject){
                            // console.log('\tDistrict: '+ district.name);
                            //find the city's id
                            var select   = 'SELECT id FROM tb_city WHERE name = ?';
                            var params  = [district.city];
                            var q   = mysql.format(select, params);
                            // console.log(q);
                            selectFromDB(q)
                                .then(function(cityID){
                                    if (cityID){
                                    // console.log(cityID);
                                    var insert = 'INSERT INTO `tb_district`(`name`, `color`, `tooltip`, `city_id`) VALUES (?,?,?,?)';
                                    var params = [district.name, district.color, district.tooltip, cityID];
                                    var q   = mysql.format(insert, params);
                                    resolve (q);
                                    }
                                })
                        });
                // console.log(p);
                topPromises.push(p);
             }
        });
    }
    // console.log(topPromisesCity +'\t'+ topPromisesDistrict);
    return (topPromises);
}


//Parse JSON
function parseSubDistricts(){

    var topPromises = [];

    // handle districts
    if (city.hasOwnProperty('districts')){
    //   console.log('Districts: ' + city.districts.length);
        city.districts.forEach( function(district){
            if (district.hasOwnProperty('district')){//district belongs to another district
                var p = new Promise(function(resolve, reject){
                            //find the district's id
                            var select = 'SELECT id FROM tb_district WHERE name = ?';
                            var params = [district.district];
                            var q      = mysql.format(select, params);
                            selectFromDB(q)
                                .then (function (districtID){
                                    if (districtID){
                                        var insert = 'INSERT INTO `tb_district`(`name`, `color`, `tooltip`, `district_id`) VALUES (?,?,?,?)';
                                        var params = [district.name, district.color, district.tooltip, districtID];
                                        var q   = mysql.format(insert, params);
                                        resolve(q);
                                    }
                                })
                        });
                topPromises.push(p);
             }
        });
    }
    return (topPromises);
}


//Parse JSON
function parseBuildings(){

    var topPromises = [];

    // handle districts
    if (city.hasOwnProperty('buildings')){
      console.log('Buildings: ' + city.buildings.length);
        city.buildings.forEach( function(building){
             if (building.hasOwnProperty('district')){//building belongs to a district)
                var p = new Promise(function(resolve, reject){
                            //find the district's id
                            var select = 'SELECT id FROM tb_district WHERE name = ?';
                            var params = [building.district];
                            var q      = mysql.format(select, params);
                            selectFromDB(q)
                                .then (function (districtID){
                                    if (districtID){
                                        var insert = 'INSERT INTO `tb_building`(`name`, `height`, `width`, `color`, `tooltip`, `district_id`) VALUES (?,?,?,?,?,?)';
                                        var params = [building.name, building.height, building.width, building.color, building.tooltip, districtID];
                                        var q   = mysql.format(insert, params);
                                        resolve(q);
                                    }
                                })
                        });
                topPromises.push(p);
             }
             else if (building.hasOwnProperty('city')){//building belongs to a city)
                var p = new Promise(function(resolve, reject){
                            //find the city's id
                            var select   = 'SELECT id FROM tb_city WHERE name = ?';
                            var params  = [building.city];
                            var q   = mysql.format(select, params);
                            // console.log(q);
                            selectFromDB(q)
                                .then(function(cityID){
                                    if (cityID){
                                        var insert = 'INSERT INTO `tb_building`(`name`, `height`, `width`, `color`, `tooltip`, `city_id`) VALUES (?,?,?,?,?,?)';
                                        var params = [building.name, building.height, building.width, building.color, building.tooltip, cityID];
                                        var q   = mysql.format(insert, params);
                                        resolve(q);
                                    }
                                })
                        });
                topPromises.push(p);
             }
             else{
                throw new Error('No district or city found for building '+ building.name);
             }
        });
    }
    // console.log(topPromisesCity +'\t'+ topPromisesDistrict);
    return (topPromises);
}


//
function selectFromDB(command){
    var connection = null;
    // console.log(command);
    return pool.getConnectionAsync()
        .then(function (con){
            connection = Promise.promisifyAll(con);
            return connection.queryAsync(command);
        })
        .then(function (results){
            if (results.length<1){
                console.log('Not Found\t' + command);
                return null;
            }
            else{
                // console.log('Found\t' + results[results.length-1].id);
                return (results[results.length-1].id);
            }
        })
        .catch (function (err){
            console.log(err);
            throw err;
        })
        .finally(function(){
            if (connection){
                connection.release();
            }
        });
}


//
function insertToDB(command){
    var connection = null;
    // console.log(command);
    return pool.getConnectionAsync()
        .then(function (con){
            // console.log('Added\t' + command);
            connection = Promise.promisifyAll(con);
            return connection.queryAsync(command);
        })
        .then(function (result){
            if (!result){
                console.log('Not Inserted');
                 return null;
            }
            else{
                // console.log(result.insertId);
                return (result.insertId);
            }
        })
        .catch (function (err){
            console.log(err);
            throw err;
        })
        .finally(function(){
            if (connection){
                connection.release();
            }
        });
}


function doTranscations(data){
    var topPromises = [];

    data.forEach(function(sSQL){
        // console.log('Adding: ' + sSQL);
        var p = insertToDB(sSQL);
        topPromises.push(p);
    });
    return topPromises;
}


function processPromises(promisesJSON){
    return new Promise(function(resolve, reject){
            Promise.all(promisesJSON)
            .then(doTranscations)
            .then(function (promisesInsertions){
                Promise.all(promisesInsertions)
                  .then(function(results){
                    console.log('Added\t' + results);
                    resolve()
                  });
             })
    });
}


try{
  if (!argv.f)
    throw new Error('Error: Input file not given; Aborting execution!');
}
catch (ex){
  console.log(ex);
  return;
}
var city     = require(argv.f);
var pool     = Promise.promisifyAll(mysql.createPool(config.conexoes[config.conexao]));


Promise.resolve()
    .then(parseCity)
    .then(processPromises)
    .then(parseDistricts)
    .then(processPromises)
    .then(parseSubDistricts)
    .then(processPromises)
    .then(parseBuildings)
    .then(processPromises)
    .then(function(){
        pool.end(function (err) {
            if (err)
                console.error("An error occurred: " + err);
            else
                console.log("Connection to DB terminated successfully");
        });
    })
   .catch(function(error) {
       console.log(error);
   });
