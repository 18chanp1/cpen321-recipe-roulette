var express = require('express');
var router = express.Router();
var fetch = require('node-fetch');
var Recipe = require('../db');
var url = require('url');
const fs = require('fs');
const apiKey = fs.readFileSync("api_key.txt", "utf8");

getRecipes = async (req) => {
  let ingredientList = url.parse(req.url, true).query.ingredients;
  console.log(typeof ingredientList);
  console.log(ingredientList);
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

saveRecipes = async (req) => {
  let user = url.parse(req.url, true).query.user;
  let chosenRecipe = url.parse(req.url, true).query.chosenRecipe;
  console.log(user);
  const recipes = await Recipe.find({user: `${user}`});
  console.log(typeof recipes);
  console.log(recipes);
  recipes.recipeNames.push(chosenRecipe);
  await recipes.save();
  return true;
}

router.get('/', async function(req, res, next) {
  let recipes = await getRecipes(req);
  res.send(recipes);
});

router.post('/', async function(req, res, next) {
  let result = await saveRecipes(req);
  res.send(recipes);
});

module.exports = router;
