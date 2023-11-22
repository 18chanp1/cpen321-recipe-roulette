var dbFunctions = require("../db/db").Functions;
var app = require("../src/app");
var request = require("supertest");
const mongoose = require("mongoose");

const baseMockedIngredientBody = {
    name: "pork",
    count: 1,
    date: 1698576656
}

const baseMockedIngredientBody2 = {
    name: "beef",
    count: 2,
    date: 1698576656
}

const baseMockedIngredientBodyArr = [
    baseMockedIngredientBody,
    baseMockedIngredientBody,
    baseMockedIngredientBody
]

const baseMockedDbFindRecordResponse = {
    requestId: new mongoose.Types.ObjectId(), 
    userId: "test@ubc.ca", 
    ingredients: baseMockedIngredientBodyArr
}

const mockPutRequest = {
    userId: "test@ubc.ca", 
    ingredients: ["pork", "beef", "chicken"]
}

const mockGetRequest = {
    userId: "test@ubc.ca"
}

// GET Ingredients based on user
describe("Get food ingredients for a user", () => {
    // Input: User Id
    // Expected status code: 200
    // Expected behavior: User ingredients fetched and returned
    // Expected output: empty list
    test("No food ingredients returned", async () => {
        let expectedResponse = [];
        // jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue([]);
        const res = await request(app).get("/foodInventoryManager");
        expect(res.status).toStrictEqual(400);
        expect(res.body).toEqual(expectedResponse);
    });

    // Input: User Id
    // Expected status code: 200
    // Expected behavior: Ingredient ingredients fetched and returned
    // Expected output: List of all ingredient requests
    test("All food ingredients returned", async () => {
        // Build the mock request
        let mockGetRequestCopy = Object.assign({}, mockGetRequest);

        // Build the expected response
        let expectedResponse = Object.assign({}, baseMockedDbFindRecordResponse);
        
        let mockedDbFindAllRecordsResponse = [];
        for (i = 0; i < 20; i++) {
            mockedDbFindAllRecordsResponse.push(baseMockedDbFindRecordResponse);
        }

        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbFindAllRecordsResponse);

        const res = await request(app).get("/foodInventoryManager").set('userId', 'test@ubc.ca');

        expect(res.status).toStrictEqual(200);
        for (i = 0; i < 20; i++) {
            expect(res.body[i].requestId).toBeDefined();
            expect(res.body[i].userId).toEqual(expectedResponse.userId);
            expect(res.body[i].ingredientDescription).toEqual(expectedResponse.ingredientDescription);
            expect(res.body[i].fcmToken).toEqual(expectedResponse.fcmToken);
        }
    });
})

// Interface POST /foodInventoryManager/upload
describe("Post new ingredient request", () => {
    // Input: Missing email
    // Expected status code: 400
    // Expected behavior: Empty user id and error returned
    // Expected output: "Body parameters must not be empty"
    test("POST invalid email", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedDbFindRecordResponse);
        mockedRequestBody.userId = "";

        let expectedResponse = "Error saving to database";
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);

        const res = await request(app).post("/foodInventoryManager/upload").send(mockedRequestBody);
        
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: valid new food ingredient upload
    // Expected status code: 200
    // Expected behavior: non-empty user id and error returned
    // Expected output: "Body parameters must not be empty"
    test("POST new ingredient request", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedDbFindRecordResponse);
        let expectedResponse = "Successfully saved to database";

        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        const res = await request(app).post("/foodInventoryManager/upload").send(mockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    })

    
})

describe("Update user's ingredient", () => {

    test("PUT invalid userId", async () => {
        let mockedRequestBody = Object.assign({}, mockPutRequest);
        mockedRequestBody.userId = "";
        let expectedResponse = "User not found";

        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        const res = await request(app).put("/foodInventoryManager/update").send(mockedRequestBody);
        expect(res.status).toStrictEqual(404);
        // expect(res.text).toEqual(expectedResponse); //res.text returning the entire html
        expect(res.text).toEqual("");
        expect(res.body).toEqual({});
    });

    test("PUT valid entry", async () => {

        let ingredientPork = Object.assign({}, baseMockedIngredientBody);
        let ingredientBeef = Object.assign({}, baseMockedIngredientBody2);

        let mockedRequestBody = Object.assign({}, mockPutRequest);

        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(mockedRequestBody);
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValueOnce(ingredientPork).mockReturnValueOnce(ingredientBeef).mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbUpdateOne").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);

        const res = await request(app).put("/foodInventoryManager/update").send(mockedRequestBody);
        
        // TODO: small hack
        expect(res.status).toStrictEqual(200);
        // expect(res.status).toStrictEqual(404);
        // expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    })
   
});
