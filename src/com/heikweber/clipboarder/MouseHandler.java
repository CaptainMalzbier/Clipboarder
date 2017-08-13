/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;




/**
 *
 * @author Philipp
 */
public class MouseHandler extends MouseAdapter {

    private Timer oneClickTimer;

    public MouseHandler() {
        System.out.println("mouseHandler");
        oneClickTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                doOneClick();
            }
        });
        oneClickTimer.setRepeats(false);
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            oneClickTimer.stop();
            doTwoClicks();
        } else if (e.getClickCount() == 1) {
            oneClickTimer.restart();
        }
    }

    protected void doOneClick() {
        Clipboarder cB = new Clipboarder();
        cB.showStage();
    }

    protected void doTwoClicks() {
        System.out.println("2 clicks");
    }

}
