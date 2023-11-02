var express = require('express');
var router = express.Router();
var Models = require("../utils/db");

getAllReviews = async () => {
  let response = [];
  let allRecipes = await Models.Recipe.find().limit(10);
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

likeRecipe = async (req) => {
  //console.log(req.body);
  let review = await Models.Recipe.findOne({
    userId: `${req.body.email}`, 
    recipeId: `${req.body.id}`
  });
  //console.log(review);
  if (review !== null) {
    review.likes++;
    await review.save();
    return "Liked";
  }
  return "Failed to like";
}

/* GET users listing. */
router.get('/', async function(req, res, next) {
  reviewsTest = 
  [
    {
      id: "1",
      rating: "5",
      author: "Julia Rubin",
      title: "How to cook human meat",
      stringDate: "2018-10-16",
      image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg",
      review: 
      `
      Lorem ipsum dolor sit amet, consectetur adipiscing elit. In fringilla, odio in malesuada maximus, est lectus interdum augue, lacinia placerat mi dui eu purus. Suspendisse facilisis tellus a ex pulvinar, in lacinia neque iaculis. Quisque accumsan justo non diam pulvinar ullamcorper. Maecenas ac sem mi. Donec sagittis condimentum ligula a dapibus. Nunc convallis magna sapien, non dictum metus viverra nec. Curabitur auctor aliquam dolor, vel interdum purus luctus quis. Mauris condimentum urna diam, sit amet congue nulla venenatis ut. Etiam malesuada, magna nec lobortis tincidunt, ligula tortor bibendum tellus, ac mattis nisl enim id sapien. Pellentesque sodales commodo varius. Mauris purus orci, semper ut libero at, malesuada interdum eros. Nam eu purus convallis urna imperdiet vulputate sit amet vel ipsum. Suspendisse at nisl leo. Vivamus in volutpat purus. Aenean vitae arcu mauris.
      Fusce iaculis malesuada odio, a laoreet nulla gravida laoreet. Duis gravida non nulla non fermentum. Pellentesque quis pharetra erat, quis varius odio. Nunc viverra in nulla quis ultricies. Ut pellentesque tempor lectus, nec dignissim felis viverra ac. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Nunc id odio odio. Morbi semper, nulla sit amet mattis mattis, augue erat vehicula mauris, a consequat nibh lectus ut odio. Nulla non nibh ligula. Donec viverra ornare aliquet. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia curae; 
      `
    },

    {
      id: "2",
      rating: "5",
      author: "Julia Rubin",
      title: "How to spitroast human meat",
      stringDate: "2018-10-16",
      image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg"
    }
  ]


  //res.status(511).send()
  //res.send(reviews);
  let reviews = await getAllReviews();
  res.send(reviews);
});

router.post("/like", async (req, res, next) =>
{
  console.log("like");
  let result = await likeRecipe(req);
  res.send(result);
})


module.exports = router;
