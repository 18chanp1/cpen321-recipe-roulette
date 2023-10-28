var express = require('express');
var router = express.Router();
var fetch = require('node-fetch');
var mongodb = require('../db');
var url = require('url');
const fs = require('fs');
const { default: mongoose } = require('mongoose');
const recipeSchema = new mongoose.Schema({
  user: String,
  ingredients: Array
})
const Recipe = mongoose.model('Recipe', recipeSchema);

getFlavourProfile = async (req) => {
  mongodb.once('open', function() {
    console.log("We're connected to MongoDB!");
  });
  let queriedUser = url.parse(req.url, true).query.user;
  const recipes = await Recipe.find({user: `${queriedUser}`});
  console.log(typeof recipes);
  console.log(recipes);
  for (int i)
  try {
    let res = await fetch(endpoint);
    recipes = await res.json();
    console.log(recipes);
    return recipes;
  } catch (error) {
    console.log(error);
    return null;
  }
}
router.get('/', async function(req, res, next) {
  let recipes = await getRecipes(req);
  res.send(recipes);
});

module.exports = router;
