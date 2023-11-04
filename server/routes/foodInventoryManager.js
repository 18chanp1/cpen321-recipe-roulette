const express = require('express');
var router = express.Router();
var Models = require('../utils/db');
var url = require('url');
const bodyParser = require('body-parser');


// Use body-parser middleware to parse request bodies
router.use(bodyParser.json());

router.post('/upload', async (req, res) => {
  // Get the user ID token and ingredients from the request body
  const {userId, ingredients} = req.body;

  // Create a new document
  const ingredientDoc = new Models.Ingredient({ userId , ingredients });

  // Save the document to MongoDB
  try {
    await ingredientDoc.save();
    res.status(200).send('Successfully saved to database');
  } catch (err) {
    console.error(err);
    res.status(500).send('Error saving to database');
  }

});


router.get('/', async function(req, res, next) {
  try {
    // mongodb.once('open', function() {
    //   console.log("We're connected to MongoDB!");
    // });


    //TODO, I think this should work, but please test it. 
    let queriedUser = req.get('userId');
    const ingredients = await Models.Ingredient.find({userId: `${queriedUser}`});
    console.log(typeof ingredients);
    console.log(ingredients);

    res.send(ingredients);
  } catch (err) {
    console.error(err);
    res.status(500).send(err.message);
  } finally {
    console.log('Finished processing request');
  }
});

// PUT: given a user and a list of ingredients, if the ingredient exists, decrement the counter or delete the food item
router.put('/update', async (req, res) => {
  let logOne = 1;
  let logTwo = 2;
  console.log(logOne + logTwo);
  try {
    const { userId, ingredients } = req.body;

    await Promise.all(ingredients.map(async (ingredientName) => {    
      const userIngredient = await Models.Ingredient.findOne({ userId });
      
      if (!userIngredient) {
        console.log(`User with userId ${userId} not found`);
        return res.status(404).send('User not found');
      } else {
        console.log("Get userId: " + userIngredient.userId);
      }

      const ingredient = userIngredient.ingredients.find(ing => ing.name === ingredientName);
      
      if (ingredient) {
        if (ingredient.count > 1) {
          // Decrement count if it's more than 1
          console.log('Ingredient Name: ' + ingredientName);
          console.log('Count: ' + ingredient.count);
          ingredient.count -= 1;
          console.log('Count after: ' + ingredient.count);
        } else {
          // Remove ingredient from user's ingredients array if count is 1 or less
          console.log('Ingredient Name: ' + ingredientName);
          console.log('Count: ' + ingredient.count);

          Models.Ingredient.updateOne(
            {userId},
            { $pull: { ingredients: { name : ingredientName } } }
          ).then(() => {
            console.log(ingredientName + " got removed from the array");
          }).catch((error) => {
            console.error(error);
          });
        }

      } else {
        console.log(`Ingredient ${ingredientName} not found`);
      }

      await userIngredient.save();
    }));
    res.send('Ingredients updated successfully');
  } catch (err) {
    console.error(err);
    res.status(500).send(err.message);
  }
});

module.exports = router;