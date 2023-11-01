const mongoose = require("mongoose");
mongoose.connect("mongodb://137.135.47.124:27017/Recipe_Roulette", {useNewUrlParser: true, useUnifiedTopology: true});

const recipeSchema = new mongoose.Schema({
  user: String,
  recipeNames: [String]
})
const Recipe = mongoose.model('recipe', recipeSchema);

const ingredientRequestSchema = new mongoose.Schema({
  reqID: mongoose.ObjectId,
  userId: String,
  ingredientName: String,
  ingredientCount: Number,
  fcmTok: String
})
const IngredientRequest = mongoose.model('ingredientRequest', ingredientRequestSchema);

const FoodItemSchema = new mongoose.Schema({
  name: {type: String, required: true},
  count: {type: Number, min: 0, required: true},
  date: [{type: Date, required: true}]
})

const FoodItemsSchema = new mongoose.Schema({
  userId: {type: String, required: [true, 'Missing userId']},
  ingredients: [FoodItemSchema]
});

// Create a model
const Ingredient = mongoose.model('food_items', FoodItemsSchema);

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.log("We're connected to MongoDB!");
});

const reviewSchema = new mongoose.Schema({
  reviewId: mongoose.ObjectId,
  userId: String,
  recipeName: String,
  reviewTitle: String,
  reviewText: String,
  likes: Number
})
const Review = mongoose.model('review', reviewSchema);

module.exports = { Recipe, IngredientRequest, Ingredient, Review };