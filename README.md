# Nutana

Nutana is an experiment to determine what it would look like if we took
all the goodness of node.js: the non-blocking asynchronous program model,
and the packaging system, and do it in Java 7 using asynchronous sockets from NIO.2
and use Equinox with p2 as the packaging system.

It's not clear how much time I'll have to spend on it in the future as I focus
on a new job. I really liked how this turned out and if you want to take it
from here, I'd be happy to watch or even advise a little. It's a cool idea and
could modernize Java in the server space and totally get away from J2EE madness.

## Example

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

## TODO list

Here are ideas I have on modules that could be developed.

* wapp - web apps similar to expressjs.
* file - async access (nio.2) to files that are delivered in the bundles
* websocket - upgrade support to WebSocket
* mongodb - async communications with MongoDB
* cassandra - async communications using Thrift to Cassandra
* freemarker - FreeMarker template support for wapp.
* mqtt - An MQTT broker
* p2 - wapp interface to install/uninstall bundles

Other general areas to consider:

* Other template engines
* Other database connections
