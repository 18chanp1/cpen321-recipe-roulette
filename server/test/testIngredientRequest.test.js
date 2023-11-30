var dbFunctions = require("../db/db").Functions;
var app = require("../src/app");
var request = require("supertest");
const { randomUUID } = require('crypto');

const baseMockedRequestBody = { 
    email: "test@ubc.ca", 
    requestItem: "400g of skinless chicken thigh",
    phoneNo: "093-881-6698",
    fcmtok: "ckXGZWPGTU-2XnmGnt_QF_:APA91bHwSHBoFCph9tz27Da8qU5M8-8RbS-wANq6EN8c3ttQ8p-4TzvkMIuURKTYtYzp4IIgg_DTUMzyWy9KCXyyXWixen8dRj2sBaibdDIAB7fff8qpqWdCSsNsYPzpoFd5HHZwH8yw"
}

const baseMockedDbFindRecordResponse = {
    requestId: randomUUID(), 
    userId: baseMockedRequestBody.email, 
    phoneNo: baseMockedRequestBody.phoneNo,
    ingredientName: baseMockedRequestBody.requestItem,
    fcmToken: baseMockedRequestBody.fcmtok
}

// Interface GET /ingredientrequests
describe("Get all ingredient requests", () => {
    // Input: None
    // Expected status code: 200
    // Expected behavior: Ingredient requests fetched and returned
    // Expected output: Empty list of ingredient requests
    test("No requests returned", async () => {
        let expectedResponse = [];
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue([]);
        const res = await request(app).get("/ingredientrequests");
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });
    
    // Input: None 
    // Expected status code: 200
    // Expected behavior: Ingredient requests fetched and returned
    // Expected output: List of all ingredient requests
    test("All requests returned", async () => {
        let expectedResponse = baseMockedDbFindRecordResponse;
        let mockedDbFindAllRecordsResponse = [];
        for (let i = 0; i < 20; i++) {
            mockedDbFindAllRecordsResponse.push(baseMockedDbFindRecordResponse);
        }
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbFindAllRecordsResponse);
        const res = await request(app).get("/ingredientrequests");
        expect(res.status).toStrictEqual(200);
        for (let i = 0; i < 20; i++) {
            expect(res.body[i].requestId).toBeDefined();
            expect(res.body[i].userId).toEqual(expectedResponse.userId);
            expect(res.body[i].ingredientName).toEqual(expectedResponse.ingredientName);
            expect(res.body[i].phoneNo).toEqual(expectedResponse.phoneNo);
            expect(res.body[i].fcmToken).toEqual(expectedResponse.fcmToken);
        }
    });
})

// Interface GET /ingredientrequests/self
describe("Get all ingredient requests made by a specific user", () => {
    // Input: Missing user email
    // Expected status code: 400
    // Expected behavior: Missing email detected and error returned
    // Expected output: "User email must not be empty"
    test("Invalid user email", async () => {
        let expectedResponse = "User email must not be empty";
        const res = await request(app).get("/ingredientrequests/self").set({email: ""});
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: None 
    // Expected status code: 200
    // Expected behavior: All Ingredient requests for user fetched and returned
    // Expected output: List of all ingredient requests made by the user
    test("All requests returned", async () => {
        let expectedResponse = baseMockedDbFindRecordResponse;
        let mockedDbFindAllRecordsResponse = [];
        for (let i = 0; i < 20; i++) {
            mockedDbFindAllRecordsResponse.push(baseMockedDbFindRecordResponse);
        }
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbFindAllRecordsResponse);
        const res = await request(app).get("/ingredientrequests/self").set({email: expectedResponse.userId});
        expect(res.status).toStrictEqual(200);
        for (let i = 0; i < 20; i++) {
            expect(res.body[i].requestId).toBeDefined();
            expect(res.body[i].userId).toEqual(expectedResponse.userId);
            expect(res.body[i].ingredientName).toEqual(expectedResponse.ingredientName);
            expect(res.body[i].phoneNo).toEqual(expectedResponse.phoneNo);
            expect(res.body[i].fcmToken).toEqual(expectedResponse.fcmToken);
        }
    });
})

// Interface POST /ingredientrequests/new
describe("Post new ingredient request", () => {
    // Input: Missing email
    // Expected status code: 400
    // Expected behavior: Empty email detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Invalid email", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedRequestBody);
        mockedRequestBody.email = "";
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/ingredientrequests/new").send(mockedRequestBody);
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Missing ingredient description 
    // Expected status code: 400
    // Expected behavior: Empty ingredient description detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Invalid ingredient description", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedRequestBody);
        mockedRequestBody.requestItem = "";
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/ingredientrequests/new").send(mockedRequestBody);
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Missing fcm token
    // Expected status code: 400
    // Expected behavior: Empty fcm token detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Invalid fcm token", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedRequestBody);
        mockedRequestBody.fcmtok = "";
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/ingredientrequests/new").send(mockedRequestBody);
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });
    
    // Input: Valid request
    // Expected status code: 200
    // Expected behavior: New ingredient request registered
    // Expected output: New ingredient request
    test("Valid request body", async () => {
        let expectedResponse = baseMockedDbFindRecordResponse;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(baseMockedDbFindRecordResponse);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        const res = await request(app).post("/ingredientrequests/new").send(baseMockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.body.requestId).toBeDefined();
        expect(res.body.userId).toEqual(expectedResponse.userId);
        expect(res.body.ingredientName).toEqual(expectedResponse.ingredientName);
        expect(res.body.phoneNo).toEqual(expectedResponse.phoneNo);
        expect(res.body.fcmToken).toEqual(expectedResponse.fcmToken);
    });
})

const mockedDonateRequestBody = {
    email: baseMockedRequestBody.email,
    requestId: "507f191e810c19729de860ea"
}

// Interface POST /ingredientrequests
describe("Donate to an ingredient request", () => {
    // Input: Missing request ID
    // Expected status code: 400
    // Expected behavior: Empty request ID detected and error returned
    // Expected output: "Missing Ingredient Request ID"
    test("Missing request ID", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedRequestBody);
        mockedRequestBody.requestId = "";
        let expectedResponse = "Missing Ingredient Request ID";
        const res = await request(app).post("/ingredientrequests").send(mockedRequestBody);
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Missing donator ID
    // Expected status code: 400
    // Expected behavior: Empty donator ID detected and error returned
    // Expected output: "Missing donator ID"
    test("Missing request ID", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedRequestBody);
        mockedRequestBody.email = "";
        let expectedResponse = "Missing donator ID";
        const res = await request(app).post("/ingredientrequests").send(mockedRequestBody);
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Non-existent request ID
    // Expected status code: 400
    // Expected behavior: Non-existent ingredient request ID detected and error returned
    // Expected output: "Request ID ${requestId} does not exist"
    test("Invalid requestID", async () => {
        let expectedResponse = `Request ID ${mockedDonateRequestBody.requestId} does not exist`;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        const res = await request(app).post("/ingredientrequests").send(mockedDonateRequestBody);
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Valid request ID
    // Expected status code: 200
    // Expected behavior: Request identified and deleted from database
    // Expected output: "Donated to request ID ${requestId}"
    test("Valid requestID", async () => {
        let requestId = baseMockedDbFindRecordResponse.requestId;
        let mockedRequestBody = Object.assign({}, baseMockedRequestBody);
        mockedRequestBody.requestId = requestId;
        let expectedResponse = `Donated to request ID ${requestId}`;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(baseMockedDbFindRecordResponse);
        jest.spyOn(dbFunctions, "dbDeleteRecord").mockReturnValue(null);
        const res = await request(app).post("/ingredientrequests").send(mockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });
})

// Interface POST /ingredientrequests/delete
describe("Delete an ingredient request", () => {
    // Input: Missing request ID
    // Expected status code: 400
    // Expected behavior: Empty request ID detected and error returned
    // Expected output: "Missing Ingredient Request ID"
    test("Missing request ID", async () => {
        let expectedResponse = "Missing Ingredient Request ID";
        const res = await request(app).post("/ingredientrequests/self/delete").send({requestId: ""});
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Non-existent request ID
    // Expected status code: 400
    // Expected behavior: Non-existent ingredient request ID detected and error returned
    // Expected output: "Request ID ${requestId} does not exist"
    test("Invalid requestID", async () => {
        let requestId = "507f191e810c19729de860ea";
        let expectedResponse = `Request ID ${requestId} does not exist`;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        const res = await request(app).post("/ingredientrequests/self/delete").send({requestId});
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Valid request ID
    // Expected status code: 200
    // Expected behavior: Request identified and deleted from database
    // Expected output: "Request ID ${requestId} successfully deleted"
    test("Valid requestID", async () => {
        let requestId = baseMockedDbFindRecordResponse.requestId;
        let expectedResponse = `Request ID ${requestId} successfully deleted`;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(baseMockedDbFindRecordResponse);
        jest.spyOn(dbFunctions, "dbDeleteRecord").mockReturnValue(null);
        const res = await request(app).post("/ingredientrequests/self/delete").send({requestId});
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });
})
    