const express = require('express');
var router = express.Router();
var Models = require('../../db/db').Models;
var dbFunctions = require("../../db/db").Functions;
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
    dbFunctions.dbSaveRecord(ingredientDoc);
    res.status(200).send('Successfully saved to database');
  } catch (err) {
    console.error(err);
    res.status(500).send('Error saving to database');
  }

});


router.get('/', async function(req, res, next) {
  //TODO: Please surround the minimal amount of code that generates exceptions with try catch blocks
  console.log("Return the list of ingredients in the user's inventory")

  try {
    // mongodb.once('open', function() {
    //   console.log("We're connected to MongoDB!");
    // });


    //TODO, I think this should work, but please test it. 
    let queriedUser = req.get('userId');
    const ingredients = dbFunctions.dbFindAllRecords(Models.Ingredient, {userId: `${queriedUser}`});
    console.log(typeof ingredients);
    console.log(ingredients);
    res.status(200);
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

  //this code is here to get around the stupid "unnecessary block" issue.
  // TODO: please, only surround code that generates exceptions with try/catch blocks. Move everything else outside. 
  let logOne = 1;
  let logTwo = 2;
  console.log(logOne + logTwo);


  try {
    const { userId, ingredients } = req.body;

    await Promise.all(ingredients.map(async (ingredientName) => {    
      // const userIngredient = await Models.Ingredient.findOne({ userId });
      const userIngredient = await dbFunctions.dbFindRecord(Models.Ingredient, { userId });
      
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
    res.status(200);
    res.send('Ingredients updated successfully');
  } catch (err) {
    console.error(err);
    res.status(500).send(err.message);
  }
});

module.exports = router;