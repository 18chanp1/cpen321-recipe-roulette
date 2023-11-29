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
  console.log(req.body);
  console.log("/foodInventoryManager/upload userId: " + userId);
  if (userId == null || userId == "") {
    res.status(400).send('Error saving to database');
  } else {
    const ingredientRecord = await dbFunctions.dbFindRecord(Models.Ingredient, {userId});
    console.log("Print the ingredient record contents")
    console.log(ingredientRecord);

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
        // console.log(ingredientRecord);
        // console.log(ingredientRecord.ingredients);
        index = ingredientRecord.ingredients.findIndex(item => item.name === newItem.name);
        console.log("index: " + index);

        if (index !== -1) {
          //Item exists, update the value
          console.log("Item exists, update the value");
          console.log(newItem.date[0]);
          ingredientRecord.ingredients[index].count += newItem.count;
          ingredientRecord.ingredients[index].date.push(...newItem.date);
          // for (let i = 0; i < newItem.date.length; i++) {
          //   ingredientRecord.ingredients[index].date.push(parseInt((newItem.date[i]) * 1000).toString());
          // }

        } else {
          //Item does not exist
          console.log("Item does not exist")
          ingredientRecord.ingredients.push(newItem);
          // ingredientRecord.ingredients.push({
          //   "name" : newItem.name,
          //   "count": newItem.count,
          //   "date": newItem.date
          // });
        }
      }
      await dbFunctions.dbSaveRecord(ingredientRecord);
      res.status(200).send('Successfully saved to database');
    }
  }

});


router.get('/', async function(req, res, next) {
  // console.log("Return the list of ingredients in the user's inventory")

  let queriedUser = req.headers.userid;
  console.log(req.headers);
  console.log("Queried User: " + queriedUser);
  if (queriedUser == null || queriedUser == "") {
    res.status(400).send([]);
  } else {
    const ingredients = await dbFunctions.dbFindAllRecords(Models.Ingredient, {userId: `${queriedUser}`});
    console.log(typeof ingredients);
    console.log(ingredients);
    res.status(200);
    res.send(ingredients);
  }
});

// PUT: given a user and a list of ingredients, if the ingredient exists, decrement the counter or delete the food item
router.put('/update', async (req, res) => {

  //this code is here to get around the stupid "unnecessary block" issue.
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
      console.log("Ingredient " + ingredientName + " Ingredient Index: " + ingredientIndex);
      if (ingredientIndex !== -1) {
        let ingredient = ingredients[ingredientIndex];
        if (ingredient.count >= 1) {
          // Decrement count if it's more than 1
          console.log('Ingredient Name: ' + ingredientName);
          console.log('Count: ' + ingredient.count);
          let newCount = ingredient.count - 1;

          let filter = {userId};
          let updateCount = {$set: {
            [`ingredients.${ingredientIndex}.count`]: newCount
          }}
          await dbFunctions.dbUpdateOne(Models.Ingredient, filter, updateCount);

          console.log('Count after: ' + newCount);
          console.log('Remove the earliest date');
          
          
          let smallestElement = Infinity;
          for (let i = 0; i < ingredient.date.length; i++) {
            if (ingredient.date[i] < smallestElement) {
              smallestElement = ingredient.date[i];
            }
          }

          console.log("Earliest Date: " + smallestElement);
          let update = {$pull: {
            [`ingredients.${ingredientIndex}.date`]: smallestElement
          }}

          await dbFunctions.dbUpdateOne(Models.Ingredient, filter, update);
          
        }
        
      } else {
        console.log(`Ingredient ${ingredientName} not found`);
      }
      
      //await dbFunctions.dbSaveRecord(userIngredient);
    }

    /*
    inputIngredients.map(async (ingredientName) => {    
      
      const ingredientIndex = ingredients.findIndex((i) => i.name == ingredientName);
      console.log("Ingredient " + ingredientName + " Ingredient Index: " + ingredientIndex);
      if (ingredientIndex !== -1) {
        let ingredient = ingredients[ingredientIndex];
        if (ingredient.count > 1) {
          // Decrement count if it's more than 1
          console.log('Ingredient Name: ' + ingredientName);
          console.log('Count: ' + ingredient.count);
          let newCount = ingredient.count - 1;

          let filter = {userId};
          let updateCount = {$set: {
            [`ingredients.${ingredientIndex}.count`]: newCount
          }}
          await dbFunctions.dbUpdateOne(Models.Ingredient, filter, updateCount);

          console.log('Count after: ' + newCount);
          console.log('Remove the earliest date');
          
          
          let smallestElement = Infinity;
          for (let i = 0; i < ingredient.date.length; i++) {
            if (ingredient.date[i] < smallestElement) {
              smallestElement = ingredient.date[i];
            }
          }

          console.log("Earliest Date: " + smallestElement);
          let update = {$pull: {
            [`ingredients.${ingredientIndex}.date`]: smallestElement
          }}

          await dbFunctions.dbUpdateOne(Models.Ingredient, filter, update);
          
        } else {
          // Remove ingredient from user's ingredients array if count is 1 or less
          console.log('Remove ingredient');
          console.log('Ingredient Name: ' + ingredientName);
          console.log('Count: ' + ingredient.count);
          
          await dbFunctions.dbUpdateOne(Models.Ingredient, {userId}, { $pull: { ingredients: { name : ingredientName } } })
        }
        
      } else {
        console.log(`Ingredient ${ingredientName} not found`);
      }
      
      //await dbFunctions.dbSaveRecord(userIngredient);
      
    });
    */
    res.status(200).send("Successfully updated the database");
    return;

});

module.exports = router;