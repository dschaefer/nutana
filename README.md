# Nutana

Take all the goodness of node.js: the non-blocking asynchronous program model,
and the packaging system, and do it in Java 7 using asynchronos sockets from NIO.2
and use Equinox with p2 as the packaging system, shake and stir and see if
it works and whether a community forms from it, which, BTW,
is actually the best thing about node.js.

Here is an example of what we're trying to achieve. This is from the first test
server in the http.test plugin. Looks like node, but in Java.

    ServiceReference<Http> ref = Activator.getContext().getServiceReference(Http.class);
    Http httpService = Activator.getContext().getService(ref);
		
    HttpServer server = httpService.createServer();
    server.onRequest(new HttpServer.RequestListener() {
        @Override
        public void handleRequest(HttpServerRequest request, HttpServerResponse response) {
            if (request.getURL().equals("/")) {
                response.setHeader("Content-Type", "text/plain");
                response.writeHead(200);
                response.write("Hello from Nutana! " + (++count));
                response.end();
            } else if (request.getURL().equals("/end")) {
                response.setHeader("Content-Type", "text/plain");
                response.writeHead(200);
                response.write("Bye!");
                response.end();
                stop();
            } else {
                response.writeHead(404);
                response.end();
            }
        }
    });
    server.listen(new InetSocketAddress(8001));
