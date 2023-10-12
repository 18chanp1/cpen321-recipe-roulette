var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
  reviews = 
  [
    {
      id: "1",
      rating: "5",
      author: "Julia Rubin",
      title: "How to cook human meat",
      stringDate: "2018-10-16",
      image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg"
    },

    {
      id: "2",
      rating: "5",
      author: "Julia Rubin",
      title: "How to spitroast human meat",
      stringDate: "2018-10-16",
      image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg"
    }
  ]
  res.send(reviews);
});


module.exports = router;
