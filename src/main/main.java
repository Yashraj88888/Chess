package main;

import javax.swing.JFrame;
import java.awt.*;



public class main {

    public static void main (String[] args) {
        
        JFrame window = new JFrame("Chess Game");

        //creating a window:


        //to stop program after closing the window 

        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //so that we cannot resize window
        window.setResizable(false);
        //window shows up at center of monitor

        // Adding GamePanel to the window
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        //adds gamepanel to the window
        //packing window will adjust its size to the gamepanel

        window.setLocationRelativeTo(null);
        //to actually see the window we use 
        window.setVisible(true);

        gp.launchGame();

        
    }

} 
