package ce326.hw3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Konstantinos
 */
//To JScrollPane me ta periexomena tou current Directory
public class ContentsScrollPane extends JScrollPane implements ActionListener{
    JButton prevButtonClicked;//JButton apo prohgoumeno click
    JButton currButtonClicked;//Jbutton apo current click
    JButton currButtonRightClick; //Jbutton de3iou click,xrhsimopoihtai mono ston MouseAdapter gia de3i click
    //sta arxeia kai emfanish tou popupMenu
    long prevTimeClicked, currTimeClicked;
    File[] filesList;//Pinakas me ta periexomena tou current Directory
    MainFrame currFrame; //Metavlhth anaforas sto MainFrame gia prosvash se oles tis metavlhtes tou para8urou
    MouseAdapter myMA; //MouseListener olwn twn arxeiwn gia right click
    
    public ContentsScrollPane (JPanel contentPanel,File currDirectory,MainFrame currentFrame){
        super(contentPanel);
        
        currFrame = currentFrame;
        prevTimeClicked = 0;
        prevButtonClicked = new JButton();
        currButtonClicked = null;
        if(currFrame.currFileClicked == null){
            currFrame.editMenu.setEnabled(false);
        }
        init(currDirectory);
    }
    private void init(File currDirectory){
        
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        //mouseListener for ContentsPanel 
        //leftClick->unmark selected file
        //singleRightClick->show popupMenu
        this.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(currButtonClicked!=null){
                    currButtonClicked.setContentAreaFilled(false);
                    currButtonClicked = null;
                    currFrame.currFileClicked = null;
                    if(currFrame.editMenu.copyPressed || currFrame.editMenu.cutPressed){
                        currFrame.editMenu.setEnabled(true);
                    }
                    else{
                        currFrame.editMenu.setEnabled(false);
                    }
                }
                if(e.getButton() == MouseEvent.BUTTON3 && 
                        (currFrame.editMenu.copyPressed ||currFrame.editMenu.cutPressed)){
                    currFrame.editMenu.rightClickPopupMenu.show(e.getComponent(),e.getX(), e.getY());
                }
            }
        });
        //MouseListener olwn twn arxeiwn pou kalei to popupMenu se periptwsh right click
        //Afairei/pros8etei kai kanei enabled/disabled ta paste kai pasteInto JMenuItems 
        //twn popupMenu kai editMenu
        myMA = new MouseAdapter() {
             @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 ) {
                    currButtonRightClick = (JButton)e.getSource();
                     String filePressedPath;
                    if(currFrame.path.endsWith("/")){
                        filePressedPath =currFrame.path.replace("\\", "/")+
                        currButtonRightClick.getActionCommand();
                    }
                    else{
                        filePressedPath =currFrame.path.replace("\\", "/")+"/"+
                                currButtonRightClick.getActionCommand();
                    }
                    File filePressed = new File(filePressedPath);
                    if(filePressed.isDirectory()){
                        currFrame.editMenu.popupMenu.add(currFrame.editMenu.pasteIntoItemp);
                        if(currFrame.editMenu.copyPressed || currFrame.editMenu.cutPressed){
                            currFrame.editMenu.pasteIntoItemp.setEnabled(true);
                            currFrame.editMenu.pasteItem.setEnabled(true);
                            currFrame.editMenu.pasteItemp.setEnabled(true);
                        }
                        else{
                            currFrame.editMenu.pasteIntoItemp.setEnabled(false);
                            currFrame.editMenu.pasteItemp.setEnabled(false);
                            currFrame.editMenu.pasteItem.setEnabled(false);
                        }
                    }
                    else{
                        currFrame.editMenu.popupMenu.remove(currFrame.editMenu.pasteIntoItemp);
                        if(currFrame.editMenu.copyPressed || currFrame.editMenu.cutPressed){
                            currFrame.editMenu.pasteItem.setEnabled(true);
                            currFrame.editMenu.pasteItemp.setEnabled(true);
                        }
                        else{
                            currFrame.editMenu.pasteItemp.setEnabled(false);
                            currFrame.editMenu.pasteItem.setEnabled(false);
                        }
                    }

                    if(currFrame.contentScrollPane.currButtonRightClick.
                            equals(currFrame.contentScrollPane.prevButtonClicked)){

                        currFrame.editMenu.popupMenu.show(e.getComponent(),e.getX(), e.getY());
                    }
                }
            }
        };

        filesList = currDirectory.listFiles();
        if(filesList == null)
            return;
        File[] filesList1 = filesList.clone();
        filesList1= getAlphabeticalFolders(filesList1);
        File[] filesList2 = getAlphabeticalFiles(filesList);
        filesList = joinLists(filesList1,filesList2);
        
        if(filesList!= null){
            createFileIcons(filesList,currFrame.contentPanel);
        }
    }
    
     //enhmerwsh ContentPanel meta apo eisodo se directory
    //kaleitai apo thn MainFrame.appContentsRefresh
    public void updateContents(JPanel contentPanel,File currDirectory,MainFrame currentFrame){
        contentPanel.removeAll();
        contentPanel.validate();

        filesList = currDirectory.listFiles();
        
        if(filesList!=null){
            File[] filesList1 = filesList.clone();
            filesList1= getAlphabeticalFolders(filesList1);
        
        
            File[] filesList2 = getAlphabeticalFiles(filesList);
        
            filesList = joinLists(filesList1,filesList2);
            if(filesList!= null){
                this.createFileIcons(filesList,contentPanel);
            }
        }
        JScrollBar scrollBar = this.getVerticalScrollBar();
        scrollBar.setValue(verticalScrollBar.getMinimum());
        this.validate();
    }
    

    public File findFile(String fileName,File[] fileListTemp){
        File returnFile = fileListTemp[0];
        for(File f: fileListTemp){
            if(f ==null)
                continue;
            if(f.getName().equals(fileName)){
                returnFile = f;
                break;
            } 
            returnFile = f;
        }
        
        return returnFile;
    } 
    
    public File[] getAlphabeticalFolders(File[] currList){
        File[] tempList = new File[currList.length];
        int j=0;
        
        for (File f : currList) {
            if(f ==null)
                continue;
            if (f.isDirectory()) {
                tempList[j] = f;
                j++;
            }
        }

        return tempList;
    }
    
    public File[] getAlphabeticalFiles(File[] currList){
        File[] tempList = new File[currList.length];
        int j = 0;
        
        for (File f : currList) {
            if(f ==null)
                continue;
            if (f.isFile()) {
                tempList[j] = f;
                j++;
            }
        }
          
        return tempList;
    }
    
    public File[] joinLists(File[] list1,File[] list2){
        int totalSize=0,i=0;
        
        for(File f:list1){
            if(f ==null)
                continue;
            totalSize++;
        }
        for(File f:list2){
            if(f ==null)
                continue;
            totalSize++;
        }
       
        File[] totalList = new File[totalSize];
        
        for(File f:list1){
            if(f ==null)
                continue;
            totalList[i]= f;
            i++;
        }
        
        for(File f:list2){
            if(f ==null)
                continue;
            totalList[i]= f;
            i++;
        }
        
        return totalList;
    }
    
    private void createFileIcons(File[] filesList,JPanel contentPanel){
        
        for( File f : filesList) {
            if(f == null)
                continue;
            if(f.getName().startsWith("."))
                continue;
            String fileName = f.getName();
            if(f.isDirectory()){
                ImageIcon img = new ImageIcon("icons/folder.png");
                JButton fileButton = new JButton();
                fileButton.setBorder(BorderFactory.createEmptyBorder());
                fileButton.setContentAreaFilled(false);
                fileButton.setIcon(img);
                fileButton.setText(fileName);
                fileButton.setHorizontalTextPosition(JLabel.CENTER);
                fileButton.setVerticalTextPosition(JLabel.BOTTOM);
                fileButton.addActionListener(this);
                fileButton.addMouseListener(myMA);
                contentPanel.add(fileButton);
            }
            else if(f.isFile()){
                ImageIcon img;
                
                if((fileName.endsWith("audio"))||(fileName.endsWith("mp3"))||
                        (fileName.endsWith("ogg"))||(fileName.endsWith("wav"))){
                    img = new ImageIcon("icons/audio.png"); 
                }
                else if((fileName.endsWith("bmp"))||(fileName.endsWith("giff"))||
                        (fileName.endsWith("image"))||(fileName.endsWith("jpg"))||
                        (fileName.endsWith("png"))||(fileName.endsWith("jpeg"))){
                    img = new ImageIcon("icons/bmp.png"); 
                }
                else if((fileName.endsWith("doc"))||(fileName.endsWith("docx"))||
                        (fileName.endsWith("odt"))){
                    img = new ImageIcon("icons/doc.png");
                }
                else if((fileName.endsWith("gz"))||(fileName.endsWith("tar"))||
                        (fileName.endsWith("tgz"))||(fileName.endsWith("zip"))){
                    img = new ImageIcon("icons/gz.png");
                }
                else if((fileName.endsWith("htm"))||(fileName.endsWith("html"))||
                        (fileName.endsWith("xml"))){
                    img = new ImageIcon("icons/html.png");
                }
                else if((fileName.endsWith("ods"))||(fileName.endsWith("xlsx"))||
                        (fileName.endsWith("xlx"))){
                    img = new ImageIcon("icons/ods.png");
                }
                else if(fileName.endsWith("pdf")){
                    img = new ImageIcon("icons/pdf.png");
                }
                else if(fileName.endsWith("txt")){
                    img = new ImageIcon("icons/txt.png");
                }
                else if(fileName.endsWith("video")){
                    img = new ImageIcon("icons/video.png");
                }
                else{
                    img = new ImageIcon("icons/question.png");
                }
                
                JButton fileButton = new JButton();
                fileButton.setBorder(BorderFactory.createEmptyBorder());
                fileButton.setContentAreaFilled(false);
                fileButton.setIcon(img);
                fileButton.setText(fileName);
                fileButton.setHorizontalTextPosition(JLabel.CENTER);
                fileButton.setVerticalTextPosition(JLabel.BOTTOM);
                fileButton.addActionListener(this);
                fileButton.addMouseListener(myMA);
                contentPanel.add(fileButton);
            }
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e){
        currTimeClicked  = System.currentTimeMillis();
        currButtonClicked = (JButton)e.getSource();
        String fileClickedString = e.getActionCommand();
        File fileClicked = findFile(fileClickedString,filesList);
        
        //an doubleclick sto idio arxeio 
        if((currButtonClicked !=null)&&currButtonClicked.equals(prevButtonClicked) &&
               (currTimeClicked - prevTimeClicked <500)){
            if(fileClicked.isFile()){//an arxeio
                prevButtonClicked.setContentAreaFilled(false);
                try{
                    Desktop.getDesktop().open(fileClicked);
                }
                catch(IOException ex){
                    errorFrame("No Default Program For This FileType!");
                }
            }
            else{//an directory 
                currFrame.path = currFrame.path.replace("\\", "/");
                String path = currFrame.path;
                path = path.replace("\\", "/");
                if(path.endsWith("/")){
                    path = path +fileClickedString;
                }
                else{
                    path = path + "/" +fileClickedString;
                }
                currFrame.currFileClicked = null;
               
                currFrame.appContentsRefresh(path);
            }
        }
        else{//an mono aristero click se allo arxeio apo to prohgoumeno
            prevButtonClicked.setContentAreaFilled(false);
            currButtonClicked.setContentAreaFilled(true);
            
            currButtonClicked.setBackground(Color.LIGHT_GRAY);
            currFrame.currFileClicked = fileClicked;
            if(currFrame.currFileClicked.isFile()){
                currFrame.editMenu.popupMenu.remove(currFrame.editMenu.favouritesItemp);
                currFrame.editMenu.remove(currFrame.editMenu.favouritesItem);
            }
            else{
                currFrame.editMenu.popupMenu.add(currFrame.editMenu.favouritesItemp);
                currFrame.editMenu.add(currFrame.editMenu.favouritesItem);
            }
            prevButtonClicked = currButtonClicked;
            prevTimeClicked = currTimeClicked;
        }
      
        if(currFrame.currFileClicked == null){
            if((!currFrame.editMenu.copyPressed && !currFrame.editMenu.cutPressed)){
                currFrame.editMenu.setEnabled(false);
            }
            else{
                currFrame.editMenu.setEnabled(true);
            }
        }
        else{
             currFrame.editMenu.setEnabled(true);
        }

    }

    //Modal box gia enhmerwsh xrhsth gia ola ta errors
    public void errorFrame(String errorMessage){
        JDialog noDefaultProgram = new JDialog();
        noDefaultProgram.setModal(true);
        noDefaultProgram.setTitle("  ERROR");
        noDefaultProgram.setBounds(550,250,330,100);
        noDefaultProgram.setDefaultCloseOperation(PropertiesFrame.DISPOSE_ON_CLOSE);
        JTextArea noDefWarning = new JTextArea(errorMessage);
        noDefWarning.setEditable(false);
        noDefWarning.setBackground(new Color(235, 235, 255));
        noDefWarning.setFont(new Font("Serif", Font.PLAIN, 20));
        noDefWarning.setForeground(Color.RED);
        noDefaultProgram.add(noDefWarning,BorderLayout.CENTER);
        noDefaultProgram.setVisible(true);
    }
    
}
