var express = require('express');
var router = express.Router();
var Models = require("../utils/db");

/* GET users listing. */
router.get('/', async function(req, res, next) {
  let response = [];
  let allRecipes = await Models.dbGetAllReviews();
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
  let review = await Models.dbFindRecipe(req.body.email, req.body.id);
  if (review !== undefined) {
    review.likes++;
    await Models.dbSaveRecord(review);
    res.status(200);
    res.send("Recipe liked");
  } else {
    res.status(400);
    res.send("Failed to like");
  }
})


module.exports = router;
