const ws = require("ws").Server

function setupWSS(httpsServer)
{
    let wss = new ws({server: httpsServer});

    wss.on("connection", (ws) =>
    {
        console.log("connection");

        //send a sample message
        msg = 
        {
            entryID: "69",
            name: "JuliaRubin",
            details: "I like this recipe",
            contact: "123456",
            image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg",
            type: "SHOPREQ"
        }

        ws.send(JSON.stringify(msg));


        ws.on("message", (message) =>
        {
            console.log("message: " + message);
            ws.send("echo: " + message);
        })

        ws.on("close", () =>
        {
            console.log("closed");
        })
    })

    return wss;
}

module.exports = setupWSS