const mongoose = require("mongoose");

if (process.env.APP_ENV != "TEST") {
  mongoose.connect("mongodb://jaber:recipe@137.135.47.124:27017/Recipe_Roulette?authSource=admin", {useNewUrlParser: true, useUnifiedTopology: true});
  const db = mongoose.connection;
  db.on('error', console.error.bind(console, 'connection error:'));
  db.once('open', function() {
    console.log("We're connected to MongoDB!");
  });
}

const recipeSchema = new mongoose.Schema({
  userId: String,
  recipeName: String,
  recipeId: String,
  recipeSummary: String,
  recipeImage: String,
  numTimes: Number,
  likes: Number
})
const Recipe = mongoose.model('recipe', recipeSchema);

const ingredientRequestSchema = new mongoose.Schema({
  requestId: String,
  userId: String,
  phoneNo: String,
  ingredientName: String,
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

const dbGetAllReviews = async () => {
  let allRecipes = await Recipe.find().limit(30);
  return allRecipes;
}

const dbFindRecord = async (model, filter) => {
  let record = await model.findOne(filter);
  return record;
}

const dbFindAllRecords = async (model, filter) => {
  let records = await model.find(filter);
  return records;
}

const dbUpdateOne = async (model, filter, update) => {
  let record = await model.updateOne(filter, update);
  return record;
}

const dbSaveRecord = async (record) => {
  await record.save();
}

const dbDeleteRecord = async (record) => {
  await record.deleteOne();
}

module.exports = { 
  Models: {
    Recipe, IngredientRequest, Ingredient
  },
  Functions: {
    dbGetAllReviews, dbFindRecord, dbFindAllRecords, dbDeleteRecord, dbSaveRecord, dbUpdateOne
  }
};
