/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clipboarder;

import java.awt.SystemTray;

/**
 *
 * @author Philipp
 */
public class Clipboarder {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello World!");
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
    }

}
