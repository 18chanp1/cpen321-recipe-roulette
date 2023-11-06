const mongoose = require("mongoose");
mongoose.connect("mongodb://jaber:recipe@137.135.47.124:27017/Recipe_Roulette?authSource=admin", {useNewUrlParser: true, useUnifiedTopology: true});

const recipeSchema = new mongoose.Schema({
  userId: String,
  recipeName: String,
  recipeId: String,
  recipeSummary: String,
  likes: Number
})
const Recipe = mongoose.model('recipe', recipeSchema);

const ingredientRequestSchema = new mongoose.Schema({
  reqId: mongoose.ObjectId,
  userId: String,
  ingredientName: String,
  ingredientCount: Number,
  fcmToken: String
})
const IngredientRequest = mongoose.model('ingredientRequest', ingredientRequestSchema);

const foodItemSchema = new mongoose.Schema({
  name: {type: String, required: true},
  count: {type: Number, min: 0, required: true},
  date: [{type: Date, required: true}]
})

const foodItemsSchema = new mongoose.Schema({
  userId: {type: String, required: [true, 'Missing userId']},
  ingredients: [foodItemSchema]
});

// Create a model
const Ingredient = mongoose.model('foodItems', foodItemsSchema);

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