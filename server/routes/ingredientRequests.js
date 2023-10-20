var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
  reviews = 
  [
    {
        reqID : "69",
        reqDate: "2018-10-16",
        expiryDate: "2018-10-20",
        ingredientID: "420",
        ingredientName: "Human Meat",
        requestor: "Julia Rubin",
        image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg",
        requestorID: "1"
    },

    {
        reqID : "2",
        reqDate: "2018-10-16",
        expiryDate: "2018-10-20",
        ingredientID: "420",
        ingredientName: "Human Meat",
        requestor: "Julia Rubin",
        image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg",
        requestorID: "1"
    },
  ]
  res.send(reviews);
});


module.exports = router;
