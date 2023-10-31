const express = require('express');
var router = express.Router();
var mongodb = require('../db');
var url = require('url');
var mongoose = require('mongoose');
const bodyParser = require('body-parser');

// Create a schema
const Schema = mongoose.Schema;

const FoodItemSchema = new Schema({
  name: {type: String, required: true},
  count: {type: Number, min: 0, required: true},
  date: [{type: Date, required: true}]
})

const FoodItemsSchema = new Schema({
  userId: {type: String, unique: true, index: true, required: [true, 'Missing userId'], unique: true},
  ingredients: [FoodItemSchema]
});

// Create a model
const Ingredient = mongoose.model('food_items', FoodItemsSchema);

// Use body-parser middleware to parse request bodies
router.use(bodyParser.json());

router.post('/upload', async (req, res) => {
  // Get the user ID token and ingredients from the request body
  const {userId, ingredients} = req.body;

  // Create a new document
  const ingredientDoc = new Ingredient({ userId , ingredients });

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

    let queriedUser = url.parse(req.url, true).query.userId;
    const ingredients = await Ingredient.find({userId: `${queriedUser}`});
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

// DELETE/UPDATE: given a user and a list of ingredients, if the ingredient exists, decrement the counter or delete the food item

/*
  {
    "userId": "yourUserId",
    "ingredients": ["ingredient1", "ingredient2", ...]
  }
*/
router.put('/update', async (req, res) => {
  try {
    const { userId, ingredients } = req.body;

    await Promise.all(ingredients.map(async (ingredientName) => {
      const userIngredient = await Ingredient.findOne({ userId });
      const ingredient = userIngredient.ingredients.find(ing => ing.name === ingredientName);
      
      if (ingredient) {
        if (ingredient.count > 1) {
          // Decrement count if it's more than 1
          ingredient.count -= 1;
        } else {
          // Remove ingredient from user's ingredients array if count is 1 or less
          userIngredient.ingredients.pull(ingredient);
        }
      } else {
        console.log(`Ingredient ${ingredientName} not found`);
      }

      // userIngredient.ingredients.markModified('ingredients');
      await userIngredient.save();
    }));
    res.send('Ingredients updated successfully');
  } catch (err) {
    console.error(err);
    res.status(500).send(err.message);
  }
});

module.exports = router;