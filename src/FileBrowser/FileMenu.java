package FileBrowser;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Konstantinos
 */
public class FileMenu extends JMenu implements ActionListener {
    static int windowsOpen = 0;
    JFrame currFrame;

    public FileMenu(String name, JFrame frame) {
        super(name);
        currFrame = frame;
        init();
    }

    private void init() {
        JMenuItem newWindow = new JMenuItem("New Window");
        newWindow.addActionListener(this);
        this.add(newWindow);

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(this);
        this.add(exit);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String fileMenuString = e.getActionCommand();

        if (fileMenuString.equals("New Window")) {
            windowsOpen++;
            MainFrame mFr = new MainFrame("FileBrowser(" + windowsOpen + ")", windowsOpen);
            mFr.setVisible(true);
        }
        if (fileMenuString.equals("Exit")) {
            currFrame.dispose();
        }
    }
}
