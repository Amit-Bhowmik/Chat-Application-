import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private Socket socket;
    private BufferedReader br;
    private PrintWriter out;

    //Messenger Interface
    private JLabel heading = new JLabel("Client Area");
    private JTextArea msgArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Maiandra GD", Font.PLAIN, 20);
    private Font messageFont = new Font("Maiandra GD", Font.PLAIN, 16);



    //Login page
    private JTextField usernameField = new JTextField();
    private JTextField portField = new JTextField();
    private JButton connectButton = new JButton("Connect");



    public Client() {
        try {
            System.out.println("Sending Request to server");
            socket = new Socket("192.168.43.201", 5555);
            System.out.println("Connected to the server..");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            createGUI();
            startReading();
            handleEvents();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to server", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void handleEvents() {
        messageInput.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    String contentToSend = messageInput.getText();
                    msgArea.append("Me: " + contentToSend + "\n");
                    out.println(contentToSend);
                    out.flush();
                    messageInput.setText(" ");
                    messageInput.requestFocus();
                }
            }

        });
    }
    private void createGUI() {
        setTitle("Client Message[END]");
        setSize(400, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBackground(new Color(33, 33, 33));

        DefaultCaret caret = (DefaultCaret) messageInput.getCaret();
        caret.setBlinkRate(500);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        caret.setBlinkRate(500);

        messageInput.setCaretColor(Color.WHITE);

        try {
            UIManager.put("TextField.caretForeground", Color.WHITE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        heading.setFont(font);
        msgArea.setFont(messageFont);
        messageInput.setFont(messageFont);
        heading.setBackground(new Color(33, 33, 33));
        msgArea.setBackground(new Color(33, 33, 33));

        ImageIcon originalIcon = new ImageIcon("resources/cLogo.png");
        Image originalImage = originalIcon.getImage();
        Image resizedImage = originalImage.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);
        heading.setIcon(resizedIcon);

        //heading.setHorizontalTextPosition(SwingConstants.CENTER);
        //heading.setVerticalAlignment(SwingConstants.WEST);
        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(05, 10, 05, 10));

        msgArea.setEditable(false);
        msgArea.setForeground(Color.WHITE);
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        messageInput.setBackground(new Color(33,33,33));
        messageInput.setForeground(Color.WHITE);
        messageInput.setMargin(new Insets(10, 5, 10, 5));

        setLayout(new BorderLayout());
        add(heading, BorderLayout.NORTH);
        add(new JScrollPane(msgArea), BorderLayout.CENTER);
        add(messageInput, BorderLayout.SOUTH);

        setVisible(true);
    }

    public void startReading() {
        Runnable reader = () -> {
            try {
                while (true) {
                    String msg = br.readLine();
                    if (msg == null || msg.equals("exit")) {
                        handleExit();
                        break;
                    }
                    msgArea.append("Server: " + msg + "\n");
                }
            } catch (IOException e) {
                handleExit();
            }
        };

        new Thread(reader).start();
    }

    private void handleExit() {
        JOptionPane.showMessageDialog(this, "Server Left the chat", "Info", JOptionPane.INFORMATION_MESSAGE);
        messageInput.setEnabled(false);
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Client::new);
    }
}
