var express = require('express');
var router = express.Router();

const fs = require('fs');

/* GET home page. */
router.get('/', function(req, res, next) {
    console.log("loading assetlinks.json")
    fs.readFile('./.well-known/assetlinks.json', (err, json) => {
        let obj = JSON.parse(json);
        res.json(obj);
    });
});

module.exports = router;