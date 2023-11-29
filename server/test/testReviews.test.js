var dbFunctions = require("../db/db").Functions;
var app = require("../src/app");
var request = require("supertest");

const mockedDbGetAllRecipesResponse = [
    {
        recipeId: "567446",
        likes: 5,
        userId: "test@ubc.ca",
        recipeName: "Creamy Salmon Pasta",
        recipeImage: "https://spoonacular.com/recipeImages/73420-312x231.jpg",
        recipeSummary: "Lorem Ipsum for first recipe"
    },
    {
        recipeId: "564321",
        likes: 2,
        userId: "test2@ubc.ca",
        recipeName: "Sticky Baked Chicken",
        recipeImage: "https://spoonacular.com/recipeImages/73420-312x231.jpg",
        recipeSummary: "Lorem Ipsum for second recipe"
    }        
]


// Interface GET /reviews
describe("Get all recipes", () => {
    // Input: None
    // Expected status code: 200
    // Expected behavior: Recipes are fetched and sent to frontend
    // Expected output: Empty list of all recipes
    test("No recipes", async () => {
        let expectedResponse = [];
        jest.spyOn(dbFunctions, "dbGetAllReviews").mockReturnValue([]);
        const res = await request(app).get("/reviews");
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });
    
    // Input: None
    // Expected status code: 200
    // Expected behavior: Recipes are fetched and sent to frontend
    // Expected output: List of all recipes by all users
    test("Small number of recipes", async () => {
        let expectedResponse = [];
        mockedDbGetAllRecipesResponse.forEach(recipe => {
            let review = {
              id: recipe.recipeId,
              rating: recipe.likes,
              author: recipe.userId,
              title: recipe.recipeName,
              image: recipe.recipeImage,
              review: recipe.recipeSummary
            }
            expectedResponse.push(review);
          })
        jest.spyOn(dbFunctions, "dbGetAllReviews").mockReturnValue(mockedDbGetAllRecipesResponse);
        const res = await request(app).get("/reviews");
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });

    // Input: None
    // Expected status code: 200
    // Expected behavior: Recipes are fetched and sent to frontend
    // Expected output: List of all recipes by all users
    test("Max number of recipes", async () => {
        const maxRecipes = 30;
        let mockedDbGetAllRecipesResponseFull = [];
        let expectedResponse = [];
        let review = {
            id: mockedDbGetAllRecipesResponse[0].recipeId,
            rating: mockedDbGetAllRecipesResponse[0].likes,
            author: mockedDbGetAllRecipesResponse[0].userId,
            title: mockedDbGetAllRecipesResponse[0].recipeName,
            image: mockedDbGetAllRecipesResponse[0].recipeImage,
            review: mockedDbGetAllRecipesResponse[0].recipeSummary
        }
        for (let i = 0; i < maxRecipes; i++) {
            mockedDbGetAllRecipesResponseFull.push(mockedDbGetAllRecipesResponse[0]);
            expectedResponse.push(review);
        }
        jest.spyOn(dbFunctions, "dbGetAllReviews").mockReturnValue(mockedDbGetAllRecipesResponseFull);
        const res = await request(app).get("/reviews");
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });
})


const mockedDbFindRecordResponse = {
    recipeId: "564321",
    likes: 1,
    userId: "test2@ubc.ca",
    recipeName: "Chicken Curry",
    recipeImage: "https://spoonacular.com/recipeImages/73420-312x231.jpg",
    recipeSummary: "A hearty curry from chicken. Perfect for a winter night"
} 

// Interface POST /reviews/like
describe("Liking a recipe", () => {
    // Input: Missing author email
    // Expected status code: 400
    // Expected behavior: Missing email detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Missing email", async () => {
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/reviews/like").send({
            email: "",
            id: mockedDbFindRecordResponse.recipeId
        });
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Missing recipe id
    // Expected status code: 400
    // Expected behavior: Missing recipe id detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Missing recipe ID", async () => {
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/reviews/like").send({
            email: mockedDbFindRecordResponse.userId,
            id: ""
        });
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: combination of email and recipeId does not exist in db
    // Expected status code: 400
    // Expected behavior: No recipe is fetched and error is returned
    // Expected output: "Specified review does not exist"
    test("Non-Existent recipe", async () => {
        let expectedResponse = "Specified review does not exist";
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        const res = await request(app).post("/reviews/like").send({
            email: mockedDbFindRecordResponse.userId,
            id: "00000"
        });
        expect(res.status).toStrictEqual(400);
        expect(res.body).toEqual({});
        expect(res.text).toEqual(expectedResponse);
    });

    // Input: combination of email and recipeId exist in db
    // Expected status code: 200
    // Expected behavior: Recipe is fetched and like count incremented
    // Expected output: Liked recipe
    test("Existing recipe", async () => {
        let expectedResponse = mockedDbFindRecordResponse;
        expectedResponse.likes++;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(mockedDbFindRecordResponse);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        const res = await request(app).post("/reviews/like").send({
            email: mockedDbFindRecordResponse.userId,
            id: mockedDbFindRecordResponse.recipeId
        });
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });
})

// Interface POST /reviews/custom
describe("Posting a custom review/recipe", () => {
    // Input: Missing author email
    // Expected status code: 400
    // Expected behavior: Missing email detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Missing email", async () => {
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/reviews/custom").send({
            recipeName: mockedDbFindRecordResponse.recipeName,
            userId: "",
            recipeSummary: mockedDbFindRecordResponse.recipeSummary
        });
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Missing recipe name
    // Expected status code: 400
    // Expected behavior: Missing recipe name detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Missing recipe name", async () => {
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/reviews/custom").send({
            recipeName: "",
            userId: mockedDbFindRecordResponse.userId,
            recipeSummary: mockedDbFindRecordResponse.recipeSummary
        });
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Missing recipe summary
    // Expected status code: 400
    // Expected behavior: Missing recipe summary detected and error returned
    // Expected output: "Body parameters must not be empty"
    test("Missing recipe summary", async () => {
        let expectedResponse = "Body parameters must not be empty";
        const res = await request(app).post("/reviews/custom").send({
            recipeName: mockedDbFindRecordResponse.recipeName,
            userId: mockedDbFindRecordResponse.userId,
            recipeSummary: ""
        });
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Valid user email, recipe name and summary
    // Expected status code: 200
    // Expected behavior: New recipe is created, saved and returned
    // Expected output: The newly saved recipe/review
    test("Valid input", async () => {
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        const res = await request(app).post("/reviews/custom").send({
            recipeName: mockedDbFindRecordResponse.recipeName,
            userId: mockedDbFindRecordResponse.userId,
            recipeSummary: mockedDbFindRecordResponse.recipeSummary,
        });
        expect(res.status).toStrictEqual(200);
        expect(res.body.recipeId).toBeDefined();
        expect(res.body.userId).toEqual(mockedDbFindRecordResponse.userId);
        expect(res.body.recipeName).toEqual(mockedDbFindRecordResponse.recipeName);
        expect(res.body.recipeSummary).toEqual(mockedDbFindRecordResponse.recipeSummary);
        expect(res.body.recipeImage).toEqual("https://visitors-centre.jrc.ec.europa.eu/sites/default/files/thumbnail/kmffq_additional-illo4video_2019_5_fishplate%5B1%5D.jpg");
        expect(res.body.likes).toEqual(0);
        expect(res.body.numTimes).toEqual(1);
    });
})