<html>
<head>
    <script>
        var socket = new WebSocket("ws://localhost:8081/websocket");

        socket.onopen = function () {
            socket.send('Ping');
        };

        socket.onmessage = function (e) {
            document.write(e.data);
            socket.close();
        };
    </script>
</head>
<body>
    BODY
</body>
</html>