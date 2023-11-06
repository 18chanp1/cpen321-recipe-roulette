// const { getMessaging } = require('firebase-admin/messaging');
// const { initializeApp } = require('firebase-admin/app');
var admin = require("firebase-admin");
var serviceAccount = require("../firebase_admin.json");
var Models = require("../utils/db");
const { default: mongoose } = require('mongoose');

const app = admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

var express = require('express');
var router = express.Router();

// // Your web app's Firebase configuration
// // For Firebase JS SDK v7.20.0 and later, measurementId is optional
// const firebaseConfig = {
//     apiKey: "AIzaSyAF7p-BqV4LQIyDhHhFUMVFtpIBYRqKMi4",
//     authDomain: "cpen321-recipe-roulette-401802.firebaseapp.com",
//     projectId: "cpen321-recipe-roulette-401802",
//     storageBucket: "cpen321-recipe-roulette-401802.appspot.com",
//     messagingSenderId: "217153905955",
//     appId: "1:217153905955:web:def5ae4f0059d5763d9896",
//     measurementId: "G-MLT9C3GYJ1"
//   };

// // Initialize Firebase
// const app = initializeApp(firebaseConfig);


// Initialize Firebase Cloud Messaging and get a reference to the service
const messaging = admin.messaging(app);

// Send a message to the device corresponding to the provided
// registration token.

let deleteIngredientRequest = async (req) => {
    console.log(req.body.reqID);
    let ingredientRequest = Models.IngredientRequest.findOne({reqID: `${req.body.reqID}`});
    console.log(ingredientRequest);
    if (ingredientRequest != null) {
        await ingredientRequest.deleteOne();
    }
}

let requestIngredient = async (req) => {
    // Get all info of ingredient and create new document in collection
    console.log(req.body);
    let user = req.body.email;
    let ingredientName = req.body.requestItem;
    let ingredientCount = 1;
    let fcmTok = req.body.fcmtok;
    let id = new mongoose.Types.ObjectId();
    let newRequestIngredient = new Models.IngredientRequest({
        reqID: id, 
        userId: user, 
        ingredientName, 
        ingredientCount,
        fcmTok
    });
    await newRequestIngredient.save();
}

let donateIngredient = async (req) => {
    // This registration token comes from the client FCM SDKs.
    console.log(req.body.reqID);
    let ingredientRequest = await Models.IngredientRequest.findOne({reqID: `${req.body.reqID}`});
    if (ingredientRequest == null) {
        return null;
    }
    let fcmToken = ingredientRequest.fcmTok;
    console.log(ingredientRequest);
    console.log(fcmToken);
    await ingredientRequest.deleteOne();
    const message = {
        data: {
            text: `Request ID ${req.body.reqID} fulfilled`
        },
        token: fcmToken    
    };

    messaging.send(message).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
    })
    .catch((error) => {
        console.log('Error sending message:', error);
    });
}

let getAllRequests = async () => {
    let allRequests = await Models.IngredientRequest.find();
    console.log(allRequests);
    return allRequests;
}

let getAllSelfRequests = async (req) => {
    console.log(req.headers);
    let userId = req.headers.email;
    let allRequests = await Models.IngredientRequest.find({userId: `${userId}`});
    console.log(allRequests);
    return allRequests;
}

router.post('/new', async function(req, res, next) {
    await requestIngredient(req);
    res.status(200);
    res.send('Request submitted');
});

router.get('/donate', async function(req, res, next) {
    donateIngredient(req);
    res.status(200);
    res.send('Donate success');
});

router.get('/self', async function(req, res, next) {
    let allRequests = await getAllSelfRequests(req);
    res.status(200);
    res.send(allRequests);
});

router.post('/self/delete', async function(req, res, next) {
    await deleteIngredientRequest(req);
    res.status(200);
    res.send("Deleted");
});

router.get('/', async function(req, res, next) {
    let allRequests = await getAllRequests();
    res.status(200);
    res.send(allRequests);
});

router.post('/', async function(req, res, next) {
    let allRequests = await donateIngredient(req);
    res.status(200);
    res.send(allRequests);
});
  
module.exports = router;
  
