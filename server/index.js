
var socketIO = require('socket.io'),
    http = require('http'),
    port = process.env.PORT || 4000,
    ip = process.env.IP || '192.168.1.108',
    server = http.createServer().listen(port, ip, function() {
        console.log("IP = " , ip);
        console.log("start socket successfully");
    });

io = socketIO.listen(server);
//io.set('match origin protocol', true);
io.set('origins', '*:*');

// to store all users in this array
var users = [];

var run = function(socket) {

    // store the current user id so we can remove it when its disconect
    var currentUserId = "";

    // to recieve the message and forwared it for all users
    socket.on("message", function(value) {
        socket.broadcast.emit("message", value);
    });
    
    // to recieve the message and forwared it for all users
    socket.on("room-message", function(value) {
        socket.broadcast.emit("room-message", value);
    });

    // to add the new user to the users list
    socket.on("user-join", function(value) {
        console.log(value.userName);  // print the username in the console
        currentUserId = value.userId; // hold the current user id so we can remove it when it's disconnected
        users.push(value);            // add the user to users array
        socket.broadcast.emit("users-connected", users); // send users array to the client
    });

    // this listener will send all the users for the client
    socket.on("request-users", function() {
        console.log("client asked for users");
        socket.emit("users-connected", users);
    });

    // to remove the disconnected user
    socket.on("disconnect", function () {
        var newUsers = [];
        for (var i = 0; i < users.length; i++) 
        {
            users.filter(function(element) {
                if (element.userId !== currentUserId)
                {
                    newUsers.push(element);
                }
               return element.userId === currentUserId;
            });
        }
        users = newUsers;
        socket.broadcast.emit("users-connected", users);
    });

}

io.sockets.on('connection', run);
