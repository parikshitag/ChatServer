package chat;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerConnection implements Runnable {
  private static final int port = 6564;
  private static final String CRLF = "\r\n";
  private BufferedReader in;
  private PrintWriter out;
  private String id, toid = null;
  private MDIParent mdi;
  public ServerConnection(MDIParent sc,String site) throws IOException {
      mdi=sc;

    Socket server = new Socket(site, port);
    in = new BufferedReader(new InputStreamReader(server.getInputStream()));
    out = new PrintWriter(server.getOutputStream(), true);
  }

  

  private String readline() {
    try {
      return in.readLine();
    } catch (IOException e) {
      return null;
    }
  }

      void setTo(String to) {
        toid = to;
      }


      void send(String s) {
        if (toid != null)
          out.println("to " + toid + " " + s);
      }

    void chat(String s) {
        send("chat " + id + " " + s);
      }

      void setName(String s) {
        out.println("name " + s);
      }

      void chatTo(String destid) {
        setTo(destid);
      //  send("challenge " + id);
      }

      void quit() {
        send("quit " + id);  // tell other player
        out.println("quit"); // unhook
       }

  // reading from server...

  private Thread t;
  void start() {
    t = new Thread(this);
    t.start();
  }



   private static final int ID = 1;
  private static final int ADD = 2;
  private static final int CHAT = 3;
  private static final int DELETE = 4;
 // private static final int CHALLENGE = 4;
  private static Hashtable keys = new Hashtable();
  private static String keystrings[] = {
    "", "id", "add", "chat", "delete" //"challenge"
  };
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
      case ID:
        id = st.nextToken();
        break;
      case ADD: {
          String id = st.nextToken();
          String hostname = st.nextToken();
          String name = st.nextToken(CRLF);
          mdi.add(id, hostname, name);
        }
        break;
      case CHAT: {
          String from = st.nextToken();
          mdi.chatFrom(from);
          String body=st.nextToken(CRLF);
          mdi.chat(from,body );
        }
        break;
        case DELETE:
            mdi.delete(st.nextToken());
        break;
//      case CHALLENGE: {
//          String from = st.nextToken();
//          chat.chatFrom(from);
//        }
//        break;
      }
    }
  }

}
