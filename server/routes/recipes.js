var express = require('express');
var router = express.Router();
var fetch = require('node-fetch');
var Models = require('../utils/db');
var removeIngredients = require('../utils/ingredientManager');
var url = require('url');
const fs = require('fs');
const apiKey = fs.readFileSync("api_key.txt", "utf8");
const { default: mongoose } = require('mongoose');

submitReview = async (req) => {
  console.log(req.body);
  let newReview = new Models.Review({
    reviewId: new mongoose.Types.ObjectId(),
    userId: req.body.email,
    recipeName: req.body.recipeName,
    reviewTitle: req.body.title,
    reviewText: req.body.text,
    likes: 0
  })
  await newReview.save();
  return true;
}

getRecipes = async (req) => {
  //let ingredientList = url.parse(req.url, true).query.ingredients;
  // let user = url.parse(req.url, true).query.user;
  let user = "test@ubc.ca"
  // Find all ingredients of user
  let allIngredients = await Models.Ingredient.findOne({userId: `${user}`});
  if (allIngredients == null) {
    return null;
  };
  // Push into a list
  let ingredientList = [];
  allIngredients.ingredients.forEach(ingredient => {
    ingredientList.push(ingredient.name);
  });
  //let ingredientList = ["flour","rice","bread","egg"];
  console.log(typeof ingredientList);
  console.log(ingredientList);
  // Get recipes from api endpoint
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
  let userId = req.body.email;
  let chosenRecipe = req.body.chosenRecipe;
  let ingredientList = req.body.ingredientList;
  console.log(user);
  const recipes = await Recipe.find({user: `${user}`});
  console.log(typeof recipes);
  console.log(recipes);
  recipes.recipeNames.push(chosenRecipe);
  // Delete ingredients
  await removeIngredients(userId, ingredientList);
  // Save chosen recipe into db
  await recipes.save();
  return true;
}

router.get('/', async function(req, res, next) {
  let recipes = await getRecipes(req);
  res.send(recipes);
});

router.post('/', async function(req, res, next) {
  let result = await saveRecipes(req);
  res.send(result);
});

router.post('/submitreview', async function(req, res, next) {
  let result = await submitReview(req);
  res.send(result);
});

module.exports = router;
