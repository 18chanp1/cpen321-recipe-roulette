// const { getMessaging } = require('firebase-admin/messaging');
// const { initializeApp } = require('firebase-admin/app');
var admin = require("firebase-admin");
var serviceAccount = require("../../secrets/firebase_admin.json");
var dbModels = require("../../db/db").Models;
var dbFunctions = require("../../db/db").Functions;

const app = admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

var express = require('express');
var router = express.Router();

// Initialize Firebase Cloud Messaging and get a reference to the service
const messaging = admin.messaging(app);

router.post('/new', async function(req, res, next) {
    // Get info of ingredient request and create new document in collection
    let userId = req.body.email;
    let ingredientDescription = req.body.requestItem;
    let fcmToken = req.body.fcmtok;
    if (userId && ingredientDescription && fcmToken) {
        let requestId = dbFunctions.dbGetObjectId();
        let newIngredientRequest = new dbModels.IngredientRequest({
            requestId, 
            userId, 
            ingredientDescription,
            fcmToken
        });
        await dbFunctions.dbSaveRecord(newIngredientRequest);
        res.status(200);
        res.send(newIngredientRequest);
    } else {
        res.status(400);
        res.send("Body parameters must not be empty");
    }
});

router.get('/self', async function(req, res, next) {
    let userId = req.headers.email;
    if (userId) {
        let allSelfRequests = await dbFunctions.dbFindAllRecords(dbModels.IngredientRequest, {userId: `${userId}`});
        res.status(200);
        res.send(allSelfRequests);
    } else {
        res.status(400);
        res.send("User email must not be empty");
    }
});

router.post('/self/delete', async function(req, res, next) {
    let requestId = req.body.requestId;
    if (requestId) {
        let ingredientRequest = dbFunctions.dbFindRecord(
            dbModels.IngredientRequest, 
            {requestId: `${req.body.requestId}`}
            );
        if (ingredientRequest) {
            await dbFunctions.dbDeleteRecord(ingredientRequest);
            res.status(200);
            res.send(`Request ID ${requestId} successfully deleted`);
        } else {
            res.status(400);
            res.send(`Request ID ${requestId} does not exist`); 
        }
    } else {
        res.status(400);
        res.send("Missing Ingredient Request ID");
    }
});

router.get('/', async function(req, res, next) {
    let allRequests = await dbFunctions.dbFindAllRecords(dbModels.IngredientRequest, {});
    res.status(200);
    res.send(allRequests);
});

router.post('/', async function(req, res, next) {
    // This registration token comes from the client FCM SDKs.
    let requestId = req.body.requestId;
    if (!requestId) {
        res.status(400);
        res.send("Missing Ingredient Request ID");
        return;
    }
    let ingredientRequest = await dbFunctions.dbFindRecord(
        dbModels.IngredientRequest, 
        {requestId: `${requestId}`}
    );

    if (!ingredientRequest) {
        res.status(400);
        res.send(`Request ID ${requestId} does not exist`);
        return;
    }
    let fcmToken = ingredientRequest.fcmToken;
    await dbFunctions.dbDeleteRecord(ingredientRequest);
    const message = {
        data: {
            text: `Request ID ${requestId} fulfilled`
        },
        token: fcmToken   
    };

    messaging.send(message);
    // .then((response) => {
    //     // Response is a message ID string.
    //     console.log('Successfully sent message:', response);
    // })
    // .catch((error) => {
    //     console.log('Error sending message:', error);
    // });

    res.status(200);
    res.send(`Donated to request ID ${requestId}`);
});
  
module.exports = router;
  