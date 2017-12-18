/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author sergioandres
 */
public class ComandProcessor {
    private List<SocketController> clients = new LinkedList<>();
    private int secuencia = 1;

    
    
    
//    public String responsecommand(String aCommand, SocketController sender){
//        aCommand = aCommand.trim().toUpperCase();
//        String response = "";
//        SimpleDateFormat sdf = null;       
//        if(aCommand.startsWith("SEND TO")){
//            String[] aux = aCommand.split(" ");
//            String text = aCommand.substring(("SEND TO "+aux[2]).length());
//            if(writeText(text, aux[2],sender.getName()))
//                response="100";
//            else
//                response = "200";
//        }
//        else if(aCommand.equals("USERS")){
//            sender.writeText(this.getUsuarios(sender.getName()));
//            response = "100";
//        }
//        else if(aCommand.startsWith("SETNICKNAME ")){
//            sender.setName(aCommand.substring(12));
//            sender.writeText(this.getUsuarios(sender.getName()));
//            response = "100";
//        }
//        
//        return response;
//    }
    
    public int getNumOfUsers(){
        return this.clients.size();
    }
    
    public String responsecommand(String aCommand, SocketController sender){
        aCommand = aCommand.trim().toUpperCase();
        String response = "200";
        SimpleDateFormat sdf = null;       
        if(aCommand.startsWith("SENDALL ") && sender.registered()){
            if(writeText(aCommand.substring(8),sender))
                response = "100";
            else
                response = "200";
        }
        else if(aCommand.equals("NUMOFUSERS") && sender.registered()){
            response = "100 "+this.getNumOfUsers();
        }
        else if(aCommand.startsWith("SEND ") && sender.registered()){
            String[] aux = aCommand.split(" ");
            String text = aCommand.substring(("SEND "+aux[1]).length());
            if(writeText(text, aux[1],sender))
                response="100";
            else
                response = "200";
        }
        else if(aCommand.equals("GETUSERS")){
            
        }else if(aCommand.startsWith("REMOVE ")){
            String idmsj = aCommand.substring(7);
            String namclient = idmsj.split(" ")[1];
            SocketController client = this.getClient(namclient);
            client.writeText(aCommand+" "+sender.getName());
            sender.writeText(aCommand+" "+client.getName());
            response = "100";
        }
        else if(aCommand.equals("GETID") && sender.registered()){
            response = "ID:"+sender.getId();
        }
        else if(aCommand.startsWith("REGISTER ")){
            String username = aCommand.substring(9);
            if(this.validarNombre(username))
            {
                sender.setName(username);
                sender.writeText(this.getUsuarios(sender.getName()));
                response = "100";
                sender.setId(secuencia);
                secuencia++;
            }
            else 
            {
                sender.setIntentos(sender.getIntentos() + 1);
                if(sender.getIntentos() == 3)
                {
                    sender.setQuit(true);
                    this.clients.remove(sender);
                }
                else
                    sender.writeText("Intentos restantes: " + (3 - sender.getIntentos()));
                response = "200";
            }
            
            
        }
        
        return response;
    }
    
    public boolean validarNombre(String nombre){
        return Pattern.matches("[A-Za-z][A-Za-z0-9]+", nombre) && !this.usedName(nombre);
    }
    
    public boolean usedName(String username){
        boolean used = false;
        Iterator<SocketController> it = this.clients.iterator();
        while(it.hasNext() && !used){
            SocketController client = it.next();
            if(username.equals(client.getName()))
                used = true;
        }
        return used;
    }
    
    public String getUsuarios(String sender){
        String usuarios="USERS:";
        for(SocketController client: this.clients){
            if(!sender.equals(client.getName()))
                usuarios+=client.getName()+",";
        }
        return usuarios.substring(0, usuarios.length()-1);
    }

    void add(SocketController socketController) {
        clients.add(socketController);
    }
    
    void delete(SocketController client){
        this.clients.remove(client);
    }
    
    public void writeText(String text){
        for (SocketController client : clients) {
            client.writeText(text);
        }
    }
    
    public boolean writeText(String text, SocketController sender){
        String iden = sender.getId()+System.currentTimeMillis();
        //sender.writeText("GRUPAL "+iden+":"+sender.getName()+":"+text);
        if(clients.size()>1){
            for (SocketController client : clients) {
                client.writeText("GRUPAL "+iden+":"+sender.getName()+":"+text);
            }
            return true;
        }
        return false;
    }
    
    public boolean writeText(String text, String adresse, SocketController sender){
        boolean enviado = false;
        Iterator<SocketController> i = this.clients.iterator();
        String iden = sender.getId()+System.currentTimeMillis();
        sender.writeText(iden+":"+sender.getName()+":"+text);
        while(!enviado && i.hasNext()){
            SocketController client = i.next();
            if(client.getName().equals(adresse)){
                client.writeText(iden+":"+sender.getName()+":"+text);
                enviado = true;
            }
        }
        return enviado;
    }
    
    
    public SocketController getClient(String name){
        SocketController client = null;
        Iterator<SocketController> it = this.clients.iterator();
        boolean encontrado = false;
        while(it.hasNext() && !encontrado){
            SocketController cl = it.next();
            if(cl.getName().equals(name))
            {
                client = cl;
                encontrado = true;
            }
        }
        return client;
    }
}
