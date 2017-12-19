/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import Vistas.MainFrame;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sergioandres
 */
public class SocketController implements Runnable{
    private Thread theThread = null;
    private Socket theSocket = null;
    private PrintWriter theOut = null;
    private BufferedReader theIn = null;
    private ComandProcessor theComandProcessor = null;
    private String name;
    private int intentos;
    private boolean quit = false;
    private String id;
    private Agent agent;
    private boolean listaenviada = false;
    
    
    public SocketController(Socket newSocket, Agent agent){
        this.intentos = 0;
        theSocket = newSocket;
        this.name = theSocket.getRemoteSocketAddress().toString();
        theComandProcessor = new ComandProcessor();
        this.agent = agent;
        try {
            theOut = new PrintWriter(getTheSocket().getOutputStream(),true);
            theIn = new BufferedReader(new InputStreamReader(getTheSocket().getInputStream(),"UTF-8"));
        } catch (Exception e) {
            Logger.getLogger(SocketController.class.getName()).log(Level.SEVERE,null,e);
        }
        theThread = new Thread(this);
        theThread.start();
    }
    
    
    
    public void close(){
        try {
            getTheOut().close();
            getTheIn().close();
            getTheSocket().close();
        } catch (Exception e) {
        }
    }
    
    public void writeText(String text){
        getTheOut().println(text);
    }
    
    public String readText(){
        String text = null;
        try {
            text = getTheIn().readLine();
        } catch (Exception e) {
            Logger.getLogger(SocketController.class.getName()).log(Level.SEVERE,null,e);
        }
        return text;
    }

    @Override
    public void run() {
        String message = null;
        
        writeText("100 broadcastserver");
        MainFrame.textArea.setText(MainFrame.textArea.getText()+"100 broadcastserver");
        while(!isQuit()){
            message = readText();
            if(message.startsWith("ips:")){
                agent.Command(message);
            }
            System.out.println(message);
            
            //this.listamensaje.add(message);
        }
        close();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isAlive(){
        return this.getTheSocket().isConnected();
    }

    public int getIntentos() {
        return intentos;
    }

    public void setIntentos(int intentos) {
        this.intentos = intentos;
    }

    public boolean isQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }
 
    public boolean registered(){
        return this.getName() != null;
    }

    public void setId(int id) {
        this.setId("");
        int n = 2 - (int) Math.log10(id);
        for(int i = 0; i < n; i++)
            this.setId("0" + this.getId());
        this.setId(this.getId() + id);
    }

    public String getId() {
        return id;
    }

    
    @Override
    public String toString() {
        return this.getName(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the theThread
     */
    public Thread getTheThread() {
        return theThread;
    }

    /**
     * @param theThread the theThread to set
     */
    public void setTheThread(Thread theThread) {
        this.theThread = theThread;
    }

    /**
     * @return the theSocket
     */
    public Socket getTheSocket() {
        return theSocket;
    }

    /**
     * @param theSocket the theSocket to set
     */
    public void setTheSocket(Socket theSocket) {
        this.theSocket = theSocket;
    }

    /**
     * @return the theOut
     */
    public PrintWriter getTheOut() {
        return theOut;
    }

    /**
     * @param theOut the theOut to set
     */
    public void setTheOut(PrintWriter theOut) {
        this.theOut = theOut;
    }

    /**
     * @return the theIn
     */
    public BufferedReader getTheIn() {
        return theIn;
    }

    /**
     * @param theIn the theIn to set
     */
    public void setTheIn(BufferedReader theIn) {
        this.theIn = theIn;
    }

    /**
     * @return the theComandProcessor
     */
    public ComandProcessor getTheComandProcessor() {
        return theComandProcessor;
    }

    /**
     * @param theComandProcessor the theComandProcessor to set
     */
    public void setTheComandProcessor(ComandProcessor theComandProcessor) {
        this.theComandProcessor = theComandProcessor;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    public boolean isListaenviada() {
        return listaenviada;
    }

    public void setListaenviada(boolean listaenviada) {
        this.listaenviada = listaenviada;
    }
    
    
}
