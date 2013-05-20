package chat;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class MDIParent extends JFrame{
   // private Chat chat;
    private JDesktopPane dp = new JDesktopPane();
    private DefaultListModel listModel = new DefaultListModel();
    private JList list = new JList(listModel);

     ServerConnection server;
    public static String SERVERNAME;
             private String name;
            private static int left;
            private static int top;
            private String others_name;

       private static void incr() {
        left += 30;
        top += 30;
        if (top == 150) {
            top = 0;
        }
       }

     class Chat extends JInternalFrame implements ActionListener{
            JTextArea txtRecieve;
            JTextField txtSend;
            JInternalFrame chat;



        void setComponents(){
               JPanel mainPanel= new JPanel();

               txtRecieve = new JTextArea();
                txtRecieve.setMargin(new Insets(5, 5, 5, 5));
               txtRecieve.setWrapStyleWord(true);
               txtRecieve.setEditable(false);

               JScrollPane qScroller= new JScrollPane(txtRecieve);
               qScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
               qScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

               txtSend = new JTextField(50);

               mainPanel.add(qScroller);
               mainPanel.add(txtSend);

               getContentPane().add(BorderLayout.CENTER, mainPanel);
                qScroller.setPreferredSize(new Dimension(550,260));
               setSize(600, 340);
               setResizable(false);
               setVisible(true);
                


        }
        



        



        
        Chat()
        {
            setClosable(true);
            setIconifiable(true);
            setComponents();
            txtSend.addActionListener(this);
            txtRecieve.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent me)
                {
                    txtSend.requestFocusInWindow();
                }});
            addInternalFrameListener(new internalFramelist());
            setLocation(left, top);
            incr();


        
    }
        class internalFramelist extends InternalFrameAdapter
        {

                public void internalFrameClosing(InternalFrameEvent event) {
                    //listModel.removeElement(this);
                    setVisible(false);
                }

                public void internalFrameOpened(InternalFrameEvent event) {
                    int index = listModel.indexOf(Chat.this);
                    list.getSelectionModel().setSelectionInterval(index, index);
                    txtSend.requestFocusInWindow();
                }

                public void internalFrameActivated(InternalFrameEvent event) {
                    int index = listModel.indexOf(Chat.this);
                    list.getSelectionModel().setSelectionInterval(index, index);
                    txtSend.requestFocusInWindow();
                }
        }

        public String toString() {
            return getTitle();
        }
        public void actionPerformed(ActionEvent ae) {
            Object source = ae.getSource();
            if(source == txtSend) {
                            String s=getTitle();
                            if (s != null) {
                              String destid = s.substring(s.indexOf('(')+1, s.indexOf(')'));
                              server.chatTo(destid);  // accept will get called if
                                                         // they accept.
                             validate();
                            }
                          txtRecieve.append("Me: "  + txtSend.getText() + "\n");
              server.chat(txtSend.getText());
              txtSend.setText("");
            }

          }
        public void select() {
            try {
                toFront();
                setSelected(true);
            } catch (java.beans.PropertyVetoException e) {}
        }

    }
        

    public MDIParent() {
        super("Chat");
        state();
        //setResizable(false);
        getContentPane().add(dp);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        setVisible(true);


        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, list, dp);
        sp.setDividerLocation(120);
        getContentPane().add(sp);

        list.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


                list.addListSelectionListener(new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        Chat val = (Chat)list.getSelectedValue();
                        if (val != null) {
                            val.select();
                            val.txtSend.requestFocusInWindow();
                        }

                     }
                 });
        list.addMouseListener(new MouseAdapter() {
             public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        Chat val = (Chat)list.getSelectedValue();
                            val.setVisible(true);
                        if(val.isClosed())
                            dp.add(val);
                            val.select();
                     }
            }
        });

        SERVERNAME=JOptionPane.showInputDialog(this,"Specify Server Address or Name","127.0.0.1");


                try{
                  JOptionPane.showMessageDialog(this,"Connecting to " + SERVERNAME,"Connection",JOptionPane.INFORMATION_MESSAGE);
                  server = new ServerConnection(this,SERVERNAME);
                  server.start();

                  JOptionPane.showMessageDialog(this,"Connected: " + SERVERNAME,"Connection",JOptionPane.INFORMATION_MESSAGE);
                }catch(Exception e){JOptionPane.showMessageDialog(this,"Server down or currently not running", "Server Error",JOptionPane.ERROR_MESSAGE);System.exit(1);}
                String s=JOptionPane.showInputDialog(this, "Enter your name", "Name", JOptionPane.INFORMATION_MESSAGE);
                if(s==null || s.equals(""))
                      System.exit(1);
                else
                    nameEntered(s);
       

        }

          void add(String id, String hostname, String name) {
            Chat chat=new Chat();
            dp.add(chat);
            chat.setTitle("(" + id + ")  " + name + "@" + hostname);
            listModel.add(listModel.size(), chat);
            chat.select();
          }

          void chat(String id, String s) {
           for (int i = 0; i < listModel.getSize(); i++) {
            String str = ((Chat)listModel.getElementAt(i)).getTitle();
                  String id1 = str.substring(str.indexOf("(") + 1, str.indexOf(")"));
                  if (id1.equals(id)) {
                      Chat val = (Chat)listModel.getElementAt(i);
                      val.select();
                          val.txtRecieve.append(others_name + ": " + s + "\n");

                   }
                }

          }

                  //Someone wrote a text to you
        void chatFrom(String id){

            others_name = getName(id);   // who was it?

        }

            void delete(String id) {
                
                for (int i = 0; i < listModel.getSize(); i++) {
                  String s = ((Chat)listModel.getElementAt(i)).getTitle();
                  String id1 = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
                  if (id1.equals(id)) {
                      ((Chat)listModel.getElementAt(i)).dispose();
                        listModel.remove(i);
                    break;
                  }
                }
            }

        private String getName(String id) {
                for (int i = 0; i < list.getModel().getSize(); i++) {
                  String s = ((Chat)list.getModel().getElementAt(i)).getTitle();
                  String id1 = s.substring(s.indexOf("(") + 1, s.indexOf(")"));
                  if (id1.equals(id)) {
                    return s.substring(s.indexOf(" ") + 3, s.indexOf("@"));
                  }
                }
                return null;
        }


        private void nameEntered(String s) {
                if (s.equals(""))
                  return;
                name = s;
                validate();
                this.setTitle("Chat: " + name);
                server.setName(name);
            }

    void state()
    {


       this.addWindowListener(new WindowAdapter() {

            public void windowOpened(WindowEvent ee) {
                setExtendedState(JFrame.MAXIMIZED_BOTH);
            }

            public void windowClosing(WindowEvent we)
            {
                server.quit();
            }
        });


                Toolkit t = Toolkit.getDefaultToolkit();
                setLocation(0,0);
                setSize(t.getScreenSize());

    }







    void start(){

    }

}
