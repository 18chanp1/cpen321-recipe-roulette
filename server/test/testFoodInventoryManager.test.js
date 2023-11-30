var dbFunctions = require("../db/db").Functions;
var app = require("../src/app");
var request = require("supertest");
const mongoose = require("mongoose");

const baseMockedIngredientBody = {
    name: "pork",
    count: 1,
    date: Date.parse("Sun Oct 29 2023 10:50:56 GMT+0000") 
}

const baseMockedIngredientBody2 = {
    name: "beef",
    count: 2,
    date: Date.parse("Sun Oct 29 2023 10:50:56 GMT+0000")
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

/*
// GET Ingredients based on user
describe("Get food ingredients for a user", () => {
    // Input: User Id
    // Expected status code: 200
    // Expected behavior: User ingredients fetched and returned
    // Expected output: empty list
    test("No food ingredients returned", async () => {
        let expectedResponse = [];
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

        // Build the expected response
        let expectedResponse = Object.assign({}, baseMockedDbFindRecordResponse);
        
        let mockedDbFindAllRecordsResponse = [];
        for (let i = 0; i < 20; i++) {
            mockedDbFindAllRecordsResponse.push(baseMockedDbFindRecordResponse);
        }

        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbFindAllRecordsResponse);

        const res = await request(app).get("/foodInventoryManager").set('userId', 'test@ubc.ca');

        expect(res.status).toStrictEqual(200);
        for (let i = 0; i < 20; i++) {
            expect(res.body[i].requestId).toBeDefined();
            expect(res.body[i].userId).toEqual(expectedResponse.userId);
            expect(res.body[i].ingredientDescription).toEqual(expectedResponse.ingredientDescription);
            expect(res.body[i].fcmToken).toEqual(expectedResponse.fcmToken);
        }
    });
})
*/

// Interface POST /foodInventoryManager/upload
describe("Post new ingredient request", () => {
    // Input: Missing email
    // Expected status code: 400
    // Expected behavior: Empty user id and error returned
    // Expected output: "Error saving to database"
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

    // Input: valid new food ingredient upload. UserId record did not exist originally
    // Expected status code: 200
    // Expected behavior: non-empty user id and no error returned
    // Expected output: "Successfully saved to database"
    test("POST new ingredient request, not existing originally", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedDbFindRecordResponse);
        let expectedResponse = "Successfully saved to database"; 

        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        
        const res = await request(app).post("/foodInventoryManager/upload").send(mockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: valid new food ingredient upload. UserId record existed originally
    // Expected status code: 200
    // Expected behavior: non-empty user id and no error returned
    // Expected output: "Successfully saved to database"
    test("Post new ingredient request, existing originally", async () => {
        let mockedRequestBody = Object.assign({}, baseMockedDbFindRecordResponse);
        let expectedResponse = "Successfully saved to database"; 

        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(baseMockedDbFindRecordResponse);

        const res = await request(app).post("/foodInventoryManager/upload").send(mockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.test).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    })

    
})

/*
// Interface PUT /foodInventoryManager/update
describe("Update user's ingredient", () => {

    // Input: invalid userId
    // Expected status code: 404
    // Expected output: empty text and body
    test("PUT invalid userId", async () => {
        let mockedRequestBody = Object.assign({}, mockPutRequest);
        mockedRequestBody.userId = "";

        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        const res = await request(app).put("/foodInventoryManager/update").send(mockedRequestBody);
        expect(res.status).toStrictEqual(404);
        expect(res.text).toEqual("");
        expect(res.body).toEqual({});
    });

    // Input: valid user id and ingredients
    // Expected status code: 200
    // Expected output: empty text and body
    test("PUT valid entry", async () => {

        let ingredientPork = Object.assign({}, baseMockedIngredientBody);
        let ingredientBeef = Object.assign({}, baseMockedIngredientBody2);

        let mockedRequestBody = Object.assign({}, mockPutRequest);

        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(mockedRequestBody);
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValueOnce(ingredientPork).mockReturnValueOnce(ingredientBeef).mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbUpdateOne").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);

        const res = await request(app).put("/foodInventoryManager/update").send(mockedRequestBody);
        
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual("Successfully updated the database");
        expect(res.body).toEqual({});
    })
   
});
*/