package FileBrowser;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Konstantinos
 */
public class SearchPanel extends JPanel implements ActionListener, Runnable {

    MainFrame currFrame;
    JTextField searchTextField;
    JButton searchButton, stopButton;
    File[] searchResult;
    String fileName, fileType;
    int operation;// 1=searchFiles without type, 2=search dirs , 3=search specific file type
    Thread runningThread;
    boolean interrupt;

    public SearchPanel(MainFrame currentFrame) {
        super();
        currFrame = currentFrame;
        init();
    }

    private void init() {

        this.setLayout(new BorderLayout());

        searchButton = new JButton("   Search   ");
        searchButton.setBackground(new Color(183, 210, 255));
        searchButton.addActionListener(this);
        this.add(searchButton, BorderLayout.EAST);

        stopButton = new JButton("     Stop     ");
        stopButton.setActionCommand("Stop");
        stopButton.setBackground(new Color(183, 210, 255));
        stopButton.addActionListener(this);

        searchTextField = new JTextField(MainFrame.NUMBER_OF_CHAR);
        searchTextField.addActionListener(this);

        this.add(searchTextField, BorderLayout.CENTER);
        this.setVisible(false);
    }

    @Override
    public void run() {
        switch (operation) {
            case 1:// aplh anazhth me onoma
                searchResult = searchForFiles(currFrame, currFrame.path.replace("\\", "/"), fileName);
                currFrame.appRefreshAfterSearch(searchResult);
                break;
            case 2:// anazhthsh fakelou
                searchResult = searchForDirs(currFrame, currFrame.path.replace("\\", "/"), fileName);
                currFrame.appRefreshAfterSearch(searchResult);
                break;
            case 3:// anazhthsh me sugkekrimeno eidos arxeiou
                if (fileType.contains(".")) {
                    fileType = fileType.substring(1);
                }
                searchResult = searchForFilesWithType(currFrame, currFrame.path.replace("\\", "/"), fileName, fileType);
                currFrame.appRefreshAfterSearch(searchResult);
                break;
        }
        // allagh koumpiou stop se search kai enhmervsh metavlhtvn gia nea anazhthsh
        this.remove(stopButton);
        this.add(searchButton, BorderLayout.EAST);
        this.revalidate();
        this.repaint();
        runningThread = null;
        interrupt = false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // an path8ei to stop
        if (e.getActionCommand().equals("Stop")) {
            interrupt = true;
            return;
        }
        // an pathuei to enter sto JTextField tou search enw trexei hdh h anazhthsh
        if (runningThread != null) {
            currFrame.contentScrollPane.errorFrame("Already searching...\nTry later!");
            return;
        }
        // epe3ergasia ths eisodou tou xrhsth kai anagnwrish tou eidous anazhthshs
        // enhmerwsh tvn metavlhtvn fileName kai fileType kai enar3h anazhthshs
        int spaceCounter = 0, space;

        String searchString = searchTextField.getText();
        String searchText = searchTextField.getText();

        while (searchText.contains(" ")) {
            space = searchText.indexOf(" ");
            spaceCounter++;
            searchText = searchText.substring(space + 1, searchText.length());
        }

        if (spaceCounter > 1 || (spaceCounter == 1 && (!searchString.contains("type:")))) {
            currFrame.contentScrollPane.errorFrame(" Wrong Arguments In Search!\n Try: <Name>   Type:<Type>");
            return;
        }
        if ((!(currFrame.path.contains("/"))) && (currFrame.path.length() == 2)
                && (currFrame.path.charAt(1) == ':')) {

            currFrame.path = currFrame.path + "/";
        }
        if (searchString.contains(" type:")) {
            space = searchString.indexOf(" ");
            fileName = searchString.substring(0, space);
            fileType = searchString.substring(space + 6);

            if (fileType.equals("dir")) {
                operation = 2;
                runningThread = new Thread(this);
                runningThread.start();
            } else {
                operation = 3;
                runningThread = new Thread(this);
                runningThread.start();
            }

        } else {
            fileName = searchString;
            operation = 1;
            runningThread = new Thread(this);
            runningThread.start();
        }
        // allagh koumpiou search se koumpi stop
        this.remove(searchButton);
        this.add(stopButton, BorderLayout.EAST);
        this.revalidate();
        this.repaint();
    }

    // o kurios anadromikos algori8mos pou trexei h anazhthsh kai vriskei ta arxeia
    // pou periexoun to name
    // sto onoma tous.H anazhthsh 3ekinaei apo ton current Folder.Diatrexontas thn
    // lista tou current Folder
    // elegxoume an einai arxeio h dir.An arxeio->elegxoume an periexei to name sto
    // onoma.An dir->elegoume
    // an periexei to name sto onoma tou kai kaloume thn idia sunarthsh gia ayto to
    // dir
    // Se ka8e epanalhpsh tou for an uparxoun nea apotelesmata au3anete o xoros tou
    // pinaka kai enonontai ta apotelesmata
    // Se periptwsh interrupt kanei return osa exei vrei mexri ekeinh thn strigmh
    public File[] searchForFiles(MainFrame frame, String path, String name) {
        File[] searchResultTotal = new File[1];
        File[] temp;
        int resultCounter = 0;
        File currDir = new File(path.replace("\\", "/"));
        File[] fileList = currDir.listFiles();

        if (fileList != null) {
            for (File f : fileList) {
                if (f == null)
                    continue;

                if (f.isFile()) {
                    if (f.getName().toLowerCase().contains(name.toLowerCase())) {
                        if (resultCounter > 0) {
                            int prevLength = searchResultTotal.length;
                            temp = searchResultTotal;
                            searchResultTotal = new File[prevLength + 1];
                            System.arraycopy(temp, 0, searchResultTotal, 0, temp.length);
                        }
                        searchResultTotal[resultCounter] = f;
                        resultCounter++;
                    }
                } else {
                    if (f.getName().toLowerCase().contains(name.toLowerCase())) {
                        if (resultCounter > 0) {
                            int prevLength = searchResultTotal.length;
                            temp = searchResultTotal;
                            searchResultTotal = new File[prevLength + 1];
                            System.arraycopy(temp, 0, searchResultTotal, 0, temp.length);
                        }
                        searchResultTotal[resultCounter] = f;
                        resultCounter++;
                    }
                    File[] tempR = searchForFiles(frame, f.getAbsolutePath(), name);

                    int searchLength = searchResultTotal.length;
                    File[] temp1 = searchResultTotal;
                    searchResultTotal = new File[searchLength + tempR.length];
                    System.arraycopy(temp1, 0, searchResultTotal, 0, searchLength);
                    System.arraycopy(tempR, 0, searchResultTotal, searchLength, tempR.length);
                    resultCounter = resultCounter + tempR.length;
                }
                if (interrupt == true) {
                    return searchResultTotal;
                }
            }
        }

        return searchResultTotal;
    }

    // gia search me sugkekrimeno typo arxeiou
    // kalei thn searchForFiles,thn anadromikh sunarthsh tou search kai sthn
    // sunexeia epe3ergazete ta
    // apotelesmata auths gia na krathsei mono ta arxeia pou exoun thn apaitoumenh
    // katalh3h
    public File[] searchForFilesWithType(MainFrame frame, String path, String name, String suffix) {
        File[] searchResultTotal = new File[1];
        File[] temp;
        int resultCounter = 0;

        File[] searchResultRet = searchForFiles(frame, path.replace("\\", "/"), name);

        if (searchResultRet == null) {
            return null;
        }
        for (File f : searchResultRet) {
            if (f == null)
                continue;
            if (f.isFile()) {
                if (f.getName().endsWith("." + suffix)) {
                    if (resultCounter > 0) {
                        int prevLength = searchResultTotal.length;
                        temp = searchResultTotal;
                        searchResultTotal = new File[prevLength + 1];
                        System.arraycopy(temp, 0, searchResultTotal, 0, temp.length);
                    }
                    searchResultTotal[resultCounter] = f;
                    resultCounter++;
                }
            }
        }

        return searchResultTotal;
    }

    // gia search mono dir
    // kalei thn searchForFiles,thn anadromikh sunarthsh tou search kai sthn
    // sunexeia epe3ergazete ta
    // apotelesmata auths gia na krathsei mono ta directories
    public File[] searchForDirs(MainFrame frame, String path, String name) {
        File[] searchResultTotal = new File[1];
        File[] temp;
        int resultCounter = 0;

        File[] searchResultRet = searchForFiles(frame, path.replace("\\", "/"), name);

        if (searchResultRet == null) {
            return null;
        }
        for (File f : searchResultRet) {
            if (f == null)
                continue;
            if (f.isDirectory()) {
                if (resultCounter > 0) {
                    int prevLength = searchResultTotal.length;
                    temp = searchResultTotal;
                    searchResultTotal = new File[prevLength + 1];
                    System.arraycopy(temp, 0, searchResultTotal, 0, temp.length);
                }
                searchResultTotal[resultCounter] = f;
                resultCounter++;
            }
        }
        return searchResultTotal;
    }

}
