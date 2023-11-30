var dbFunctions = require("../db/db").Functions;
var app = require("../src/app");
var request = require("supertest");
const mongoose = require("mongoose");

// Test Objects
const baseMockedIngredientBody = {
    name: "pork",
    count: 1,
    date: [Date.parse("Sun Oct 29 2023 10:50:56 GMT+0000")]
}

const baseMockedIngredientBody2 = {
    name: "beef",
    count: 2,
    date: [Date.parse("Sun Oct 29 2023 10:50:56 GMT+0000"), Date.parse("Sun Nov 29 2023 10:50:56 GMT+0000")]
}

const ingredientsArrPUT = [
    baseMockedIngredientBody,
    baseMockedIngredientBody2
]

const ingredientsArrPUTMissing = [
    baseMockedIngredientBody
]

const mockedDbFindRecordResponse = {
    requestId: new mongoose.Types.ObjectId(), 
    userId: "test@ubc.ca", 
    ingredients: ingredientsArrPUT
}

const mockedDbFindRecordResponseMissing = {
    requestId: new mongoose.Types.ObjectId(), 
    userId: "test@ubc.ca", 
    ingredients: ingredientsArrPUTMissing
}

const mockPutRequest = {
    userId: "test@ubc.ca", 
    ingredients: ["pork", "beef", "chicken"]
}

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
    // Expected behavior: Ingredients fetched and returned
    // Expected output: List of all ingredient requests
    test("All food ingredients returned", async () => {

        // Build the expected response
        let expectedResponse = Object.assign({}, mockedDbFindRecordResponse);
        let mockedDbFindAllRecordsResponse = [];
        mockedDbFindAllRecordsResponse.push(expectedResponse);

        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(mockedDbFindRecordResponse);

        const res = await request(app).get("/foodInventoryManager").set('userId', 'test@ubc.ca');

        expect(res.status).toStrictEqual(200);
        expect(res.body.length).toStrictEqual(2);

        for (let i = 0; i < res.body.length; i++) {
            expect(res.body[i].name).toEqual(expectedResponse.ingredients[i].name);
            expect(res.body[i].count).toEqual(expectedResponse.ingredients[i].count);

            for (let j = 0; j < res.body[i].date.length; j++) {
                expect(res.body[i].date[j]).toEqual(expectedResponse.ingredients[i].date[j]);
            }
        }
    });

    // Input: User Id
    // Expected status code: 200
    // Expected behavior: Ingredients fetched and returned
    // Expected output: Empty list of ingredients
    test("User Ingredient is null", async () => {
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        const res = await request(app).get("/foodInventoryManager").set('userId', 'test@ubc.ca');
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual([]);
    })
})

// Interface POST /foodInventoryManager/upload
describe("Post new ingredient request", () => {
    // Input: Missing email
    // Expected status code: 400
    // Expected behavior: Empty user id and error returned
    // Expected output: "Error saving to database"
    test("POST invalid email", async () => {
        let mockedRequestBody = Object.assign({}, mockedDbFindRecordResponse);
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
        let mockedRequestBody = Object.assign({}, mockedDbFindRecordResponse);
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
        let mockedRequestBody = Object.assign({}, mockedDbFindRecordResponse);
        let expectedResponse = "Successfully saved to database"; 

        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(mockedDbFindRecordResponseMissing);

        const res = await request(app).post("/foodInventoryManager/upload").send(mockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    })

    
})


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

        let mockedRequestBody = Object.assign({}, mockPutRequest);

        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(mockedDbFindRecordResponse);
        jest.spyOn(dbFunctions, "dbUpdateOne").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);

        const res = await request(app).put("/foodInventoryManager/update").send(mockedRequestBody);
        
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual("Successfully updated the database");
        expect(res.body).toEqual({});
    });
   
})
