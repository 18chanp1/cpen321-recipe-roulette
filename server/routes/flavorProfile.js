var express = require('express');
var router = express.Router();
var Models = require('../utils/db');
var url = require('url');

getFlavourProfile = async (req) => {
  let queriedUser = url.parse(req.url, true).query.user;
  const recipes = await Models.Recipe.find({user: `${queriedUser}`});
  console.log(typeof recipes);
  console.log(recipes);
  let meat = 0;
  let veggie = 0;
  let meat_indicators = ["meat", "beef", "pork", "chicken"];
  recipes.recipeNames.forEach(name => {
    veggie++;
    for (let i = 0; i < meat_indicators.length; i++) {
      if (name.includes(meat_indicators[i])) {
        meat++;
        veggie--;
        break;
      }
    }
  });
  if (meat > veggie) {
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
