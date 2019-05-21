var WebSocketServer = require('ws').Server;
wss = new WebSocketServer({port: 8081, path: '/main'});

let onibusSelecionadoAgora;

wss.on('connection', function(ws) {
    // Quando o cliente conecta, sobe o servidor HTTP para receber o onibus selecionado
    var http = require('http');

    http.createServer(function (req, res) {
        req.on('data', function (chunk) {
            onibusSelecionadoAgora = chunk.toString();

            var net = require('net');
            // Conecta ao servidor Java
            var client = net.connect(8888, 'localhost');
            // Envia para o server java o onibus selecionado, para ativar o Esper
            client.write(onibusSelecionadoAgora + '\n');
            client.on('data', (data) => {
                var dados = data.toString();
                var latLong = dados.split(" ");
                var latitude = latLong[0];
                var longitude = latLong[1];
                ws.send(JSON.stringify({ "latitude": latitude, "longitude": longitude }));
            });
        });
    }).listen(6969);
});
