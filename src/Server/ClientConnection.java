package Server;
import java.net.*;
import java.io.*;
import java.util.*;

class ClientConnection implements Runnable{
  private Socket sock;
  private BufferedReader in;
  private OutputStream out;
  private String host;
  private Server server;
  private static final String CRLF = "\r\n";
  private String name = null;    // for humans
  private String id;

    public ClientConnection(Server srv,Socket s,int i) {
        try{
            server=srv;
            sock=s;
            in=new BufferedReader(new InputStreamReader(s.getInputStream()));
            out=s.getOutputStream();
            host=s.getInetAddress().getHostName();
            id = "" + i;

            // tell the new one his id...
            write("id " + id + CRLF);

            new Thread(this).start();
        }catch(IOException e){
            System.out.println("failed ClientConnection " + e);
        }
    }

   public void write(String s) {
    byte buf[];
    buf = s.getBytes();
    try {
      out.write(buf, 0, buf.length);
    } catch (IOException e) {
      close();
    }
  }

   public String getId() {
    return id;
  }
   
  public String toString() {
    return id + " " + host + " " + name;
  }
   //delete the connection and inform all users that this user quits
  public void close() {
    server.kill(this);
    try {
      sock.close();   // closes in and out too.
    } catch (IOException e) { }
  }

  private String readline() {
    try {
      return in.readLine();
    } catch (IOException e) {
      return null;
    }
  }


  static private final int NAME = 1;
  static private final int QUIT = 2;
  static private final int TO = 3;

  static private Hashtable keys = new Hashtable();
  static private String keystrings[] = {
    "", "name", "quit", "to"};
  static {
    for (int i = 0; i < keystrings.length; i++)
      keys.put(keystrings[i], new Integer(i));
  }

  private int lookup(String s) {
    Integer i = (Integer) keys.get(s);
    return i == null ? -1 : i.intValue();
  }
  public void run() {

    String s;
    StringTokenizer st;

    while ((s = readline()) != null) {
      st = new StringTokenizer(s);
      String keyword = st.nextToken();
      switch (lookup(keyword)) {
      default:
        System.out.println("bogus keyword: " + keyword + "\r");
        break;
      case NAME:
        name = st.nextToken() +
          (st.hasMoreTokens() ? " " + st.nextToken(CRLF) : "");
        System.out.println("[" + new Date() + "] " + this + " Added \r");
        server.set(id, this);
        break;
      case QUIT:
        close();
        System.out.println("[" + new Date() + "] " + this + " Exited \r");
        return;
      case TO:
        String dest = st.nextToken();
        String body = st.nextToken(CRLF);
        server.sendto(dest, body);
        break;
      }
    }
    close();
  }



}
