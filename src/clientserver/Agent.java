/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import Vistas.MainFrame;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Camilo
 */
public class Agent implements Runnable{
    private Thread theThread = null;
    private ServerSocket theSeverServerSocket = null;
    private LinkedList<SocketController> theClients = new LinkedList<>();
    private boolean quit = false;

    public Agent(int newPort) throws IOException{
        this.theSeverServerSocket = new ServerSocket(newPort);
        this.theThread = new Thread(this);
    }
    
    public void start(){
        this.theThread.start();
    }
    
    public void stop() {
        quit = false;
        try {
            theSeverServerSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
        theThread.stop();
    }
    
    public String getDirecciones(){
        String direcciones = "";
        for (SocketController theClient : theClients) {
        direcciones+=theClient.getName();
        if(theClient != theClients.getLast())
            direcciones+=",";
        }
        return direcciones;
    }
    
    public boolean hayConexion(String ip){
        Iterator<SocketController> it = theClients.iterator();
        boolean igual = false;
        while (it.hasNext() && !igual) {
            SocketController next = it.next();
            igual = next.getName().equals(ip);
        }
        
        return igual;
    }
    
    public boolean connect(String aHostname) {
        if(this.hayConexion(aHostname))
            return false;
        
        try {
            SocketController sc = new SocketController(new Socket(aHostname, 11000),this);
            this.theClients.add(sc);
            sc.setName(aHostname);
            
        } catch (IOException ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    
    @Override
    public void run() {
        Socket aSocket = null;
        while(!quit){
            try {
                synchronized(theClients){                   
                    aSocket = this.theSeverServerSocket.accept();
                    this.theClients.add(new SocketController(aSocket,this));
                    String ip = aSocket.getRemoteSocketAddress().toString().substring(1).split(":")[0];
                    this.theClients.getLast().setName(ip);
                    String direcciones = this.getDirecciones();
                    this.theClients.getLast().writeText("ips:"+direcciones);
                }
            } catch (IOException ex) {
                quit = true;
            }
        }
    }
    
    public void Command(String command){
//        synchronized(theClients){
            command = command.toUpperCase();
            MainFrame.textArea.setText(MainFrame.textArea.getText()+"\n"+command);
            if(command.equals("QUIT")){
                for (SocketController theClient : theClients) {
                    MainFrame.textArea.setText(MainFrame.textArea.getText()+"\n"+theClient.getName()+":"+command);
                    theClient.setQuit(true);
                }
                quit = true;
            }
            else if(command.startsWith("CONNECT:")){
                MainFrame.textArea.setText(MainFrame.textArea.getText()+"\n"+command);
                String ip = command.substring(8);
                this.connect(ip);
            }else if(command.startsWith("IPS:")){
                String ips = command.substring(4);
                MainFrame.textArea.setText(MainFrame.textArea.getText()+"\n"+command);
                String direcciones[] = ips.split(",");
                for (int i = 0; i < direcciones.length-1; i++) {
                    this.connect(direcciones[i]);
                }
                SocketController sender = this.buscar(direcciones[direcciones.length-1]);
                if(sender != null)
                if(!sender.isListaenviada())
                {
                    sender.setListaenviada(true);
                    sender.writeText("ips:"+this.getDirecciones()+","+sender.getTheSocket().getLocalSocketAddress().toString().split(":")[0]);
                    MainFrame.textArea.setText(MainFrame.textArea.getText()+"\n"+"ips:"+this.getDirecciones()+","+sender.getTheSocket().getLocalSocketAddress().toString().split(":")[0]);
                }
            }else if(command.startsWith("SENDALL ")){
                for (SocketController theClient : theClients) {
                    MainFrame.textArea.setText(MainFrame.textArea.getText()+"\n"+theClient.getName()+":"+command);
                    theClient.writeText(command.substring(8));
                }
            }else if(command.startsWith("GETIPS")){
                System.out.println(this.getDirecciones());
                MainFrame.textArea.setText(MainFrame.textArea.getText()+"\n"+command);
            }
//        }
    }
    
    public SocketController buscar(String ip){
        Iterator<SocketController> it  = this.theClients.iterator();
        SocketController buscado = null;
        while(it.hasNext() && buscado == null){
            SocketController client = it.next();
            if(client.getName().equals(ip))
                buscado = client;
        }
        return buscado;
    }

    public boolean isQuit() {
        return quit;
    }
    
    
}
