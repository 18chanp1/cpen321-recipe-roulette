var Models = require("../utils/db");
var app = require("../app");
var request = require("supertest");

jest.mock("../utils/db");

// Interface GET www.myserver.ca/reviews
describe("Get all recipes", () => {
    // Input: None
    // Expected status code: 200
    // Expected behavior: Recipes are fetched from db
    // Expected output: List of all recipes by all users (max 30)
    test("Valid recipes", async () => {
        let mockedDbResponse = [
            {
                recipeId: "567446",
                likes: 5,
                userId: "test@ubc.ca",
                recipeName: "Creamy Salmon Pasta",
                image: "https://spoonacular.com/recipeImages/73420-312x231.jpg",
                recipeSummary: "Lorem Ipsum for first recipe"
            },
            {
                recipeId: "564321",
                likes: 2,
                userId: "test2@ubc.ca",
                recipeName: "Sticky Baked Chicken",
                image: "https://spoonacular.com/recipeImages/73420-312x231.jpg",
                recipeSummary: "Lorem Ipsum for second recipe"
            }        
        ]
        let execptedResponse = [];
        mockedDbResponse.forEach(recipe => {
            let review = {
              id: recipe.recipeId,
              rating: recipe.likes,
              author: recipe.userId,
              title: recipe.recipeName,
              image: recipe.image,
              review: recipe.recipeSummary
            }
            execptedResponse.push(review);
          })
        jest.spyOn(Models, "dbGetAllReviews").mockReturnValue(mockedDbResponse);
        const res = await request(app).get("/reviews");
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(execptedResponse);
    });
})
    