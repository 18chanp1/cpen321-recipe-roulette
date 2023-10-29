var express = require('express');
var router = express.Router();
var fetch = require('node-fetch');
var mongodb = require('../db');
var url = require('url');
const fs = require('fs');
const apiKey = fs.readFileSync("api_key.txt", "utf8")


getRecipes = async (req) => {
  mongodb.once('open', function() {
    console.log("We're connected to MongoDB!");
  });
  let ingredientList = url.parse(req.url, true).query.ingredients;
  console.log(typeof ingredientList);
  console.log(ingredientList)
  //let ingredientList = ["flour","rice","bread","egg"];
  let endpoint = `https://api.spoonacular.com/recipes/findByIngredients?apiKey=${apiKey}&ingredients=${ingredientList}&number=5`;
  let recipes;
  try {
    let res = await fetch(endpoint);
    recipes = await res.json();
    console.log(recipes);
    return recipes;
  } catch (error) {
    console.log(error);
    return null;
  }
}
router.get('/', async function(req, res, next) {
  let recipes = await getRecipes(req);
  res.send(recipes);
});

module.exports = router;
