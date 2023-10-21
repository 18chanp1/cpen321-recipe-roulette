var express = require('express');
var router = express.Router();

/* GET users listing. */

router.post('/new', function(req, res, next) {
    console.log(req.body);
    res.status(200).send("success");
});

router.get("/self", (req, res, next) =>
{
  reviews = 
  [
    {
        reqID : "69",
        reqDate: "2018-10-16",
        expiryDate: "2018-10-20",
        ingredientID: "420",
        ingredientName: "Elephant penis",
        requestor: "Valentino Jaber",
        image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg",
        requestorID: "1"
    },

    {
        reqID : "2",
        reqDate: "2018-10-16",
        expiryDate: "2018-10-20",
        ingredientID: "420",
        ingredientName: "Horse penis",
        requestor: "Valentino Jaber",
        image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg",
        requestorID: "1"
    },
  ]
  res.send(reviews); 
})

router.get("/self/delete", (req, res, next) =>
{
  res.status(200).send()
})

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

router.post('/', function(req, res, next) {
    console.log(req.body);
    res.status(200).send("success");
});

module.exports = router;
