var fetch = require('node-fetch');

describe("Non-functional test for server crashes", () => {
    test("Error rate no more than 12", async () => {
        let crashFile = await fetch("https://cpen321-reciperoulette.westus.cloudapp.azure.com/static/crashes.txt");
        if (crashFile.status == 200) {
            let inputD = await crashFile.text()
            let dates = inputD.toString().split("\n")
            let dateFormatted = []
            for (const date of dates) {
                dateFormatted.push(Date.parse(date))
            }
            let diffTime = (new Date() - dateFormatted[0])/(1000*60*60);
            let errRate = (dateFormatted.length - 1) / diffTime;
            console.log("The error rate is %d errors per hour", errRate)
            expect(errRate).toBeLessThanOrEqual(12);
        } else {
            expect(false).toBeTruthy();
        }
    });
})
