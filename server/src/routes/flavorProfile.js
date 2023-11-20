var express = require('express');
var router = express.Router();
var dbFunctions = require('../../db/db').Functions;
var dbModels = require('../../db/db').Models;

router.get('/', async function(req, res, next) {
  let userId = req.headers.email;
  if (!userId) {
    res.status(400);
    res.send("Missing user email");
    return;
  }
  let recipes = await dbFunctions.dbFindAllRecords(dbModels.Recipe, {userId});
  let flavorProfile = "No Flavor Profile available";
  if (recipes.length == 0) {
    res.status(200);
    res.send(flavorProfile);
    return;
  }
  let meat = 0;
  let veggie = 0;
  let pastry = 0;
  let meat_indicators = ["meat", "beef", "pork", "chicken", "salmon"];
  let pastry_indicators = ["pie", "strudel", "pastry", "croissant", "dessert"];
  recipes.forEach(recipe => {
    veggie++;
    for (let i = 0; i < meat_indicators.length; i++) {
      if (recipe.recipeName.toLowerCase().includes(meat_indicators[i])) {
        meat++;
        veggie--;
        break;
      } else if (recipe.recipeName.toLowerCase().includes(pastry_indicators[i])) {
        pastry++;
        veggie--;
        break;
      }
    }
  });
  if (meat >= veggie && meat >= pastry) {
    flavorProfile = "Meat Lover";
  } else if (pastry >= veggie && pastry >= meat) {
    flavorProfile = "Pastry Lover";
  } else {
    flavorProfile = "Veggie Lover"
  }
  res.status(200);
  res.send(flavorProfile);
});

module.exports = router;
