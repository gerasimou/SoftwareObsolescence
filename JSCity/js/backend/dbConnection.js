var mysql     = require('promise-mysql');

var config    = require('../config.json'),

pool      = mysql. createPool(config.conexoes[config.conexao]);


function getSqlConnection() {
  return pool.getConnection().disposer(function(connection) {
    pool.releaseConnection(connection);
  });
}
 
module.exports = getSqlConnection;