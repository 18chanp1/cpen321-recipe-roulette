var express = require('express');
var router = express.Router();
var Models = require("../utils/db");

let getAllReviews = async () => {
  let response = [];
  let allRecipes = await Models.Recipe.find().limit(20);
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
  console.log(response);
  return response;
}

let likeRecipe = async (req) => {
  //console.log(req.body);
  let review = await Models.Recipe.findOne({
    userId: `${req.body.email}`, 
    recipeId: `${req.body.id}`
  });
  //console.log(review);
  if (review !== null) {
    review.likes++;
    await review.save();
    return true;
  }
  return false;
}

/* GET users listing. */
router.get('/', async function(req, res, next) {
  let reviews = await getAllReviews();
  res.status(200);
  res.send(reviews);
});

router.post("/like", async (req, res, next) =>
{
  console.log("like");
  let result = await likeRecipe(req);
  if (result) {
    res.status(200);
    res.send("Recipe liked");
  } else {
    res.status(400);
    res.send("Failed to like");
  }
})


module.exports = router;
