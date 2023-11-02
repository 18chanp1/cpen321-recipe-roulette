var express = require('express');
var router = express.Router();
var Models = require('../utils/db');
var url = require('url');

getFlavourProfile = async (req) => {
  //let queriedUser = url.parse(req.url, true).query.email;
  let queriedUser = req.headers.email;
  console.log(queriedUser);
  let recipes = await Models.Recipe.find({userId: `${queriedUser}`});
  let meat = 0;
  let veggie = 0;
  let meat_indicators = ["meat", "beef", "pork", "chicken", "salmon"];
  // console.log(recipes);
  if (recipes === undefined || recipes === null || recipes.length == 0) {
    return "No Flavor Profile Available";
  }
  recipes.forEach(recipe => {
    console.log(recipe)
    veggie++;
    for (let i = 0; i < meat_indicators.length; i++) {
      if (recipe.recipeName.toLowerCase().includes(meat_indicators[i])) {
        meat++;
        veggie--;
        break;
      }
    }
  });
  if (meat >= veggie) {
    return "Meat Lover";
  } else {
    return "Veggie Lover";
  }
};

router.get('/', async function(req, res, next) {
  let flavorProfile = await getFlavourProfile(req);
  res.send(flavorProfile);
});

module.exports = router;
