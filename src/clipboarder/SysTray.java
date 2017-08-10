/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clipboarder;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

/**
 *
 * @author Philipp
 */
public class SysTray {

    public void createTrayIcon() throws AWTException {
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported, exiting!");
            return;
        }

        PopupMenu pMenu = new PopupMenu();

        List<String> menuItems = new ArrayList<String>();

        menuItems.add("Einstellungen");
        menuItems.add("Beenden");

        for (String s : menuItems) {
            MenuItem item = new MenuItem(s);
            pMenu.add(item);
        }

        Image image = Toolkit.getDefaultToolkit().getImage(
                SysTray.class.getResource("assets/images/logo.gif"));
        TrayIcon trayIcon = new TrayIcon(image, "Java-Tray ", pMenu);
        trayIcon.setImageAutoSize(true);
        SystemTray tray = SystemTray.getSystemTray();
        tray.add(trayIcon);
    }

}
