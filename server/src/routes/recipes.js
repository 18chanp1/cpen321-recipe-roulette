var express = require('express');
var router = express.Router();
var fetch = require('node-fetch');
var dbFunctions = require('../../db/db').Functions;
var dbModels = require('../../db/db').Models;
const fs = require('fs');
const apiKey = fs.readFileSync("./secrets/api_key.txt", "utf8");

router.get('/', async function(req, res, next) {
  let userId = req.headers.email;
  if (!userId) {
    res.status(400);
    res.send("Missing user email");
    return;
  }
  // Find all ingredients of user
  let allIngredients = await dbFunctions.dbFindRecord(dbModels.Ingredient, {userId});
  if (!allIngredients || allIngredients.ingredients.length == 0) {
    res.status(200);
    res.send([]);
    return;
  }
  // Push into a list
  let ingredientList = [];
  allIngredients.ingredients.forEach(ingredient => {
    ingredientList.push(ingredient.name);
  });
  // Get recipes from api endpoint
  let endpoint = `https://api.spoonacular.com/recipes/findByIngredients?apiKey=${apiKey}&ingredients=${ingredientList}&number=5`;
  let apiResponse = await fetch(endpoint);
  if (apiResponse.status >= 300) {
    res.status(500);
    res.send("Cannot fetch recipes at this point");
  } else {
    let recipes = await apiResponse.json();
    res.status(200);
    res.send(recipes);
  }
});

router.post('/', async function(req, res, next) {
  let userId = req.body.userId;
  let recipeId = req.body.recipeId;
  if (!userId || !recipeId) {
    res.status(400);
    res.send("Request body parameters must not be empty");
    return;
  }
  let savedRecipe = await dbFunctions.dbFindRecord(dbModels.Recipe, {
    userId, 
    recipeId
  });
  if (savedRecipe) {
    savedRecipe.numTimes++;
  } else {
    let summaryEndpoint = `https://api.spoonacular.com/recipes/${recipeId}/summary?apiKey=${apiKey}`;
    let recipeSummary = "";
    let recipeName = "";
  
    let apiResponse = await fetch(summaryEndpoint);
    if (apiResponse.status >= 300) {
      res.status(500);
      res.send("Cannot save chosen recipe at this point");
      return;
    }
    let resJson = await apiResponse.json();
    recipeSummary = resJson.summary;
    recipeName = resJson.title;
  
    savedRecipe = new dbModels.Recipe({ 
      userId,
      recipeId,
      recipeSummary,
      recipeName,
      numTimes: 1,
      likes: 0
    });
  }
  await dbFunctions.dbSaveRecord(savedRecipe);

  let instrEndpoint = `https://api.spoonacular.com/recipes/${recipeId}/analyzedInstructions?apiKey=${apiKey}`;
  let apiResponse = await fetch(instrEndpoint);
  if (apiResponse.status >= 300) {
    res.status(500);
    res.send("Cannot fetch instructions at this point");
    return;
  }
  let resJson = await apiResponse.json();
  savedRecipe.instructions = resJson;
  let response = {
    userId: savedRecipe.userId,
    recipeId: savedRecipe.recipeId,
    recipeName: savedRecipe.recipeName,
    recipeSummary: savedRecipe.recipeSummary,
    numTimes: savedRecipe.numTimes,
    instructions: resJson,
  }
  res.status(200);
  res.send(response);
});

module.exports = router;
