var express = require('express');
var router = express.Router();
var dbFunctions = require("../../db/db").Functions;
var dbModels = require("../../db/db").Models;

router.get('/', async function(req, res, next) {
  let response = [];
  let allRecipes = await dbFunctions.dbGetAllReviews();
  allRecipes.forEach(recipe => {
    let review = {
      id: recipe.recipeId,
      rating: recipe.likes,
      author: recipe.userId,
      title: recipe.recipeName,
      image: "https://spoonacular.com/recipeImages/73420-312x231.jpg",
      review: recipe.recipeSummary
    }
    response.push(review);
  })
  res.status(200);
  res.send(response);
});

router.post("/like", async (req, res, next) =>
{

  let review = await dbFunctions.dbFindRecord(dbModels.Recipe, 
    { 
      userId: req.body.email, 
      recipeId: req.body.id
    });
  if (review != null) {
    review.likes++;
    await dbFunctions.dbSaveRecord(review);
    res.status(200);
    res.send(review);
  } else {
    res.status(400);
    res.send("Failed to like");
  }
})


module.exports = router;
