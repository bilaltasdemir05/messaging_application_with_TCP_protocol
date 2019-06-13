package bilgisayar_aglari_3;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;


public class sunucu_tarafi {
	// Server soket
    private static ServerSocket serverSocket = null;
    // Client soket
    private static Socket clientSocket = null;
    // Maximum baðlantý sayýsý
    private static final int maxClientSayisi = 10;
    // Her bir client için oluþturlacak Thread dizisi
    private static final ClientThread[] threads = new ClientThread[maxClientSayisi];
	public static void main(String[] args) {
		int portNo = 3333;
        try {
            serverSocket = new ServerSocket(portNo);
        } catch (IOException e) {
            System.out.println(e);
        }
        /*
         * Her bir client için ayrý soketler ve threadlerin oluþturulmasý
         */
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                int i = 0;
                for (i = 0; i < maxClientSayisi; i++) {
                    if (threads[i] == null) {
                        (threads[i] = new ClientThread(clientSocket, threads)).start();
                       
                        break;
                    }
                }
                if (i == maxClientSayisi) {
                    PrintStream ps = new PrintStream(clientSocket.getOutputStream());
                    ps.println("Kullanici Sýnýrý Aþýldý");
                    ps.close();
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
class ClientThread extends Thread {

    private DataInputStream dis = null;
    private PrintStream ps = null;
    private Socket clientSocket = null;
    private final ClientThread[] threads;
    private int maxClientSayisi;

    public ClientThread(Socket clientSocket, ClientThread[] threads) {
        this.clientSocket = clientSocket;
        this.threads = threads;
        maxClientSayisi = threads.length;
    }
    @Override
    public void run() {
        int maxClientSayisi = this.maxClientSayisi;
        ClientThread[] threads = this.threads;

        try {
            dis = new DataInputStream(clientSocket.getInputStream());
            ps = new PrintStream(clientSocket.getOutputStream());
            
           
            ps.println("Nickname: ");
            String name = dis.readLine().trim();
            ps.println("Merhaba " + name + "! Burasi Local Server.");
            for (int i = 0; i < maxClientSayisi; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].ps.println(name + "odaya baglandi.");
                }
            }
            while (true) {
                String satir = dis.readLine();
                int id = 0;
                if (satir.startsWith("/quit")) {
                    break;
                }
                for (int i = 0; i < maxClientSayisi; i++) {
                    if (threads[i] != null) {
                        threads[i].ps.println("<" + name + ">: " + satir);

                        String url = "jdbc:mysql://localhost:3306/mesajkayýt?useSSL=false";
                        String user = "root";
                        String password = "1234";
                        
                        try {
                        	Connection con = DriverManager.getConnection(url, user, password);
                        	Statement st = con.createStatement();
                        	ResultSet rs=st.executeQuery("select * from mesajlar");
                        	while(rs.next())  {
                        		id=rs.getInt(1);
                        		
                        	}
                        	id=id+1;
                        	String sorgu=String.format("insert into mesajlar values( %d, '%s','%s')", id,name,satir);
                        	st.executeUpdate(sorgu);
                        }
                        catch (SQLException ex) {
                            System.out.print(ex);
                        
                	}
                        
                        
						
                    }
                }
            }
            for (int i = 0; i < maxClientSayisi; i++) {
                if (threads[i] != null && threads[i] != this) {
                    threads[i].ps.println(name + " adlý kisi odadan ayrildi.");
                }
            }
            ps.println(name + " Gule Gule!");

            /*
             * Yeni bir Clientýn baðlanabilmesi için aktif olan Client null yapýlýr
             */
            for (int i = 0; i < maxClientSayisi; i++) {
                if (threads[i] == this) {
                    threads[i] = null;
                }
            }
            dis.close();
            ps.close();
            clientSocket.close();
        } catch (IOException e) {
        }
    }
}
