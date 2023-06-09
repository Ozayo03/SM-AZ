const tcpSocket = require('net').Socket()
const httpServer = require('http').createServer()
const Server = require('socket.io').Server
const fs = require('fs')
const url = require('url')
const cors = require('cors')
const upload = require('express-fileupload')
const express = require('express')
const path = require('path')
const app = express()

app.use(upload())
app.use(
    cors({
        origin: "*"
    })
)
app.listen(8081, () => {console.log("listening on port 8081")})

// app.set("views", path.join(__dirname, "views"))
// app.set("view engine", "ejs")

var userData
var allUsers = []
var confData

app.get('/home', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/home.ejs", {
        userData: userData,
        allUsers: allUsers,
    })
})

app.get('/login', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/login.ejs")
})

app.get('/signup', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/signup.ejs")
})

app.get('/conference', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/conference.ejs", {
        confData: confData
    })
})

app.get('/fileinput', (req, res) => {
    res.render("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/ejs/fileinput.ejs")
})

app.post('/sendfile', (req, res) => {
    console.log(req.files.file.data)
    let file = req.files.file


    // fr.readAsBinaryString(file)
    //
    // fr.addEventListener("load", () => {
    //     const result = fr.result
    //     console.log(result)
    // })
})

app.get('/models/app.js', (req, res) => {
    res.sendFile("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/models/app.js")
})

app.get('/models/gateway.js', (req, res) => {
    res.sendFile("/home/muano/Desktop/ConferenceManagementTool/CMTFrontEnd/views/models/gateway.js")
})

httpServer.listen(8080, () => console.log('listening on http://localhost:8080'))

/*
    SOCKET.IO SOCKET CONNECTION WHICH COMMUNICATES VIA THE TCP SOCKET CONNECTED
    TO THE BACKEND
*/
// const httpServer = createServer
const io = new Server(httpServer, {
    cors: {origin: "*"}
})

io.on('connection', (socket) => {
    console.log(" the gateway is connected with the stream")
    /*
    TCP SOCKET CONNECTION WHICH IS A MEDIATOR BETWEEN THE SOCKET.IO SOCKET
    AND THE BACK END GATEWAY TCP CONNECTION
    */
    tcpSocket.connect(13000, "127.0.0.1", () => {
        console.log("TCP connection to 127.0.0.1:13000 established")
    })

    tcpSocket.on("close", () => {
        console.log("TCP connection to 127.0.0.1:13000 closed")
    })

    tcpSocket.on('data', (tcpSocketRes) => {
        if (tcpSocketRes.toString() == "send signup details") {
            console.log("\n response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("send signup details", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "send pword") {
            console.log("\n response from tcpSocket (send pword) : " + tcpSocketRes.toString())
            socket.emit("send pword", tcpSocketRes.toString())
            console.log("emitted 'send pword' to client" + tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "signup successful") {
            console.log("\n response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("signup successful", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "incorrect pword or email") {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("incorrect pword or email", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString().includes(">>")) {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            // get all userData belonging to the user
            userData = JSON.parse(tcpSocketRes.toString().split(">>")[1])
            socket.emit('logged in', tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString().includes("**")) {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            // get all confData belonging to the conference
            confData = JSON.parse(tcpSocketRes.toString().split("**")[1])
            // confData = "This is pretty much working"
            socket.emit('confData received', tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString().includes("<<")) {
            var responseStr = tcpSocketRes.toString().split("<<")[1]
            console.log("response from tcpSocket : " + responseStr)
            allUsers = JSON.parse(responseStr)
            // socket.emit('allusers', responseStr)
        }
        else if (tcpSocketRes.toString() == "conference created")
        {
            console.log("response from tcpSocket : " + tcpSocketRes.toString())
            socket.emit("conference created", tcpSocketRes.toString())
        }
        else if (tcpSocketRes.toString() == "send file")
        {
            socket.emit("send file", tcpSocketRes.toString())
        }
    })

    socket.on('request message', (reqMsg) => {
        tcpSocket.write(reqMsg.toString() + "\r\n")
    })

    socket.on('signup details', (signudetails) => {
        tcpSocket.write(signudetails.toString() + "\r\n")
    })

    socket.on('client pword', (pword) => {
        tcpSocket.write(pword.toString() + "\r\n")
    })

    socket.on('my file', (byteData) => {
        console.log(byteData)
        var jsonData = {ByteData: byteData}
        tcpSocket.write(JSON.stringify(jsonData) + "\r\n")
        console.log("Done Sending")
    })

    socket.on('disconnect', () => {
        console.log("gateway and stream are disconnected")
    })
})

