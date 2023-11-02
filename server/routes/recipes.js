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
  let user = url.parse(req.url, true).query.email;
  //let user = "test@ubc.ca"
  // Find all ingredients of user
  let allIngredients = await Models.Ingredient.findOne({userId: `${user}`});
  if (allIngredients == null) {
    return [];
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
    return [];
  }
}

saveRecipes = async (req) => {
  let userId = req.body.userId;
  let chosenRecipeId = req.body.recipeId;
  let summaryEndpoint = `https://api.spoonacular.com/recipes/${chosenRecipeId}/summary?apiKey=${apiKey}`;
  let summary = "";
  let name = "";
  try {
    let res = await fetch(summaryEndpoint);
    console.log(res);
    let resJson = await res.json();
    console.log(resJson);
    summary = resJson.summary;
    name = resJson.title;
  } catch (error) {
    console.log(error);
  }
  let response = {
    userId: userId,
    recipeId: chosenRecipeId,
    recipeSummary: summary,
    recipeName: name,
    likes: 0
  }
  let savedRecipe = new Models.Recipe(response);
  await savedRecipe.save();
  // Get instructions
  let instrEndpoint = `https://api.spoonacular.com/recipes/${chosenRecipeId}/analyzedInstructions?apiKey=${apiKey}`;
  try {
    let res = await fetch(instrEndpoint);
    console.log(res)
    let resJson = await res.json();
    console.log(resJson);
    response.instructions = resJson;
  } catch (error) {
    console.log(error);
  }
  // Delete ingredients

  //await removeIngredients(userId, ingredientList);
  console.log(response);
  return response;
}

router.get('/', async function(req, res, next) {
  let recipes = await getRecipes(req);
  res.send(recipes);
});

router.post('/', async function(req, res, next) {
  let result = await saveRecipes(req);
  res.send(result);
});

// router.post('/submitreview', async function(req, res, next) {
//   let result = await submitReview(req);
//   res.send(result);
// });

module.exports = router;
