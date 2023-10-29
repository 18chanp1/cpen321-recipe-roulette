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
let ingredientRequestRouter = require('./routes/ingredientRequests');

const fs = require('fs');
const http = require("http")
const https = require("https")

var app = express();


const privateKey = fs.readFileSync("/etc/letsencrypt/live/cpen321-reciperoulette.westus.cloudapp.azure.com/privkey.pem", "utf8")
const certificate = fs.readFileSync("/etc/letsencrypt/live/cpen321-reciperoulette.westus.cloudapp.azure.com/fullchain.pem", "utf8")
const ca = fs.readFileSync("/etc/letsencrypt/live/cpen321-reciperoulette.westus.cloudapp.azure.com/chain.pem", "utf8")

const credentials = {
	key: privateKey,
	cert: certificate, 
	ca: ca,
}

let httpsServer = https.createServer(credentials, app)
httpsServer.listen(8443, () => 
	{
		console.log("Https server running on 443")
	}
)

//setup websocket server
let wss = require("./wss/wss")(httpsServer); 
// let httpServer = http.createServer(app)
// httpServer.listen(8080, () => 
// 	{
// 		console.log("Http server running on 8080")
// 	}
// )

title = "test"

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
app.use('/flavorprofile', flavorProfileRouter);
app.use("/.well-known/assetlinks.json", assetLinkRouter)
app.use("/reviews", reviewRouter)
app.use("/ingredientrequests", ingredientRequestRouter)


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
