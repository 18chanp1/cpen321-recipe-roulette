var express = require('express');
var router = express.Router();
var mongodb = require('../db');
var url = require('url');
const { default: mongoose } = require('mongoose');
const recipeSchema = new mongoose.Schema({
  user: String,
  ingredients: Array
})
const Recipe = mongoose.model('Recipe', recipeSchema);

getFlavourProfile = async (req) => {
  mongodb.once('open', function() {
    console.log("We're connected to MongoDB!");
  });
  let queriedUser = url.parse(req.url, true).query.user;
  const recipes = await Recipe.find({user: `${queriedUser}`});
  console.log(typeof recipes);
  console.log(recipes);
  let meat = 0;
  let veggie = 0;
  recipes.forEach(recipe => {
    if (recipe.includes("meat") || recipe.includes("beef") || recipe.includes("pork") || recipe.includes("chicken") ) {
      meat++;
    } else {
      veggie++;
    }
  });
  if (meat > veggie) {
    return "Meat Lover";
  } else {
    return "Veggie Lover";
  }
}
router.get('/', async function(req, res, next) {
  let flavorProfile = await getFlavourProfile(req);
  res.send(flavorProfile);
});

module.exports = router;
