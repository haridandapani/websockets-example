<!DOCTYPE html>
<meta charset="UTF-8">
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>WebsSockets</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>

    <div id = "form">
        <form onsubmit="return logger(event)">
            Room<input id = "room" type="number" step = "1" min = "0" name="room" value="" required>
            Name<input id = "username" type="text" name="position" value="" required>
            <input type="submit" value="Submit">
        </form>
    </div>

    <div id = "chatter" hidden>
        <div id="chatControls">
            <input id="message" placeholder="Type your message">
            <button id="send">Send</button>
        </div>
        <ul id="userlist"> <!-- Built by JS --> </ul>
        <div id="chat">    <!-- Built by JS --> </div>
    </div>
    <script src="websocketDemo.js"></script>
</body>
</html>
