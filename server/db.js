const mongoose = require("mongoose");
mongoose.connect("mongodb://localhost:27017/test", {useNewUrlParser: true, useUnifiedTopology: true});

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.log("We're connected to MongoDB!");
});

const recipeSchema = new mongoose.Schema({
  user: String,
  recipeNames: [String]
})
const Recipe = mongoose.model('recipe', recipeSchema);

const ingredientRequestSchema = new mongoose.Schema({
  requestId: mongoose.ObjectId,
  user: String,
  ingredientName: String,
  ingredientCount: Number,
  fcmTok: String
})
const IngredientRequest = mongoose.model('ingredientRequest', ingredientRequestSchema);

module.exports = { Recipe, IngredientRequest };