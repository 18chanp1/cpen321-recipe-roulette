var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
  reviews = 
  [
    {
      id: "1",
      rating: "5",
      author: "John Doe",
      title: "How to cook human meat",
      stringDate: "2018-10-16",
      image: "https://cpen321-reciperoulette.westus.cloudapp.azure.com/reviews/images/1"
    }
  ]
  res.send(reviews);
});

module.exports = router;
