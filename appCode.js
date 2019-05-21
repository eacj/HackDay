var WebSocketServer = require('ws').Server;
wss = new WebSocketServer({port: 8081, path: '/main'});

let onibusSelecionadoAgora;

wss.on('connection', function(ws) {
    // Quando o cliente conecta, sobe o servidor HTTP para receber o onibus selecionado
    var http = require('http');

    http.createServer(function (req, res) {
        req.on('data', function (chunk) {
            onibusSelecionadoAgora = chunk.toString();
            console.log(onibusSelecionadoAgora);

            var net = require('net');
            // Conecta ao servidor Java
            var client = net.connect(8888, 'localhost');
            // Envia para o server java o onibus selecionado, para ativar o Esper
            client.write(onibusSelecionadoAgora + '\n');
            client.on('data', (data) => {
                // const server_message = JSON.parse(data.toString);
                // const tipo = server_message.tipo;
                //
                // if (tipo = "OnibusEvent") {
                //     const latitude = server_message.latitude;
                //     const longitude = server_message.longitude;
                // }
                var dados = data.toString();
                console.log(dados);
                var latLong = dados.split(" ");
                var latitude = latLong[0];
                var longitude = latLong[1];
                console.log(latitude + "," + longitude);
                // ws.send(JSON.stringify({ "num": onibusSelecionadoAgora }));
                ws.send(JSON.stringify({ "latitude": latitude, "longitude": longitude }));
                // client.end();
            });
        });
    }).listen(6969);
});
