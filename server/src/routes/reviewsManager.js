var express = require('express');
var router = express.Router();
var dbFunctions = require("../../db/db").Functions;
var dbModels = require("../../db/db").Models;
const { randomUUID } = require('crypto');

router.get('/', async function(req, res, next) {
  let response = [];
  let allRecipes = await dbFunctions.dbGetAllReviews();
  allRecipes.forEach(recipe => {
    let dup = false;
    for (let r of response) {
      if (recipe.recipeId == r.id && recipe.userId == r.author) {
        dup = true;
        break;
      }
    }
    if (!dup) {
      let review = {
        id: recipe.recipeId,
        rating: recipe.likes,
        author: recipe.userId,
        title: recipe.recipeName,
        image: recipe.recipeImage,
        review: recipe.recipeSummary
      }
      response.push(review);
    }
  })
  console.log(response)
  res.status(200);
  res.send(response);
});

router.post("/like", async (req, res, next) =>
{
  let userId = req.body.email;
  let recipeId = req.body.id;
  if (!userId || !recipeId) {
    res.status(400);
    res.send("Body parameters must not be empty");
  }
  let reviews = await dbFunctions.dbFindAllRecords(dbModels.Recipe, 
    { 
      userId,
      recipeId
    });
  if (reviews.length > 0) {
    let r = 0;
    let maxLikes = 0;
    for (let i = 0; i < reviews.length; i++) {
      if (reviews[i].likes > maxLikes) {
        maxLikes = reviews[i].likes;
        r = i;
      }
    }
    let review = reviews[r];
    review.likes++;
    await dbFunctions.dbSaveRecord(review);
    res.status(200);
    res.send(review);
  } else {
    res.status(400);
    res.send("Specified review does not exist");
  }
})

router.post("/custom", async (req, res, next) =>
{
  let recipeName = req.body.recipeName;
  let userId = req.body.userId;
  let recipeSummary = req.body.recipeSummary;
  if (!recipeName || !userId || !recipeSummary) {
    res.status(400);
    res.send("Body parameters must not be empty");
    return;
  }
  let recipeImage = "https://visitors-centre.jrc.ec.europa.eu/sites/default/files/thumbnail/kmffq_additional-illo4video_2019_5_fishplate%5B1%5D.jpg"
  let recipeId = randomUUID();
  let savedRecipe = new dbModels.Recipe({ 
    userId,
    recipeId,
    recipeSummary,
    recipeImage,
    recipeName,
    numTimes: 1,
    likes: 0
  });
  await dbFunctions.dbSaveRecord(savedRecipe);
  res.status(200);
  res.send(savedRecipe);
})


module.exports = router;
