package FileBrowser;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Konstantinos
 */
// To panel pou periexei to breadCrumb,dhladh to absolute path tou current
// Directory
// me JButtons gia plohghsh sto fileSystem
public class BreadCrumbPanel extends JPanel implements ActionListener {
    MainFrame currFrame;// Metavlhth anaforas sto MainFrame gia prosvash se oles tis metavlhtes tou
                        // para8urou

    public BreadCrumbPanel(MainFrame currentFrame) {
        super();

        currFrame = currentFrame;

        init();
    }

    private void init() {
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        String[] prevDirsPaths = pathDivider(currFrame.path.replace("\\", "/"));
        createBreadCrumbicons(prevDirsPaths);
    }

    // Xrhsimopoiontas ta n-1 paths pou prokuptoun apo thn pathDivider ftiaxnoume
    // kai
    // pros8etoume ta Jbuttons tou breadcrumb(o fakelos pou eimaste den einai
    // Jbutton,mono Jlabel)
    private void createBreadCrumbicons(String[] paths) {

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBackground(new Color(255, 237, 169));

        // antistrofh tou pinaka paths gia swsth topo8ethsh twn JButtons
        String[] temp = paths.clone();
        int i = 0;
        for (String s : temp) {
            if (s == null)
                continue;
            paths[temp.length - i - 1] = s;
            i++;
        }

        JLabel empty = new JLabel(" ");
        empty.setBorder(BorderFactory.createEmptyBorder());
        this.add(empty);

        // Gia ka8e path pairnoume to teleutaio kommati meta to teleutaio slash
        // kai to orizoyme ws onoma tou koumpiou,enw vazoume ws entolh tou oloklhro to
        // path
        i = 0;
        for (String s : paths) {
            if (s == null) {
                continue;
            }
            if (s.isEmpty()) {
                continue;
            }

            String name;
            s = s.replace("\\", "/");
            if (s.contains("/")) {
                name = s.substring(s.lastIndexOf("/") + 1);
            } else {
                name = s;
            }
            JButton linkButton = new JButton(name + " > ");
            if ((s.length() == 2) && (s.endsWith(":")) && (currFrame.path.endsWith("/"))) {
                linkButton = new JButton(s);
            }

            linkButton.setBorder(BorderFactory.createEmptyBorder());
            linkButton.setContentAreaFilled(false);
            linkButton.setFont(new Font("Serif", Font.ROMAN_BASELINE, 17));
            linkButton.setActionCommand(s);
            linkButton.addActionListener(this);

            this.add(linkButton);
            i++;
        }
        // Pros8etoume to teleutaio JLabel pou einai o trexon fakelos
        currFrame.path = currFrame.path.replace("\\", "/");
        int lastSlash = currFrame.path.lastIndexOf("/");
        JLabel currFolder = new JLabel(currFrame.path.substring(lastSlash + 1, currFrame.path.length()));
        currFolder.setBorder(BorderFactory.createEmptyBorder());
        currFolder.setFont(new Font("Serif", Font.PLAIN, 17));
        this.add(currFolder);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String path = e.getActionCommand();
        path = path.replace("\\", "/");

        // Meta to search emfanizetai to koumpi back sto breadcrumb kai an path8ei
        // epistrefoume sto home directory
        if (path.equals("Back")) {
            BackAction();
            return;
        }

        if ((!(path.contains("/"))) && (path.length() == 2) && (path.charAt(1) == ':')) {
            currFrame.appContentsRefresh(path + "/");
            currFrame.path = path;
        } else {
            currFrame.appContentsRefresh(path);
        }
    }

    // ka8e egrafh toy pinaka ret exei ena ablolute path kai ret periexei n-1 paths
    // se sxesh me ta onomata fakelwn pou periexei to current path.
    private String[] pathDivider(String path) {
        path = path.replace("\\", "/");
        String[] prevFoldersPaths = new String[200];

        int i = 0;
        while (true) {
            if (!(path.contains("/"))) {
                break;
            }
            int lastSlash = path.lastIndexOf("/");
            if (i > 199)
                break;
            prevFoldersPaths[i] = path.substring(0, lastSlash);
            path = prevFoldersPaths[i];
            i++;
        }
        String[] ret = new String[i];
        System.arraycopy(prevFoldersPaths, 0, ret, 0, i);

        return ret;
    }

    // enhmerwsh breadcrumb meta apo eisodo se directory
    // kaleitai apo thn MainFrame.appContentsRefresh
    public void updateBreadCrumbPanel(String newPath) {
        this.removeAll();

        String[] prevDirsPaths = pathDivider(newPath);

        createBreadCrumbicons(prevDirsPaths);

        currFrame.revalidate();
        currFrame.repaint();
        this.revalidate();
        this.repaint();
    }

    // to breadcrumb meta to search:exei to JButton back kai to Total Results
    // kaleitai apo thn MainFrame.appRefreshAfterSearch
    public void BreadCrumbSearch(int totalResults) {
        removeAll();

        this.setBackground(new Color(255, 237, 169));

        JButton backButton = new JButton(" <<  Back  << ");
        backButton.setActionCommand("Back");
        backButton.setBackground(new Color(207, 182, 226));
        backButton.setFont(new Font("Serif", Font.ROMAN_BASELINE, 13));
        backButton.addActionListener(this);
        this.add(backButton, BorderLayout.WEST);

        JLabel result = new JLabel("   Total Results: " + totalResults);
        result.setBackground(new Color(255, 237, 169));
        this.add(result, BorderLayout.EAST);

        this.revalidate();
        this.repaint();
    }

    // afairoume ta periexomena tou searchPathCompPanel kai ta enhmerwnoume:
    // 1)olo to ContentAfterSearchPane kai antikauhstatai me to ContentsScrrollPane
    // 2)ta periexomena tou BreadcrumbPanel kai enhmerwnontai me to
    // currentPath=HomeFolder
    public void BackAction() {
        String userHome = "user.home";
        currFrame.path = System.getProperty(userHome).replace("\\", "/");
        currFrame.currDirectory = new File(currFrame.path);

        currFrame.searchPathCompPanel.remove(currFrame.pathCompPanel);
        currFrame.searchPathCompPanel.validate();
        currFrame.searchPathCompPanel.repaint();

        currFrame.pathCompPanel.remove(currFrame.breadcrumbPanel);
        currFrame.pathCompPanel.remove(currFrame.contentScrollPane);
        if (currFrame.contAfterSearchScrollPane != null)
            currFrame.pathCompPanel.remove(currFrame.contAfterSearchScrollPane);

        currFrame.pathCompPanel.validate();
        currFrame.pathCompPanel.repaint();

        currFrame.contAfterSearchScrollPane = null;
        currFrame.currFileClicked = null;
        currFrame.file4Paste = null;

        currFrame.breadcrumbPanel = new BreadCrumbPanel(currFrame);
        currFrame.breadcrumbPanel.validate();
        currFrame.breadcrumbPanel.repaint();
        currFrame.pathCompPanel.add(currFrame.breadcrumbPanel, BorderLayout.NORTH);

        currFrame.pathCompPanel.remove(currFrame.contentPanel);
        currFrame.pathCompPanel.validate();
        currFrame.pathCompPanel.repaint();

        currFrame.contentPanel.removeAll();
        currFrame.contentPanel = new JPanel();
        currFrame.contentPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 2, 2));

        currFrame.contentScrollPane = new ContentsScrollPane(currFrame.contentPanel, currFrame.currDirectory,
                currFrame);
        currFrame.contentScrollPane.getVerticalScrollBar().setUnitIncrement(15);
        currFrame.pathCompPanel.add(currFrame.contentScrollPane, BorderLayout.CENTER);

        currFrame.searchPathCompPanel.add(currFrame.pathCompPanel, BorderLayout.CENTER);

        currFrame.add(currFrame.searchPathCompPanel, BorderLayout.CENTER);
        currFrame.validate();
        currFrame.repaint();
    }
}
