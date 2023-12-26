package com.example.pedidocompra;





import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private   String LOG ;
    private  String ip = "10.0.0.25";
    private  String port = "3306";
    private static String classs = "net.sourceforge.jtds.jdbc.Driver";
    private  String db = "FINANCEIRO";
    private  String un = "sa";
    private  String password = "S3rv3r";
    public static final int TIMEOUT_MS=2000;
    public  Connection connect() {
        Connection conn = null;
        String ConnURL = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + ip +":"+port+";"
                    + "databaseName=" + db + ";user=" + un + ";password="
                    + password + ";";
            conn = DriverManager.getConnection(ConnURL);
            LOG ="";
        } catch (SQLException e) {
            Log.d("DEBBUG", e.getMessage());
            LOG = e.toString();
        } catch (ClassNotFoundException e) {
            Log.d("DEBBUG", e.getMessage());
            LOG = e.toString();
        }
        return conn;
    }

    public  String getIp() {
        return ip;
    }


    public boolean serverAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        }

        // Attempt to bind to database port
        boolean available = false;
        SocketAddress sockAddr = new InetSocketAddress(ip,Integer.parseInt(port));
        Socket sock = new Socket();

        // On timeout, SocketTimeoutException is thrown.
        try {
            sock.connect(sockAddr, TIMEOUT_MS);
            available = true;
        } catch(Exception e){
            e.printStackTrace();
        }
        finally {
            try{
                sock.close();
            } catch(Exception e2) {}
        }

        return available;
    }

    public  void setIp(String ip) {
        this.ip = ip;
    }

    public  String getPort() {
        return port;
    }

    public  void setPort(String port) {
        this.port = port;
    }

    public  String getDb() {
        return db;
    }

    public  void setDb(String db) {
        this.db = db;
    }

    public  String getUn() {
        return un;
    }

    public  void setUn(String un) {
        this.un = un;
    }

    public  String getPassword() {
        return password;
    }

    public  void setPassword(String password) {
        this.password = password;
    }

    public String getLOG() {
        return LOG;
    }
}
