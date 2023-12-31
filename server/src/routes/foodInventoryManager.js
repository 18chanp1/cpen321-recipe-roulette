const express = require('express');
var router = express.Router();
var Models = require('../../db/db').Models;
var dbFunctions = require("../../db/db").Functions;
const bodyParser = require('body-parser');


// Use body-parser middleware to parse request bodies
router.use(bodyParser.json());

// POST: posts the user's ingredients to the database
router.post('/upload', async (req, res) => {
  // Get the user ID token and ingredients from the request body
  const {userId, ingredients} = req.body;
  if (userId == null || userId == "") {
    res.status(400).send('Error saving to database');
  } else {
    const ingredientRecord = await dbFunctions.dbFindRecord(Models.Ingredient, {userId});

    if (ingredientRecord == null) {
      console.log("userID record does not exist");
      // Create a new document
      const ingredientDoc = new Models.Ingredient({ userId , ingredients });

      // Save the document to MongoDB
      await dbFunctions.dbSaveRecord(ingredientDoc);
      res.status(200).send('Successfully saved to database');
    } else {
      // the userID record exists
      console.log("userID record exists");
      for (let newItem of ingredients) {
        //find the index
        let index = ingredientRecord.ingredients.findIndex(item => item.name === newItem.name);

        if (index !== -1) {
          //Item exists, update the value
          console.log("Item exists, update the value");
          ingredientRecord.ingredients[index].count += newItem.count;
        
          for (let i = 0; i < newItem.date.length; i++) {
            ingredientRecord.ingredients[index].date.push(newItem.date[i]);
          }

        } else {
          //Item does not exist
          console.log("Item does not exist")
          ingredientRecord.ingredients.push(newItem);
        }
      }
      await dbFunctions.dbSaveRecord(ingredientRecord);
      res.status(200).send('Successfully saved to database');
    }
  }

});

// GET: gets the list of ingredients for a user
router.get('/', async function(req, res, next) {

  let queriedUser = req.headers.userid;
  if (queriedUser == null || queriedUser == "") {
    res.status(400).send([]);
  } else {
    const userIngredients = await dbFunctions.dbFindRecord(Models.Ingredient, {userId: `${queriedUser}`});
    
    if (!userIngredients) {
      res.status(200).send([]);
      return;
    }
    
    let ingredients = userIngredients.ingredients;
    let response = [];

    for (let ingredient of ingredients) {
      if (ingredient.count !== 0) {
        response.push(ingredient);
      }
    }

    res.status(200);
    res.send(response);
  }
});

// PUT: given a user and a list of ingredients, if the ingredient exists, decrement the counter or delete the food item
router.put('/update', async (req, res) => {

  //this code is here to get around the "unnecessary block" issue.
  let logOne = 1;
  let logTwo = 2;
  console.log(logOne + logTwo);

    const userId = req.body.userId;
    let inputIngredients = req.body.ingredients;
    const userIngredient = await dbFunctions.dbFindRecord(Models.Ingredient, { userId });
    if (userIngredient == null) {
      console.log(`User with userId ${userId} not found`);
      res.status(404).send();
      return;
    } else {
      console.log("Get userId: " + userIngredient.userId);
    }
    let ingredients = userIngredient.ingredients;

    for (let ingredientName of inputIngredients) {
      const ingredientIndex = ingredients.findIndex((i) => i.name == ingredientName);
      if (ingredientIndex !== -1) {
        let ingredient = ingredients[ingredientIndex];
        if (ingredient.count >= 1) {
          // Decrement count
          let newCount = ingredient.count - 1;

          let filter = {userId};
          let updateCount = {$set: {
            [`ingredients.${ingredientIndex}.count`]: newCount
          }}
          await dbFunctions.dbUpdateOne(Models.Ingredient, filter, updateCount);
          
          let smallestElement = Infinity;
          for (let i = 0; i < ingredient.date.length; i++) {
            if (ingredient.date[i] < smallestElement) {
              smallestElement = ingredient.date[i];
            }
          }

          let update = {$pull: {
            [`ingredients.${ingredientIndex}.date`]: smallestElement
          }}

          await dbFunctions.dbUpdateOne(Models.Ingredient, filter, update); 
        }
        
      } else {
        console.log(`Ingredient ${ingredientName} not found`);
      }
      
    }

    res.status(200).send("Successfully updated the database");
    return;

});

module.exports = router;