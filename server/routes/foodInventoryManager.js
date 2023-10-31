const express = require('express');
var router = express.Router();
var Models = require('../db');
var url = require('url');
var mongoose = require('mongoose');
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
    mongodb.once('open', function() {
      console.log("We're connected to MongoDB!");
    });

    let queriedUser = url.parse(req.url, true).query.userId;
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

module.exports = router;