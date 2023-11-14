var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var recipesRouter = require('./routes/recipes');
var ingredientRequestRouter = require('./routes/ingredientRequest');
var flavorProfileRouter = require('./routes/flavorProfile');
let assetLinkRouter = require('./routes/assetLink');
let reviewRouter = require('./routes/reviews');
var foodInventoryRouter = require('./routes/foodInventoryManager');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/recipes', recipesRouter);
app.use('/ingredientrequests', ingredientRequestRouter);
app.use('/flavourprofile', flavorProfileRouter);
app.use("/.well-known/assetlinks.json", assetLinkRouter);
app.use("/reviews", reviewRouter);
app.use("/foodInventoryManager", foodInventoryRouter);


// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
