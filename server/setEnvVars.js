var admin = require("firebase-admin");

process.env.APP_ENV = 'TEST'
process.env.FB_CRED = '../../secrets/firebase_admin_mock.json'
jest.spyOn(admin.credential, "cert").mockReturnValue(null);
jest.spyOn(admin, "initializeApp").mockReturnValue(null);
jest.spyOn(admin, "messaging").mockReturnValue({send: () => null});