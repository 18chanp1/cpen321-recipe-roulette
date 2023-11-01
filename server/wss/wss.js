const ws = require("ws").Server
const uuidv4 = require("uuid").v4

function setupWSS(httpsServer)
{
    let wss = new ws({server: httpsServer});
    let requests = {};

    wss.on("connection", (ws) =>
    {
        console.log("connection");

        //send a sample message
        // msg = 
        // {
        //     entryID: "69",
        //     name: "JuliaRubin",
        //     details: "I like this recipe",
        //     contact: "123456",
        //     image: "https://ece.ubc.ca/files/2017/03/2016a-13-e1580928549507.jpeg",
        //     type: "SHOPREQ"
        // }

        // ws.send(JSON.stringify(msg));


        ws.on("message", (message) =>
        {
            //initialization message.
            let obj = JSON.parse(message.toString());
            console.log(obj);

            //check if it is initialization message
            if(obj.type == "NEWCOOKREQ" || obj.type == "NEWSHOPREQ")
            {
                //change type
                obj.type = obj.type.substring(3);

                //announce it to everyone
                ann = {}
                ann.entryID = uuidv4()
                ann.name = obj.name
                ann.details = obj.details
                ann.contact = obj.contact
                ann.image = obj.image
                ann.type = obj.type

                for(const e in requests)
                {
                    e.ws.send(JSON.stringify(ann))
                }

                console.log(ann);

                //add to requests
                obj.ws = ws
                requests[ann.entryID] = obj
                ws.entryID = ann.entryID
            }
        })

        ws.on("close", () =>
        {
            //find the corresponding request
            let id = ws.entryID
            delete requests[id]

            //announce it to everyone else
            ann = {
                entryID: id,
                type: "DEL"
            }

            for (const e in requests)
            {
                e.ws.send(JSON.stringify(ann))
            }

            console.log(ann);
        })
    })

    return wss;
}

module.exports = setupWSS