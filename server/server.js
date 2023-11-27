const fs = require('fs');
const http = require("http")
const https = require("https")
const app = require("./src/app");

if (process.env.APP_ENV != "TEST") {
	const privateKey = fs.readFileSync("/etc/letsencrypt/live/cpen321-reciperoulette.westus.cloudapp.azure.com/privkey.pem", "utf8")
	const certificate = fs.readFileSync("/etc/letsencrypt/live/cpen321-reciperoulette.westus.cloudapp.azure.com/fullchain.pem", "utf8")
	const ca = fs.readFileSync("/etc/letsencrypt/live/cpen321-reciperoulette.westus.cloudapp.azure.com/chain.pem", "utf8")
	
	const credentials = {
		key: privateKey,
		cert: certificate, 
		ca
	}
	
	let httpsServer = https.createServer(credentials, app)
	httpsServer.listen(8443, () => 
		{
			console.log("Https server running on 443")
		}
	)
	
	// setup websocket server
	app.wssServer = require("./wss/wss")(httpsServer); 
} else {
	let httpServer = http.createServer(app)
	httpServer.listen(8080, () => 
		{
			console.log("Http server running on 8080")
		}
	)
}

module.exports = app