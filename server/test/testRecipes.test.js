jest.mock('node-fetch');

var fetch = require('node-fetch');
var dbFunctions = require("../db/db").Functions;
var app = require("../src/app");
var request = require("supertest");

const baseMockedRequestBody = { 
    userId: "test@ubc.ca",
    recipeId: "795512",
    image: "https://someimgurl.jpg"
}

let baseMockedDbFindRecordGetResponse = {
    ingredients: [
        {
            name: "Salmon",
            date: [new Date(2023, 12, 9), new Date(2023, 12, 11)]
        },
        {
            name: "Chicken",
            date: [new Date(2023, 12, 20), new Date(2023, 12, 25)]
        },
        {
            name: "Soy Sauce",
            date: [new Date(2024, 2, 18)]
        },
        {
            name: "Heavy Cream",
            date: [new Date(2024, 1, 15)]
        },
        {
            name: "Onion",
            date: [new Date(2023, 12, 15), new Date(2023, 12, 20)]
        },
        {
            name: "BBQ Sauce",
            date: [new Date(2024, 5, 20)]
        }
    ]
}

const mockedRecipes = [
    {
        recipeName: "Creamy Salmon Pasta",
        userId: "test@ubc.ca",
        recipeId: "577324",
        recipeSummary: "Tis a pasta recipe",
        recipeImage: "https://visitors-centre.jrc.ec.europa.eu/sites/default/files/thumbnail/kmffq_additional-illo4video_2019_5_fishplate%5B1%5D.jpg",
        numTimes: 2,
        likes: 5
    },
    {
        recipeName: "Sticky Baked Chicken",
        userId: "test2@gmail.com",
        recipeId: "623324",
        recipeSummary: "Tis a chicken recipe",
        recipeImage: "https://visitors-centre.jrc.ec.europa.eu/sites/default/files/thumbnail/kmffq_additional-illo4video_2019_5_fishplate%5B1%5D.jpg",
        numTimes: 3,
        likes: 2
    },
]

const mockedInstructions = "Placeholder instructions"

// Interface GET /recipes
describe("Get all available recipes", () => {
    // Input: Missing user email
    // Expected status code: 400
    // Expected behavior: Missing user email detected and error returned
    // Expected output: "Missing user email"
    test("Missing user email", async () => {
        let expectedResponse = "Missing user email";
        const res = await request(app).get("/recipes").set({email: ""});
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: User with no ingredients saved
    // Expected status code: 200
    // Expected behavior: No recipes fetched
    // Expected output: Empty list of recipes
    test("No ingredients saved", async () => {
        let expectedResponse = [];
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        const res = await request(app).get("/recipes").set({email: "test@ubc.ca"});
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });

    // Input: External API returns error
    // Expected status code: 500
    // Expected behavior: External API error detected and error returned
    // Expected output: "Cannot fetch recipes at this point"
    test("External API returns error", async () => {
        let expectedResponse = "Cannot fetch recipes at this point";
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(baseMockedDbFindRecordGetResponse);
        fetch.mockReturnValue(Promise.resolve({ status: 401, json: () => Promise.resolve()}));
        const res = await request(app).get("/recipes").set({email: "test@ubc.ca"});
        expect(res.status).toStrictEqual(500);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Valid user with ingredients saved
    // Expected status code: 200
    // Expected behavior: Recipes fetched and returned
    // Expected output: Empty list of recipes
    test("Valid email and Ingredients saved", async () => {
        let expectedResponse = mockedRecipes;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(baseMockedDbFindRecordGetResponse);
        fetch.mockReturnValue(Promise.resolve({ status: 201, json: () => Promise.resolve(mockedRecipes)}));
        const res = await request(app).get("/recipes").set({email: "test@ubc.ca"});
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });
})

const baseMockedDbFindRecordPostResponse = {
    userId: baseMockedRequestBody.userId,
    recipeId: baseMockedRequestBody.recipeId,
    recipeName: 'Whole 30 Slow Cooker',
    recipeImage: baseMockedRequestBody.image,
    numTimes: 1,
    likes: 0,
    recipeSummary: `Whole 30 Slow Cooker is a <b>gluten free, dairy free, and whole 30</b> hor d'oeuvre. For <b>$17.22 per serving</b>, this recipe <b>covers 67%</b> of your daily requirements of vitamins and minerals. This recipe makes 65 servings with <b>3760 calories</b>, <b>365g of protein</b>, and <b>237g of fat</b> each. If you have conquer your fear of cooking a chicken the easy way! from one lovely life, pulled pork taco salad from anya's eats, easy slow cooker taco meat from rubies and radishes, and a few other ingredients on hand, you can make it. 24 people have tried and liked this recipe. From preparation to the plate, 
  this recipe takes about <b>45 minutes</b>. It is brought to you by Pink When. All things considered, we decided this recipe <b>deserves a spoonacular score of 3%</b>. This score is very bad (but still fixable). If you like this recipe, you might also like recipes such as <a href="https://spoonacular.com/recipes/slow-cooker-easy-slow-cooker-pot-roasted-steak-174176">Slow-Cooker Easy Slow-Cooker Pot-Roasted Steak</a>, <a href="https://spoonacular.com/recipes/red-eye-bbq-ribs-slow-cooker-75-days-of-summer-slow-cooker-s-504481">Red Eye BBQ Ribs Slow Cooker – 75 Days of Summer Slow Cooker s</a>, and <a href="https://spoonacular.com/recipes/slow-cooker-beef-gyros-75-days-of-summer-slow-cooker-s-504655">Slow Cooker Beef Gyros – 75 Days of Summer Slow Cooker s</a>.`
}

// Interface POST /recipes
describe("Save chosen recipe and return its details", () => {
    // Input: Missing userId and recipeId
    // Expected status code: 400
    // Expected behavior: Missing parameters detected and error returned
    // Expected output: "Request body parameters must not be empty"
    test("Missing body parameters", async () => {
        let expectedResponse = "Request body parameters must not be empty";
        const res = await request(app).post("/recipes").send({
            userId: "",
            recipeId: null
        });
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: External API returns error on first call
    // Expected status code: 500
    // Expected behavior: External API error detected and error returned
    // Expected output: "Cannot save chosen recipe at this point"
    test("External API fails on first call", async () => {
        let expectedResponse = "Cannot save chosen recipe at this point";
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        fetch.mockReturnValue(Promise.resolve({ status: 408, json: () => Promise.resolve({}) }));
        const res = await request(app).post("/recipes").send(baseMockedRequestBody);
        expect(res.status).toStrictEqual(500);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: External API returns error on second call
    // Expected status code: 500
    // Expected behavior: External API error detected and error returned
    // Expected output: "Cannot fetch instructions at this point"
    test("External API fails on second call", async () => {
        let expectedResponse = "Cannot fetch instructions at this point";
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        fetch.mockReturnValueOnce(Promise.resolve({ status: 201, json: () => Promise.resolve({
            summary: baseMockedDbFindRecordPostResponse.recipeSummary,
            title: baseMockedDbFindRecordPostResponse.recipeName
        })}));
        fetch.mockReturnValueOnce(Promise.resolve({ status: 405, json: () => Promise.resolve({})}));
        const res = await request(app).post("/recipes").send(baseMockedRequestBody);
        expect(res.status).toStrictEqual(500);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: Valid parameters and recipe already exists in database 
    // Expected status code: 200
    // Expected behavior: Recipe is fetched from db and numTimes incremented
    // Expected output: The saved recipe
    test("Recipe exists in db", async () => {
        let expectedResponse = Object.assign({}, baseMockedDbFindRecordPostResponse);
        expectedResponse.instructions = mockedInstructions;
        expectedResponse.numTimes++;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(Object.assign({}, baseMockedDbFindRecordPostResponse));
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        fetch.mockReturnValue(Promise.resolve({ status: 201, json: () => Promise.resolve(mockedInstructions)}));
        const res = await request(app).post("/recipes").send(baseMockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });

    // Input: Valid parameters and recipe does not exist in database 
    // Expected status code: 200
    // Expected behavior: Recipe is created and added to db
    // Expected output: The saved recipe
    test("Recipe does not exist in db", async () => {
        let expectedResponse = Object.assign({}, baseMockedDbFindRecordPostResponse);
        expectedResponse.instructions = mockedInstructions;
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        fetch.mockReturnValueOnce(Promise.resolve({ status: 201, json: () => Promise.resolve({
            summary: baseMockedDbFindRecordPostResponse.recipeSummary,
            title: baseMockedDbFindRecordPostResponse.recipeName
        })}));
        fetch.mockReturnValueOnce(Promise.resolve({ status: 201, json: () => Promise.resolve(mockedInstructions)}));
        const res = await request(app).post("/recipes").send(baseMockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });

    // Input: Valid parameters, recipe does not exist in database and recipeImage not supplied
    // Expected status code: 200
    // Expected behavior: Recipe is created and added to db with default image url
    // Expected output: The saved recipe
    test("Recipe does not exist in db and image not supplied", async () => {
        let expectedResponse = Object.assign({}, baseMockedDbFindRecordPostResponse);
        expectedResponse.instructions = mockedInstructions;
        expectedResponse.recipeImage = "https://visitors-centre.jrc.ec.europa.eu/sites/default/files/thumbnail/kmffq_additional-illo4video_2019_5_fishplate%5B1%5D.jpg"
        jest.spyOn(dbFunctions, "dbFindRecord").mockReturnValue(null);
        jest.spyOn(dbFunctions, "dbSaveRecord").mockReturnValue(null);
        fetch.mockReturnValueOnce(Promise.resolve({ status: 201, json: () => Promise.resolve({
            summary: baseMockedDbFindRecordPostResponse.recipeSummary,
            title: baseMockedDbFindRecordPostResponse.recipeName
        })}));
        fetch.mockReturnValueOnce(Promise.resolve({ status: 201, json: () => Promise.resolve(mockedInstructions)}));
        let mockedRequestBody = Object.assign({}, baseMockedRequestBody);
        mockedRequestBody.image = null;
        const res = await request(app).post("/recipes").send(mockedRequestBody);
        expect(res.status).toStrictEqual(200);
        expect(res.body).toEqual(expectedResponse);
    });
})