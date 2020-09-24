/*
 *   Place client application Javascript code here.
 */

var xLoc;
var yLoc;
var headerHeight;
var gameID;
var redPlayer;
var bluePlayer;
var gridLocation;
var xStart;
var xEnd;
var currentPlayer;
var gameBoard;
var movesInGame;
var currentMove;
var eventSource;
var isFinished;
var currentPlayerTurn;

var main = function() {

    let createUserButton = document.getElementById("createUserButton");
    createUserButton.addEventListener("click", createAccEvent);

    let seeLeaderboardButton = document.getElementById("leaderboardButton");
    seeLeaderboardButton.addEventListener("click", leaderBoardEvent);

    let seeCompletedGames = document.getElementById("completedButton");
    seeCompletedGames.addEventListener("click", completedGamesEvent);

    let createGameButton = document.getElementById("createNewGameButton");
    createGameButton.addEventListener("click", createNewGameEvent);

    let joinGameButton = document.getElementById("joinGameButton");
    joinGameButton.addEventListener("click", joinGameEvent);

    let watchGamesButton = document.getElementById("inProgressButton");
    watchGamesButton.addEventListener("click", watchGamesEvent);

    document.getElementById("replayButton").style.display = 'none';
    document.getElementById("moveButtons").style.display = 'none';

    gameBoard = document.getElementById("gameBoard-canvas");
    gameBoard.addEventListener('click', gameBoardEventListener);

    let previousMoveButton = document.getElementById("previousMoveButton");
    previousMoveButton.addEventListener("click", previousMoveInReplay);

    let nextMoveButton = document.getElementById("nextMoveButton");
    nextMoveButton.addEventListener("click", nextMoveInReplay);

    let startReplayButton = document.getElementById("replayButton");
    startReplayButton.addEventListener("click", startGameReplay);

    document.getElementById("replayButton").style.display = 'none';
    document.getElementById("replayMovesArticle").style.display = 'none';

};

var init = function(evt){

    //one stream for both move history and updating board.
    //use a big json object.
//    let eventSource = new EventSource('/game');

//    var messageInput = document.getElementById("gameBoard-canvas");
//    messageInput.addEventListener("click", function(e){
//
//    if (typeof(gameID) != "undefined"){
//
//        let json = {
//            xVal: gridLocation.column,
//            yVal: gridLocation.row,
//            id: gameID
//        };
//    });
//
//    }

};

var sendMessage = function(message){

    fetch("game/broadcastBoard", {method: "POST", headers:{"Content-Type": "application/json"}, body: JSON.stringify(message) })
                .then(function (response) {
                    if (!response.ok) {
                        console.log("an error occurred");
                    } else {
                        response.text().then( function(value) {
                            //the games are returned as value as a json object
                            console.log(value + " from game/broadcast");

                        });
                    }
                })
};

var messageReceived = function(e){
    console.log("json: " + e.data);
    var json = JSON.parse(e.data);
    var x = json.xVal;
    var y = json.yVal;
    var id = json.id;
    var thisPlayer = json.player;

    let ctx = gameBoard.getContext("2d");

    if (thisPlayer === redPlayer)
        ctx.fillStyle = "red";
    else if (thisPlayer === bluePlayer)
        ctx.fillStyle = "blue";

        //draw piece on board

        ctx.beginPath();
        ctx.arc(375 + (x * 28) + 14 , (y *28) + 14, 8, 0, 2 * Math.PI);
        ctx.stroke();
        ctx.fill();



    fetch("/game/" + id + "/isFinished", { method: "GET"} )
        .then(function (response) {
            if (!response.ok) {
                console.log("Error: " + response.status);
            } else {
                response.text().then( function(value) {
                    if (value === "true")
                        drawGameOver();
                });

            }
    });

    var moveTable = document.getElementById("movesTable");
    var owner;

    fetch("game/" + id + "/movesInGame", {method: "GET"} )
            .then(function(response) {
                if( !response.ok ){
                    console.log("Error in message received movesinGame");
                } else {
                    response.text().then(function (value) {
                        movesInGame = JSON.parse(value);
                        currentMove = -1;
                        console.log(movesInGame);
                        console.log(redPlayer);
                        console.log(bluePlayer);


                        var row = movesTable.insertRow();
                        var moveNumberCell = row.insertCell(0);
                        var playerNameCell = row.insertCell(1);
                        var xCoordinateCell = row.insertCell(2);
                        var yCoordinateCell = row.insertCell(3);
                        moveNumberCell.innerHTML = movesInGame.length;
                        playerNameCell.innerHTML = movesInGame[movesInGame.length-1].owner.name;
                        xCoordinateCell.innerHTML = String.fromCharCode(65 + movesInGame[movesInGame.length-1].x);
                        yCoordinateCell.innerHTML = movesInGame[movesInGame.length-1].y + 1;

                        owner = movesInGame[movesInGame.length-1].owner.name;
                        determineTurn(owner, id);

                    });
                }
            });

//            var other;
//
//            console.log("redplayer: " + redPlayer + " bluePlaer: " + bluePlayer);
//            if (owner === redPlayer)
//                other = bluePlayer;
//            else
//                other = redPlayer;
//
//            console.log("owner: " + owner + " other: " + other);

//            if (movesInGame.length === 1)
//                updatePlayerTurn(bluePlayer);
//
//            if (movesInGame[movesInGame.length-1] === movesInGame[movesInGame.length-2])


};

var determineTurn = function(player, gID){
    var other;
    if (player === redPlayer)
        other = bluePlayer;
    else
        other = redPlayer;


    if(typeof(movesInGame) === "undefined"){
        fetch("game/" + gID + "/movesInGame", {method: "GET"} )
            .then(function(response) {
                if( !response.ok ){
                    console.log("Error in determine player");
                } else {
                    response.text().then(function (value) {
                        movesInGame = JSON.parse(value);

                        if (movesInGame.length === 1)
                            updatePlayerTurn(bluePlayer);
                        else if (movesInGame[movesInGame.length-1].owner.name === movesInGame[movesInGame.length-2].owner.name)
                            updatePlayerTurn(other);
                        else
                            updatePlayerTurn(player);
                    });
                }
            });
    }else{

        if (movesInGame.length === 1)
            updatePlayerTurn(bluePlayer);
        else if (movesInGame[movesInGame.length-1].owner.name === movesInGame[movesInGame.length-2].owner.name)
            updatePlayerTurn(other);
        else
            updatePlayerTurn(player);
    }

};

var gameBoardEventListener = function(evt){
    var mousePos = getMousePosition(gameBoard, evt);
    gridLocation = getGridLocation(mousePos.x, mousePos.y, 28);
    checkFinished(currentPlayer)
};

var watchGamesEvent = function () {
    fetch("menu/inProgress", {method: "GET"})
        .then(function (response) {
            if (!response.ok) {
                console.log("an error occurred");
            } else {
                response.text().then( function(value) {
                    //the games are returned as value as a json object
                    drawMyGames(value);
                });
            }
        })
};

var loadGameBoard = function(id) {
   eventSource = new EventSource('/game/' + id);
   eventSource.onmessage = messageReceived;

    let ctx = gameBoard.getContext("2d");
    fetch("game/getBoard/" + id, {method: "GET"})
        .then(function (response) {
            if (!response.ok){
                console.log("error in loadGameBoard");
            }else{
                response.text().then(function (value) {
                    drawGameBoard();
                    drawMovesList();

                    determineTurn(currentPlayer, id);

                    let board = (JSON.parse(value)).Board;
                    for(var i = 0; i < board.length; i++) {
                        let xVal = board[i].x;
                        let yVal = board[i].y;
                        let color = board[i].color;
                        ctx.fillStyle = color;
                        //draw piece on board
                        ctx.beginPath();
                        ctx.arc(375 + (xVal * 28) + 14, (yVal * 28) + 14, 8, 0, 2 * Math.PI);
                        ctx.stroke();
                        ctx.fill();
                    }
                })
            }
        })
}

var createNewGameEvent = function() {

    let user1 = document.getElementById("user1").value;
    let user2 = document.getElementById("user2").value;
    var priv = document.getElementById('priv').checked;

    redPlayer = user1;
    currentPlayer = user1;
    bluePlayer = user2;

    let json = {
        red: user1,
        blue: user2,
        private: priv
    };

    fetch("game/createGame", {method: "PUT", body: JSON.stringify(json)})
        .then(function (response) {
            let el = document.getElementById("create-user-area");
            if (response.status == 404) {
                el.innerText = "1 or both users not found, game not created";
                el.style.color = "red";
                console.log("user not found, game not created");
            } else if (!response.ok) {
                el.innerText = "Error code: " + response.status;
                el.style.fontWeight = "bold";
                el.style.color = "red";
                console.log("broke");
            } else {
                response.text().then( function(value) {
                gameID = value;
                eventSource = new EventSource('/game/' + gameID);
                console.log("After new event source");
                eventSource.onmessage = messageReceived;
                //maybe just join game and then create event source in join game rather than
                //handlin gspecial case.
                drawGameBoard();
                updatePlayerTurn(redPlayer);
                });
            }
    });
};

var joinGameEvent = function(e){
    let username = document.getElementById("joinName").value;
    currentPlayer = username;

    let json = {
        name: username
    };

    fetch("menu/myGames", {method: "PUT", body: JSON.stringify(json)})
            .then(function (response) {
            let el = document.getElementById("joinName");
                if (!response.ok) {
                    el.value = "";
                    el.placeholder = "User not found/no games in progress"
                } else {
                    response.text().then( function(value) {
                        //the games are returned as value as a json object
                        drawMyGames(value);
                    });
                }
        });
};

var drawReplayGames = function(myGamesJSON){
    hideMenu();
    document.getElementById("gameBoard-canvas").style.display = 'none';
    document.getElementById("leaderBoard-canvas").style.display = 'none';

    let myGamesCanvas = document.getElementById("myGames-canvas");
    myGamesCanvas.style.display = "block";
    let ctx = myGamesCanvas.getContext("2d");

    myGamesCanvas.width = (window.screen.width - 50) * 0.75;
    myGamesCanvas.height = window.screen.height - 100;

    let w = myGamesCanvas.width;
    let h = myGamesCanvas.height;

    var gameList = JSON.parse(myGamesJSON);
    var rows = gameList.gameInfos;

    let xStart = (w * 0.2);
    let xEnd = (w * 0.8);

    drawMyGamesHeader(myGamesCanvas, xStart, xEnd);

    yLoc += (headerHeight/2) + 12;
    var rowsLength = rows.length;
    var colWidth = (xEnd - xStart)/3;

    ctx.lineWidth = "2";
    ctx.strokeStyle = "gray";
    for (var i = 0; i < rowsLength; i++){
        //draw the white background bar by bar
        ctx.fillStyle = "white";
        ctx.fillRect(xStart, yLoc - 15, (xEnd - xStart), 21 );
        ctx.fillStyle = "black";
        ctx.fillText(rows[i].id, xLoc, yLoc);
        ctx.fillText(rows[i].redPlayer, xLoc + colWidth, yLoc);
        ctx.fillText(rows[i].bluePlayer, xLoc + 2*colWidth, yLoc);
        ctx.beginPath();
        ctx.lineWidth = "2";
        ctx.strokeStyle = "gray";

        //Draw the vertical lines between columns
        ctx.moveTo(xLoc + (colWidth/2) + 20, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2) + 20, yLoc + 6);
        ctx.stroke();

        ctx.moveTo(xLoc + (colWidth/2) + colWidth + 20, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2) + colWidth + 20, yLoc + 6);
        ctx.stroke();

        //Draw left vertical line
        ctx.moveTo(xStart, yLoc - 15);
        ctx.lineTo(xStart, yLoc + 6);
        ctx.stroke();

        //Draw the right vertical line
        ctx.moveTo(xEnd, yLoc - 15);
        ctx.lineTo(xEnd, yLoc + 6);
        ctx.stroke();

        yLoc += 6;

        //Draw the horizontal lines between rows
        ctx.beginPath();
        ctx.moveTo(xStart, yLoc);
        ctx.lineTo(xEnd, yLoc);
        ctx.stroke();

        yLoc += 15;
    }

    //Move the yLoc back up after the loop.
    yLoc += -15;

    myGamesCanvas.addEventListener('click', function(evt) {
        var mousePos = getMousePosition(myGamesCanvas, evt);
        if (mousePos.x >= xStart && mousePos.x <= xEnd &&
            mousePos.y >= 50 && mousePos.y <= 50 + (21 * rowsLength)){
            gridLocation = getMyGameLocation(mousePos.x, mousePos.y, 21);

            var id = rows[gridLocation.row].id;
            redPlayer = rows[gridLocation.row].redPlayer;
            bluePlayer = rows[gridLocation.row].bluePlayer;
            gameID = id;
            loadGameBoard(id);
            //Had to add this to make the button not show up on list of games screen.
            document.getElementById("replayButton").style.display = 'initial';
        }
    }, false);
};

var drawMyGames = function(myGamesJSON){
    hideMenu();
    document.getElementById("gameBoard-canvas").style.display = 'none';
    document.getElementById("leaderBoard-canvas").style.display = 'none';

    let myGamesCanvas = document.getElementById("myGames-canvas");
    let ctx = myGamesCanvas.getContext("2d");

    myGamesCanvas.width = (window.screen.width - 50) * 0.75;
    myGamesCanvas.height = window.screen.height - 100;

    let w = myGamesCanvas.width;
    let h = myGamesCanvas.height;

    var gameList = JSON.parse(myGamesJSON);
    var rows = gameList.gameInfos;

    let xStart = (w * 0.2);
    let xEnd = (w * 0.8);

    drawMyGamesHeader(myGamesCanvas, xStart, xEnd);

    yLoc += (headerHeight/2) + 12;
    var rowsLength = rows.length;
    var colWidth = (xEnd - xStart)/3;

    ctx.lineWidth = "2";
    ctx.strokeStyle = "gray";
    for (var i = 0; i < rowsLength; i++){

        //draw the white background bar by bar
        ctx.fillStyle = "white";
        ctx.fillRect(xStart, yLoc - 15, (xEnd - xStart), 21 );

        ctx.fillStyle = "black";
        ctx.fillText(rows[i].id, xLoc, yLoc);
        ctx.fillText(rows[i].redPlayer, xLoc + colWidth, yLoc);
        ctx.fillText(rows[i].bluePlayer, xLoc + 2*colWidth, yLoc)

        ctx.beginPath();
        ctx.lineWidth = "2";
        ctx.strokeStyle = "gray";

        //Draw the vertical lines between columns
        ctx.moveTo(xLoc + (colWidth/2) + 20, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2) + 20, yLoc + 6);
        ctx.stroke();

        ctx.moveTo(xLoc + (colWidth/2) + colWidth + 20, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2) + colWidth + 20, yLoc + 6);
        ctx.stroke();

        //Draw left vertical line
        ctx.moveTo(xStart, yLoc - 15);
        ctx.lineTo(xStart, yLoc + 6);
        ctx.stroke();

        //Draw the right vertical line
        ctx.moveTo(xEnd, yLoc - 15);
        ctx.lineTo(xEnd, yLoc + 6);
        ctx.stroke();

        yLoc += 6;

        //Draw the horizontal lines between rows
        ctx.beginPath();
        ctx.moveTo(xStart, yLoc);
        ctx.lineTo(xEnd, yLoc);
        ctx.stroke();

       yLoc += 15;
    }

    //Move the yLoc back up after the loop.
    yLoc += -15;

    myGamesCanvas.addEventListener('click', function(evt) {
            var mousePos = getMousePosition(myGamesCanvas, evt);
            if (mousePos.x >= xStart && mousePos.x <= xEnd &&
             mousePos.y >= 50 && mousePos.y <= 50 + (21 * rowsLength)){
                gridLocation = getMyGameLocation(mousePos.x, mousePos.y, 21);
                var id = rows[gridLocation.row].id;
                redPlayer = rows[gridLocation.row].redPlayer;
                bluePlayer = rows[gridLocation.row].bluePlayer;
                gameID = id;
               loadGameBoard(id);
            }
        }, false);
};

function getMyGameLocation(posX, posY, gridSize) {
    var cellRow = Math.floor( (posY - 50) / gridSize );
    var cellCol = Math.floor(posX / gridSize);
    return {row: cellRow, column: cellCol};
};

var drawMyGamesHeader = function(canvas, xStart, xEnd){
    ctx = canvas.getContext("2d");
    let w = canvas.width;
    let h = canvas.height;

    ctx.lineWidth = "5";
    ctx.fillStyle = "white";
    headerHeight = 50;
    ctx.fillRect( xStart, 2, (xEnd - xStart), headerHeight );

    //Draw box around
    ctx.strokeStyle = "gray";
    ctx.lineWidth = "3";
    ctx.rect(xStart, 2, (xEnd - xStart), headerHeight);
    ctx.stroke();

    //Draw the vertical bars colwidth apart.
    ctx.lineWidth = "2";
    var colWidth = (xEnd - xStart)/3;
    for (var i = xStart; i <= (xEnd - xStart); i+= colWidth){
        ctx.beginPath();
        if (i != xStart){
            ctx.moveTo(i, 2);
            ctx.lineTo(i, headerHeight);
            ctx.stroke();
        }
    }

    xLoc = xStart + (colWidth/2) - 20;
    yLoc = 30;

    ctx.font = "14px Sans SC"
    ctx.fillStyle = "black";
    ctx.fillText("Game ID", xLoc, yLoc);
    ctx.fillText("Red Player", xLoc + colWidth, yLoc);
    ctx.fillText("Blue Player", xLoc + 2*colWidth, yLoc);
};


var drawGameBoard = function () {
    hideMenuAndNavAndFooter();
    document.getElementById("gameBoard-canvas").style.display = 'initial';
    document.getElementById("leaderBoard-canvas").style.display = 'none';
    document.getElementById("myGames-canvas").style.display = 'none';
    document.getElementById("replayMovesArticle").style.display = 'initial';
    document.getElementById("moveButtons").style.display = 'none';

    let ctx = gameBoard.getContext("2d");

    gameBoard.width = 1000;
    //gameBoard.height = 532;
    gameBoard.height = 600;

    ctx.fillStyle = "#bf912f";
    ctx.fillRect(375, 0, 532, 532);

    for(var i = 0; i < 19; i++){
        ctx.moveTo(i * 28 + 375, 0);
        ctx.lineTo(i * 28 + 375, 532);
        ctx.stroke();
        ctx.moveTo(375, i  * 28);
        ctx.lineTo(907, i * 28);
        ctx.stroke();
    }

    ctx.fillStyle = "black";
    ctx.font = "19px Sans SC";

    for (var i = 0 ; i < 19; i++){
        ctx.fillText(String.fromCharCode(65 + i), 375 + 8 + (28*i), 549);
    };

    for (var i = 0; i <= 19; i++){
        if (i < 10)
            ctx.fillText(i, 375 - 14, 0 + (28*i) - 5 );
        else
            ctx.fillText(i, 375 - 23, 0 + (28*i) - 5);
    };
};

var updatePlayerTurn = function(player){
    document.getElementById("currentplayermove").style.display = 'initial';
    document.getElementById("currentplayermove").innerText = player + "'s Turn";
    currentPlayerTurn = player;
};

var drawEmptyGameBoard = function () {
    hideMenuAndNavAndFooter();
    document.getElementById("gameBoard-canvas").style.display = 'initial';
    document.getElementById("leaderBoard-canvas").style.display = 'none';
    document.getElementById("myGames-canvas").style.display = 'none';

    gameBoard = document.getElementById("gameBoard-canvas");
    let ctx = gameBoard.getContext("2d");

    gameBoard.width = 1000;
    gameBoard.height = 600;

    ctx.fillStyle = "#bf912f";
    ctx.fillRect(375, 0, 532, 532);

    for(var i = 0; i < 19; i++){
        ctx.moveTo(i * 28 + 375, 0);
        ctx.lineTo(i * 28 + 375, 532);
        ctx.stroke();
        ctx.moveTo(375, i  * 28);
        ctx.lineTo(907, i * 28);
        ctx.stroke();
    }

     ctx.fillStyle = "black";
        ctx.font = "19px Sans SC";

        for (var i = 0 ; i < 19; i++){
            ctx.fillText(String.fromCharCode(65 + i), 375 + 8 + (28*i), 549);
        };

        for (var i = 0; i <= 19; i++){
            if (i < 10)
                ctx.fillText(i, 375 - 14, 0 + (28*i) - 5 );
            else
                ctx.fillText(i, 375 - 23, 0 + (28*i) - 5);
        };


};

var checkFinished = function(){
    fetch("/game/" + gameID + "/isFinished", { method: "GET"} )
                .then(function (response) {
                    if (!response.ok) {
                        console.log("Error: " + response.status);
                        return false;
                        //document.getElementById("joinName").value = "No games in progress";
                    } else {
                        response.text().then( function(value) {

                            /*If the returned value is false, then place the piece down, because the
                            game is still going */
                            if (value === "false"){
                                placePieceEvent();
                            }
                            else if (value === "true")
                                drawGameOver();

                        });
                    }
            });
};

var drawGameOver = function(){
    let gameOverCanvas = document.getElementById("gameBoard-canvas");
    let ctx = gameOverCanvas.getContext("2d");
    ctx.fillStyle = "white";
    ctx.font = "20px Sans SC";
    ctx.fillText("Game Over!", 250, 266);
};

function getMousePosition(canvas, evt) {
    var rect = canvas.getBoundingClientRect();
    return { x: evt.clientX-rect.left, y: evt.clientY-rect.top};
};

function getGridLocation(posX, posY, gridSize) {
    var cellRow = Math.floor(posY / gridSize);
    var cellCol = Math.floor((posX-375) / gridSize);
    return {row: cellRow, column: cellCol};
};

var placePieceOnReplay = function(thisPlayer, thisX, thisY) {
    //let gameBoard = document.getElementById("gameBoard-canvas");
    let ctx = gameBoard.getContext("2d");

    if (thisPlayer === redPlayer)
        ctx.fillStyle = "red";
    else if (thisPlayer === bluePlayer)
        ctx.fillStyle = "blue";

    //draw piece on board
    ctx.beginPath();
    ctx.arc(375 + (thisX * 28) + 14 , (thisY *28) + 14, 8, 0, 2 * Math.PI);
    ctx.stroke();
    ctx.fill();
};

var removePieceOnReplay = function(thisX, thisY) {
    let ctx = gameBoard.getContext("2d");
    ctx.fillStyle = "#bf912f";
    // Remove piece on board, which basically makes the game go back 1 turn.
    ctx.beginPath();
    ctx.arc(375 + (thisX * 28) + 14 , (thisY *28) + 14, 10, 0, 2 * Math.PI);
    ctx.fill();
};

var placePieceEvent = function(nameVal){
    let ctx = gameBoard.getContext("2d");
    var xVal = gridLocation.column;
    var yVal = gridLocation.row;

    let data = {
        x: xVal,
        y: yVal,
        name: currentPlayer
    };

    fetch("game/makeMove/" + gameID , {method: "PUT", headers:{"Content-Type": "application/json"}, body: JSON.stringify(data)} )
            .then( function(response){
                if (!response.ok){
                    console.log("can't make move " + response.status);
                    if(response.status == 403)
                        alert("Not your turn to make a move");
                    else if(response.status === 400)
                        alert("Invalid move");
                } else {
                    response.text().then( function(value) {

                    if (currentPlayer === redPlayer)
                        ctx.fillStyle = "red";
                    else if (currentPlayer === bluePlayer)
                        ctx.fillStyle = "blue";

                    //draw piece on board
                    ctx.beginPath();
                    ctx.arc(375 + (xVal * 28) + 14 , (yVal *28) + 14, 8, 0, 2 * Math.PI);
                    ctx.stroke();
                    ctx.fill();

                    let json = {
                        xVal: gridLocation.column,
                        yVal: gridLocation.row,
                        id: gameID,
                        player: currentPlayer
                    };

                     sendMessage(json);
                    });
                }
            });
};

var createAccEvent = function(e){
    e.preventDefault();
    var name = {name: document.getElementById("uname").value};

    fetch("menu/createUser", {method: "POST", body: JSON.stringify(name)} )
        .then( function(response){
            let el = document.getElementById("create-user-area");
            if (!response.ok){
                if (response.status == 400){
                    el.innerText = "User Already Exists"
                    el.style.color = "red";
                } else {
                el.innerText = "Error code: " + response.status;
                el.style.fontWeight = "bold";
                el.style.color = "red";
                }
            } else {
                response.text().then( function(value) {
                el.innerText = value;
                el.style.color = "green";
                el.style.fontWeight = "bold";
                });
            }
        });
};

var leaderBoardEvent = function(e) {
    fetch("/menu/leaderboard", { method: "GET"} )
        .then( function(response) {
            let el = document.getElementById("leaderboard-response-area");
            if( ! response.ok ) {
                el.innerText = "Error code: " + response.status;
                el.style.fontWeight = "bold";
                el.style.color = "red";
            } else {
                response.text().then( function(value) {
                    drawLeaderBoard(value);
                });
            }
        });

};

var completedGamesEvent = function() {

    fetch("menu/completed", {method: "GET"} )
        .then(function(response) {
        let el = document.getElementById("leaderboard-response-area");
        if( !response.ok ){
            el.innerText = "Error code: " + response.status;
            el.style.fontWeight = "bold";
            el.style.color = "red";
        } else {
            response.text().then(function (value) {
               drawReplayGames(value);
            });
        }
    });
};

var startGameReplay = function() {
    document.getElementById("leaderBoard-canvas").style.display = 'none';
    document.getElementById("myGames-canvas").style.display = 'none';
    document.getElementById("replayButton").style.display = 'none';
    document.getElementById("replayMovesArticle").style.display = 'initial';
    document.getElementById("moveButtons").style.display = 'initial';
    document.getElementById("currentplayermove").style.display = 'none';

    drawEmptyGameBoard();
    drawMovesList();


};

var drawMovesList = function (){

    fetch("game/" + gameID+ "/movesInGame", {method: "GET"} )
            .then(function(response) {
                if( !response.ok ){
                    // el.innerText = "Error code: " + response.status;
                    // el.style.fontWeight = "bold";
                    // el.style.color = "red";
                    console.log("Error");
                } else {
                    response.text().then(function (value) {
                        movesInGame = JSON.parse(value);
                        currentMove = -1;
                        console.log(movesInGame);
                        console.log(redPlayer);
                        console.log(bluePlayer);

                        var movesTable = document.getElementById("movesTable");

                        for( i = 0; i < movesInGame.length; i++) {
                            var row = movesTable.insertRow(i+1);
                            var moveNumberCell = row.insertCell(0);
                            var playerNameCell = row.insertCell(1);
                            var xCoordinateCell = row.insertCell(2);
                            var yCoordinateCell = row.insertCell(3);
                            moveNumberCell.innerHTML = i+1;
                            playerNameCell.innerHTML = movesInGame[i].owner.name;
                            xCoordinateCell.innerHTML = String.fromCharCode(65 + movesInGame[i].x);
                            yCoordinateCell.innerHTML = movesInGame[i].y + 1;
                            }
                    });
                }
            });


};

var previousMoveInReplay = function(){
    if (currentMove > -1) {
        removePieceOnReplay(movesInGame[currentMove].x, movesInGame[currentMove].y);
        currentMove--;
    } else {
        alert("Game replay is at the beginning already!");
    }
};

var nextMoveInReplay = function() {
    if (currentMove < movesInGame.length - 1) {
        currentMove++;
        placePieceOnReplay(movesInGame[currentMove].owner.name, movesInGame[currentMove].x, movesInGame[currentMove].y);
    } else {
        alert("Already at last move in replay!");
    }
};

var hideMenuAndNavAndFooter = function () {
    hideMenu();
    document.getElementById("footer").style.display = 'none';
    document.getElementById("nav").style.display = 'none';
};

var hideMenu = function(){
    document.getElementById("art1").style.display = 'none';
    document.getElementById("art2").style.display = 'none';
    document.getElementById("art3").style.display = 'none';
};

var drawLeaderBoard = function(jsonLeaderBoard){
    hideMenu();
    document.getElementById("gameBoard-canvas").style.display = 'none';
    document.getElementById("myGames-canvas").style.display = 'none';

    let leaderBoard = document.getElementById("leaderBoard-canvas");
    leaderBoard.style.display = "block";
    let ctx = leaderBoard.getContext("2d");
    leaderBoard.width = window.screen.width - 50;
    leaderBoard.height = window.screen.height - 100;

    let w = leaderBoard.width;
    let h = leaderBoard.height;

    let rows = (JSON.parse(jsonLeaderBoard)).leaderboardRows;

    drawLeaderBoardHeader(leaderBoard);

    yLoc += (headerHeight/2) + 12;
    var rowsLength = rows.length;
    var colWidth = w/6;

    ctx.lineWidth = "2";
    ctx.strokeStyle = "gray";
    for (var i = 0; i < rowsLength; i++){

        //draw the white background bar by bar
        ctx.fillStyle = "#d1f7a5";
        ctx.fillRect(2, yLoc - 15, w - 10, 21 );

        ctx.fillStyle = "black";
        ctx.fillText(i+1, xLoc, yLoc);
        ctx.fillText(rows[i].name, xLoc + colWidth, yLoc);
        ctx.fillText(rows[i].score, xLoc + 2*colWidth, yLoc);
        ctx.fillText(rows[i].wins, xLoc + 3*colWidth, yLoc);
        ctx.fillText(rows[i].losses, xLoc + 4*colWidth, yLoc);
        ctx.fillText(rows[i].ties, xLoc + 5*colWidth, yLoc);

        ctx.beginPath();
        ctx.lineWidth = "2";
        ctx.strokeStyle = "gray";


        ctx.moveTo(xLoc + (colWidth/2)+10, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2)+10, yLoc + 6);
        ctx.stroke();

        ctx.moveTo(xLoc + (colWidth/2)+colWidth + 10, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2)+colWidth + 10, yLoc + 6);
        ctx.stroke();

        ctx.moveTo(xLoc + (colWidth/2)+ 2*colWidth + 10, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2)+ 2*colWidth + 10, yLoc + 6);
        ctx.stroke();

        ctx.moveTo(xLoc + (colWidth/2)+ 3*colWidth + 10, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2)+ 3*colWidth + 10, yLoc + 6);
        ctx.stroke();

        ctx.moveTo(xLoc + (colWidth/2)+ 4*colWidth + 10, yLoc - 15);
        ctx.lineTo(xLoc + (colWidth/2)+ 4*colWidth + 10, yLoc + 6);
        ctx.stroke();

        //Draw left vertical line
        ctx.moveTo(2, yLoc - 15);
        ctx.lineTo(2, yLoc + 6);
        ctx.stroke();

        //Draw the right vertical line
        ctx.moveTo(w - 8, yLoc - 15);
        ctx.lineTo(w - 8, yLoc + 6);
        ctx.stroke();

        yLoc += 6;

        //Draw the horizontal lines between rows
        ctx.beginPath();
        ctx.moveTo(2, yLoc);
        ctx.lineTo(w - 8, yLoc);
        ctx.stroke();

       yLoc += 15;
    }

    //Move the yLoc back up after the loop.
    yLoc += -15;
};

var drawLeaderBoardHeader = function(canvas){
    ctx = canvas.getContext("2d");
    let w = canvas.width;
    let h = canvas.height;

    ctx.lineWidth = "5";
    ctx.fillStyle = "white";
    headerHeight = 50;
    ctx.fillRect( 2, 2, w - 10, headerHeight );

    ctx.strokeStyle = "gray";
    ctx.lineWidth = "3";
    ctx.rect(2, 2, w - 10, headerHeight);
    ctx.stroke();

    ctx.lineWidth = "2";
    var colWidth = w/6;
    for (var i = 0; i < w - 10; i+= colWidth){
        ctx.beginPath();
        if (i != 0){
            ctx.moveTo(i, 2);
            ctx.lineTo(i, headerHeight);
            ctx.stroke();
        }
    }

    xLoc = (colWidth/2) - 10;
    yLoc = 30;

    ctx.font = "14px Sans SC"
    ctx.fillStyle = "black";
    ctx.fillText("Rank", xLoc, yLoc);
    ctx.fillText("Name", xLoc + colWidth, yLoc);
    ctx.fillText("Score", xLoc + 2*colWidth, yLoc);
    ctx.fillText("Wins", xLoc + 3*colWidth, yLoc);
    ctx.fillText("Losses", xLoc + 4*colWidth, yLoc);
    ctx.fillText("Ties", xLoc + 5*colWidth, yLoc);
};

document.addEventListener("DOMContentLoaded", main);
document.addEventListener("DOMContentLoaded", init);
