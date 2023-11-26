var dbFunctions = require("../db/db").Functions;
var app = require("../src/app");
var request = require("supertest");

const addRecipe = (list, count, recipe) => {
    for (i = 0; i < count; i++) {
        list.push(recipe);
    }
}

const mockedRequestHeader = {
    email: "test@ubc.ca"
}

const meatRecipe = {
    userId: 'test@ubc.ca',
    recipeId: '795512',
    recipeName: 'Hearty Beef Stew',
    numTimes: 1,
    likes: 0,
    recipeSummary: "It's a stew"
}

const veggieRecipe = {
    userId: 'test@ubc.ca',
    recipeId: '795512',
    recipeName: 'Greek Salad',
    numTimes: 1,
    likes: 0,
    recipeSummary: "It's a salad"
}

const pastryRecipe = {
    userId: 'test@ubc.ca',
    recipeId: '795512',
    recipeName: 'American Pie',
    numTimes: 1,
    likes: 0,
    recipeSummary: "It's a pie"
}

const cheeseRecipe = {
    userId: 'test@ubc.ca',
    recipeId: '795512',
    recipeName: 'Gouda Cheese Puffs',
    numTimes: 1,
    likes: 0,
    recipeSummary: "It's a cheesy puff"
}

const flavorProfiles = [
    [
      "Carnivore Crusader",
      "Protein Paladin",
      "Savory Samurai"
    ],
    [
      "Cheese Connoisseur",
      "Dairy Dandy",
      "Fromage Fanatic",
    ],
    [
      "Diva Dough",
      "Sweet Swirler",
      "Bakery Buff"
    ],
    [
      "Green Gourmet",
      "Veggie Virtuoso",
      "Plant-based Picasso"
    ]
  ]

// Interface GET /flavourprofile
describe("Get user flavor profile", () => {
    // Input: Missing user email
    // Expected status code: 400
    // Expected behavior: Missing email detected and error returned
    // Expected output: "Missing user email"
    test("No recipes", async () => {
        let expectedResponse = "Missing user email";
        const res = await request(app).get("/flavourprofile").set({email: ""});
        expect(res.status).toStrictEqual(400);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: User with no recipes saved
    // Expected status code: 200
    // Expected behavior: No Flavour Profile generated
    // Expected output: "No Flavor Profile available"
    test("No recipes saved", async () => {
        let expectedResponse = "No Flavor Profile available";
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue([]);
        const res = await request(app).get("/flavourprofile").set(mockedRequestHeader);
        expect(res.status).toStrictEqual(200);
        expect(res.text).toEqual(expectedResponse);
        expect(res.body).toEqual({});
    });

    // Input: User with a lot of meat recipes saved
    // Expected status code: 200
    // Expected behavior: Meat Lover Flavour Profile generated
    // Expected output: "Meat Lover"
    test("Meat heavy recipes saved", async () => {
        let mockedDbResponse = [];
        addRecipe(mockedDbResponse, 5, meatRecipe);
        addRecipe(mockedDbResponse, 4, veggieRecipe);
        addRecipe(mockedDbResponse, 4, pastryRecipe);
        addRecipe(mockedDbResponse, 4, cheeseRecipe);
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbResponse);
        const res = await request(app).get("/flavourprofile").set(mockedRequestHeader);
        expect(res.status).toStrictEqual(200);
        expect(flavorProfiles[0]).toContain(res.text);
        expect(res.body).toEqual({});
    });

    // Input: User with a lot of cheese recipes saved
    // Expected status code: 200
    // Expected behavior: Cheese Lover Flavour Profile generated
    // Expected output: A cheese lover title
    test("Cheese heavy recipes saved", async () => {
        let mockedDbResponse = [];
        addRecipe(mockedDbResponse, 5, cheeseRecipe);
        addRecipe(mockedDbResponse, 4, veggieRecipe);
        addRecipe(mockedDbResponse, 4, meatRecipe);
        addRecipe(mockedDbResponse, 4, pastryRecipe);
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbResponse);
        const res = await request(app).get("/flavourprofile").set(mockedRequestHeader);
        expect(res.status).toStrictEqual(200);
        expect(flavorProfiles[1]).toContain(res.text);
        expect(res.body).toEqual({});
    });

    // Input: User with a lot of pastry recipes saved
    // Expected status code: 200
    // Expected behavior: Pastry Lover Flavour Profile generated
    // Expected output: A pastry lover title
    test("Pastry heavy recipes saved", async () => {
        let mockedDbResponse = [];
        addRecipe(mockedDbResponse, 5, pastryRecipe);
        addRecipe(mockedDbResponse, 4, veggieRecipe);
        addRecipe(mockedDbResponse, 4, meatRecipe);
        addRecipe(mockedDbResponse, 4, cheeseRecipe);
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbResponse);
        const res = await request(app).get("/flavourprofile").set(mockedRequestHeader);
        expect(res.status).toStrictEqual(200);
        expect(flavorProfiles[2]).toContain(res.text);
        expect(res.body).toEqual({});
    });

    // Input: User with a lot of veggie recipes saved
    // Expected status code: 200
    // Expected behavior: Veggie Lover Flavour Profile generated
    // Expected output: "Veggie Lover"
    test("Veggie heavy recipes saved", async () => {
        let mockedDbResponse = [];
        addRecipe(mockedDbResponse, 5, veggieRecipe);
        addRecipe(mockedDbResponse, 4, pastryRecipe);
        addRecipe(mockedDbResponse, 4, meatRecipe);
        addRecipe(mockedDbResponse, 4, cheeseRecipe);
        jest.spyOn(dbFunctions, "dbFindAllRecords").mockReturnValue(mockedDbResponse);
        const res = await request(app).get("/flavourprofile").set(mockedRequestHeader);
        expect(res.status).toStrictEqual(200);
        expect(flavorProfiles[3]).toContain(res.text);
        expect(res.body).toEqual({});
    });
})