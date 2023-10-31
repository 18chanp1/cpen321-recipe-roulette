const express = require('express');
var router = express.Router();
var mongodb = require('../db');

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
  userId: {type: String, required: [true, 'Missing userId']},
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
  res.send("GET inside food inventory manager");
});

module.exports = router;