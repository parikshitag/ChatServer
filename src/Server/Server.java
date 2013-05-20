/*this thread server is responsible for adding the new user in the list */


package Server;
import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable{
    private int port=6564;
    private Hashtable idcon=new Hashtable();
    private int id;
    static final String CRLF = "\r\n";

    void addConnection(Socket s){
        ClientConnection con = new ClientConnection(this, s, id);
    // we will wait for the ClientConnection to do a clean
    // handshake setting up its "name" before calling
    // set() below, which makes this connection "live."
    id++;
    }
    synchronized void kill(ClientConnection c) {
    if (idcon.remove(c.getId()) == c) {
      delete(c.getId());
        }
    }

    synchronized void delete(String the_id) {
     broadcast(the_id, "delete " + the_id);

    }
    
    synchronized void sendto(String dest, String body) {
    ClientConnection con = (ClientConnection)idcon.get(dest);
    if (con != null) {
      con.write(body + CRLF);
    }
  }

    //tell the remaining ones that this person is no longer exists
    synchronized void broadcast(String exclude, String body) {
        Enumeration e = idcon.keys();
        while (e.hasMoreElements()) {
          String id = (String)e.nextElement();
          if (!exclude.equals(id)) {
            ClientConnection con = (ClientConnection) idcon.get(id);
            con.write(body + CRLF);
          }
        }
  }


    synchronized void set(String the_id, ClientConnection con) {
        idcon.remove(the_id) ;  // make sure we're not in there twice.
         // tell this one about the other clients.
        Enumeration e = idcon.keys();
        while (e.hasMoreElements()) {
          String id = (String)e.nextElement();
          ClientConnection other = (ClientConnection) idcon.get(id);
            con.write("add " + other + CRLF);
        }
        idcon.put(the_id, con);
        broadcast(the_id, "add " + con);
  }


    public void run(){
        try{
            ServerSocket acceptsocket=new ServerSocket(port);
            System.out.println("Server listening on port " + port);
            while(true){
                Socket s=acceptsocket.accept();
                addConnection(s);
            }
      }catch(IOException e){
          System.out.println("accept loop IOException: " + e);
      }
    }

    public static void main(String[] args){
        new Thread(new Server()).start();
        try{
            Thread.currentThread().join();
        }catch(InterruptedException e){}
    }
    
    
}
