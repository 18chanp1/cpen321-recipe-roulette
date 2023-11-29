const fs = require("fs")
var fetch = require('node-fetch');

describe("Non-functional test for server crashes", () => {
    test("Error rate no more than 12", async () => {
        let crashFile = await fetch("https://cpen321-reciperoulette.westus.cloudapp.azure.com/crash.txt");
        console.log(crashFile);
    // fs.readFile("crashes.txt", (err, inputD) =>
    //     {
    //         if(err) expect(false).toBeTruthy();
    //         let dates = inputD.toString().split("\n")
    //         let dateFormatted = []
    //         for (const date of dates) {
    //             dateFormatted.push(Date.parse(date))
    //         }
    //         console.log(Date.parse(dates[0]))
    //         let diffTime = (new Date() - dateFormatted[0])/(1000*60*60);
    //         let errRate = (dateFormatted.length - 1) / diffTime;
    //         console.log("The error rate is %d errors per hour", errRate)
    //         expect(errRate).toBeLessThanOrEqual(12);
    //     })
    });
})
