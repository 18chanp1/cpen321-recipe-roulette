const mongoose = require("mongoose");
mongoose.connect("mongodb://cpen321-reciperoulette.westus.cloudapp.azure.com:27017/test", {useNewUrlParser: true, useUnifiedTopology: true});

const db = mongoose.connection;
db.on('error', console.error.bind(console, 'connection error:'));
db.once('open', function() {
  console.log("We're connected to MongoDB!");
});
module.exports = db;