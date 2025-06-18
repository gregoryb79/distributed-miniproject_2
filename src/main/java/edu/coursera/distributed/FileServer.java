package edu.coursera.distributed;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
        while (true) {

            // TODO Delete this once you start working on your solution.
           

            // TODO 1) Use socket.accept to get a Socket object
            Socket s = socket.accept();
            /*
             * TODO 2) Using Socket.getInputStream(), parse the received HTTP
             * packet. In particular, we are interested in confirming this
             * message is a GET and parsing out the path to the file we are
             * GETing. Recall that for GET HTTP packets, the first line of the
             * received packet will look something like:
             *
             *     GET /path/to/file HTTP/1.1
             */
            try{
                InputStream inputStream = s.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(reader);

                String requestLine = bufferedReader.readLine();
                OutputStream outputStream = s.getOutputStream();
                PrintWriter printer = new PrintWriter(outputStream, true);
                assert requestLine != null;
                assert requestLine.startsWith("GET ");
                if (!requestLine.startsWith("GET ")) {                
                    System.out.println("Invalid request: " + requestLine);  
                    printer.write(
                        "HTTP/1.0 400 Bad Request\r\n" +
                        "Server: FileServer\r\n" +
                        "\r\n"+
                        "\r\n"
                    );   
                    printer.flush();                    
                }

                final String path = requestLine.split(" ")[1];
                System.out.println("Received request for: " + path);
                
                PCDPPath pcdpPath = new PCDPPath(path);
                System.out.println("Parsed PCDPPath: " + pcdpPath);

                final String file = fs.readFile(pcdpPath);

                
                if (file == null) {
                    System.out.println("File not found: " + pcdpPath);
                    // Send 404 Not Found response
                    printer.write(
                        "HTTP/1.0 404 Not Found\r\n" +
                        "Server: FileServer\r\n" +
                        "\r\n"+
                        "\r\n"
                    );
                    printer.flush();
                } else {
                    System.out.println("File found: " + pcdpPath);
                    // System.out.println("Sending: ");
                    // System.out.println("HTTP/1.0 200 OK\r\n" +
                    //     "Server: FileServer\r\n" +                     
                    //     "\r\n" +
                    //     file + "\r\n");
                    // Send 200 OK response with file contents
                    printer.write(
                        "HTTP/1.0 200 OK\r\n" +
                        "Server: FileServer\r\n" +                     
                        "\r\n" +
                        file + "\r\n");
                    printer.flush();
                }
            } catch (IOException e) {
                        System.err.println("Error handling request: " + e.getMessage());
                        throw new RuntimeException("Error handling request", e);
                } finally {
                    s.close();            
                    }            

            /*
             * TODO 3) Using the parsed path to the target file, construct an
             * HTTP reply and write it to Socket.getOutputStream(). If the file
             * exists, the HTTP reply should be formatted as follows:
             *
             *   HTTP/1.0 200 OK\r\n
             *   Server: FileServer\r\n
             *   \r\n
             *   FILE CONTENTS HERE\r\n
             *
             * If the specified file does not exist, you should return a reply
             * with an error code 404 Not Found. This reply should be formatted
             * as:
             *
             *   HTTP/1.0 404 Not Found\r\n
             *   Server: FileServer\r\n
             *   \r\n
             *
             * Don't forget to close the output stream.
             */
        }
    }
}
