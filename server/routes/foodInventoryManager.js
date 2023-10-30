const express = require('express');
var router = express.Router();
var mongodb = require('../db');

var mongoose = require('mongoose');
var url = require('url');
// const { google } = require('googleapis');
const bodyParser = require('body-parser');

// Connect to MongoDB
// mongoose.connect('"mongodb://localhost:27017/Recipe_Roulette"', {useNewUrlParser: true, useUnifiedTopology: true});

// Create a schema
const Schema = mongoose.Schema;

const FoodItemSchema = new Schema({
  name: { "type": "string", "required": "true" },
  count: { "type": "number", "required": "true" },
  date:  [Date]
})

const FoodItemsSchema = new Schema({
  userId: { "type": "String"},
  ingredients: [FoodItemSchema]
});

// Create a model
const Ingredient = mongoose.model('food_items', FoodItemsSchema);


// // Create an express application
// const app = express();

// Use body-parser middleware to parse request bodies
router.use(bodyParser.json());

router.post('/upload', async (req, res) => {
  // Get the user ID token and ingredients from the request body
  const {userId, ingredients} = req.body;
  // const {ingredients} = req.body;

  // Set up OAuth2 client
  // const oauth2Client = new google.auth.OAuth2();
  // oauth2Client.setCredentials({ id_token: userIdToken });

  // // Get the user's email
  // const ticket = await oauth2Client.verifyIdToken({
  //   idToken: userIdToken,
  //   audience: YOUR_CLIENT_ID // Replace with your client ID
  // });
  // const email = ticket.getPayload().email;
  // const email = userIdToken;

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

// app.listen(3000, () => console.log('Server started on port 3000'));
module.exports = router;