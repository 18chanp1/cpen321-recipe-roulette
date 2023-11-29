var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var recipesRouter = require('./routes/recipeGenerator');
var ingredientRequestRouter = require('./routes/ingredientRequestManager');
var flavorProfileRouter = require('./routes/flavorProfileManager');
let reviewRouter = require('./routes/reviewsManager');
var foodInventoryRouter = require('./routes/foodInventoryManager');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use('/static', express.static('public'))

app.use('/recipes', recipesRouter);
app.use('/ingredientrequests', ingredientRequestRouter);
app.use('/flavourprofile', flavorProfileRouter);
app.use("/reviews", reviewRouter);
app.use("/foodInventoryManager", foodInventoryRouter);

module.exports = app;
