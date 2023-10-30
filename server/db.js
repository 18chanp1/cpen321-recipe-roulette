const mongoose = require("mongoose");
mongoose.connect("mongodb://localhost:27017/Recipe_Roulette", {useNewUrlParser: true, useUnifiedTopology: true});

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.log("We're connected to MongoDB!");
});
module.exports = db;