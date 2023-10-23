const ws = require("ws").Server

function setupWSS(httpsServer)
{
    let wss = new ws({server: httpsServer});

    wss.on("connection", (ws) =>
    {
        console.log("connection");
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