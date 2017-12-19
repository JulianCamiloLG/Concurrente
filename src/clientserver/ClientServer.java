/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientserver;

import Vistas.MainFrame;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author Camilo
 */
public class ClientServer {

    /**hole
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try{
           final Agent agent = new Agent(11000);
           java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new MainFrame(agent).setVisible(true);
                }
            });

            if(agent != null){
                agent.start();
//                Scanner sc = new Scanner(System.in);
//                while(!agent.isQuit()){
//                    String command = sc.nextLine();
//                    agent.Command(command);
//                }
            }
        }catch(IOException ex){
            System.out.println(ex);
        }
        
    }
    
    
}
