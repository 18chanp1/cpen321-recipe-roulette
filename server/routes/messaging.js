import { initializeApp } from "firebase/app";
import { getMessaging } from "firebase/messaging";
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


// // Initialize Firebase Cloud Messaging and get a reference to the service
// const messaging = getMessaging(app);

// Send a message to the device corresponding to the provided
// registration token.
  
sendMessage = async () => {
    // This registration token comes from the client FCM SDKs.
    const registrationToken = 'YOUR_REGISTRATION_TOKEN';

    const message = {
    data: {
        score: '850',
        time: '2:45'
    },
    token: registrationToken
    };
    
    getMessaging().send(message).then((response) => {
        // Response is a message ID string.
        console.log('Successfully sent message:', response);
    })
    .catch((error) => {
        console.log('Error sending message:', error);
    });
}
router.get('/', async function(req, res, next) {
    await getRecipes();
    res.send('respond with a recipe');
});
  
module.exports = router;
  
