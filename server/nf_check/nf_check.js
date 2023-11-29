const fs = require("fs")

fs.readFile("crashes.txt", (err, inputD) =>
{
    if(err) throw err

    let dates = inputD.toString().split("\n")

    let dateFormatted = []

    for(const date of dates)
    {
        dateFormatted.push(Date.parse(date))
    }

    console.log(Date.parse(dates[0]))

    let diffTime = (new Date() - dateFormatted[0])/(1000*60*60);
    let errRate = (dateFormatted.length - 1) / diffTime;

    console.log("The error rate is %d errors per hour", errRate)
})
