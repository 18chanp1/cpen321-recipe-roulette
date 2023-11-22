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

  console.log("/foodInventoryManager/upload userId: " + userId);
  if (userId == null || userId == "") {
    res.status(400).send('Error saving to database');
  } else {
    // Create a new document
    const ingredientDoc = new Models.Ingredient({ userId , ingredients });

    // Save the document to MongoDB
    dbFunctions.dbSaveRecord(ingredientDoc);
    res.status(200).send('Successfully saved to database');
  }

});


router.get('/', async function(req, res, next) {
  //TODO: Please surround the minimal amount of code that generates exceptions with try catch blocks
  console.log("Return the list of ingredients in the user's inventory")

  let queriedUser = req.get('userId');
  console.log("Queried User: " + queriedUser);
  if (queriedUser == null || queriedUser == "") {
    res.status(400).send([]);
  } else {
    const ingredients = dbFunctions.dbFindAllRecords(Models.Ingredient, {userId: `${queriedUser}`});
    console.log(typeof ingredients);
    console.log(ingredients);
    res.status(200);
    res.send(ingredients);
  }
});

// PUT: given a user and a list of ingredients, if the ingredient exists, decrement the counter or delete the food item
router.put('/update', async (req, res) => {

  //this code is here to get around the stupid "unnecessary block" issue.
  // TODO: please, only surround code that generates exceptions with try/catch blocks. Move everything else outside. 
  let logOne = 1;
  let logTwo = 2;
  console.log(logOne + logTwo);

  // try {
    const { userId, ingredients } = req.body;

    await Promise.all(ingredients.map(async (ingredientName) => {    
      // const userIngredient = await Models.Ingredient.findOne({ userId });
      const userIngredient = await dbFunctions.dbFindRecord(Models.Ingredient, { userId });
      
      if (!userIngredient || userIngredient == null) {
        console.log(`User with userId ${userId} not found`);
        // return res.status(404).send('User not found');
        return res.status(404).send();
        // return res.status(404);
      } else {
        console.log("Get userId: " + userIngredient.userId);
      }

      const ingredient = dbFunctions.dbFindAllRecords(userIngredient.ingredients, ing => ing.name === ingredientName)
      // const ingredient = userIngredient.ingredients.find(ing => ing.name === ingredientName);
      
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
          
          dbFunctions.dbUpdateOne(Models.Ingredient, {userId}, { $pull: { ingredients: { name : ingredientName } } })
          // Models.Ingredient.updateOne(
          //   {userId},
          //   { $pull: { ingredients: { name : ingredientName } } }
          // ).then(() => {
          //   console.log(ingredientName + " got removed from the array");
          // }).catch((error) => {
          //   console.error(error);
          // });
        }

      } else {
        console.log(`Ingredient ${ingredientName} not found`);
      }
      dbFunctions.dbSaveRecord(userIngredient);
    }));
    // res.status(200);
    // res.send('Ingredients updated successfully');
    res.status(200).send();
  // } catch (err) {
  //   console.error(err);
  //   res.status(500).send(err.message);
  // }
});

module.exports = router;