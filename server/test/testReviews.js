// Interface GET www.myserver.ca/reviews
describe("Upload a photo", () => {
    // Input: test_photo.png is a correct photo
    // Expected status code: 201
    // Expected behavior: photo is added to the database
    // Expected output: photo_id
    test("Valid Photo", async () => {
    const res = await app.post("/photo/")
    .attach("photo", "test/res/test_photo.png");
    expect(res.status).toStrictEqual(201);
    expect(await Photo.getAllPhotos()
    .toEqual(expect.arrayContaining(["test_photo.png"])));
    });
    // Input: no photo
    // Expected status code: 401
    // Expected behavior: database is unchanged
    // Expected output: None
    test("No Photo", async () => {
    //...
    });
    // Input: bad_photo.txt is a not a photo
    // Expected status code: 400
    // Expected behavior: database is unchanged
    // Expected output: None
    test("Bad Photo", async () => {
    const res = await app.post("/photo/")
    .attach("photo", "test/res/bad_photo.txt");
    expect(res.status).toStrictEqual(400);
    expect(await Photo.getAllPhotos()
    .toEqual(expect.not.arrayContaining(
    ["bad_photo.txt"])));
    });
    //... more tests ...
    });
    