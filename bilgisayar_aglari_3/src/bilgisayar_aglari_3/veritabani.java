package bilgisayar_aglari_3;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class veritabani {

    public static void main(String[] args) {
    	Scanner input = new Scanner(System.in);
    	String anahtar_kelime="";
    	System.out.println("Arama Yapýlacak Anahtar Kelimeyi Giriniz:");
    	anahtar_kelime=input.nextLine();
        String url = "jdbc:mysql://localhost:3306/mesajkayýt?useSSL=false";
        String user = "root";
        String password = "1234";
        String msg="";
        String aranan_msg="";
        int sayac=0;
        try {
        	Connection con = DriverManager.getConnection(url, user, password);
        	Statement st = con.createStatement();
        	System.out.println("BAÐLANTI BAÞARILI");
        	ResultSet rs=st.executeQuery("select * from mesajlar");
        	while(rs.next())  {
        	msg=msg+rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3)+"";
        	if(msg.contains(anahtar_kelime)) {
        		sayac=1;
        		aranan_msg+=msg;
        	}
        	System.out.println(msg);
        	msg="";
        	}
        	if(sayac==1) {
        		System.out.println("Aranan Kelime Bulundu:");
        		System.out.println(aranan_msg);
        	}
        	else {
        		System.out.println("Aranan Kelime Bulunamadý:");
        	}
        	
        	
        }
        catch (SQLException ex) {
            System.out.print(ex);
        } 
       
    }
}