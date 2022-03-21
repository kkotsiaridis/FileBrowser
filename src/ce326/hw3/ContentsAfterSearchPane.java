/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw3;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 *
 * @author Konstantinos
 */
//To JScrollPane pou periexei ta apotelesmata tou search 
public class ContentsAfterSearchPane extends JScrollPane implements ActionListener{
   
    MainFrame currFrame; //Metavlhth anaforas sto MainFrame gia prosvash se oles tis metavlhtes tou para8urou
    
    public ContentsAfterSearchPane(MainFrame frame,File[] result,JPanel contents){
        super(contents);
        currFrame = frame;

        //elegxos an yparxoun >0 apotelesmata 
        boolean hasElements = false;
        for(File f:result){
            if(f == null)
               continue;
            hasElements = true;
            break;
        }
        //an apotelesmata=0 tote warning message 
        if(hasElements == false){
            JTextArea noResult = new JTextArea("                 No Results Found!"
                + "\n         Try a Different Name or FileType.");
            
            noResult.setLineWrap(true);
            noResult.setWrapStyleWord(true);
            noResult.setEditable(false);
            noResult.setFont(new Font("Serif", Font.PLAIN, 30));
            noResult.setAlignmentX(CENTER_ALIGNMENT);
            noResult.setAlignmentY(CENTER_ALIGNMENT);
            contents.add(noResult);
            contents.repaint();
            contents.validate();
            
        }
        else{
            createResultIcons(result,contents);   
        }
 
    }
    
    //dhmiourgia twn JButtons twn apotelesmatwn tou search
    private void createResultIcons(File[] results,JPanel contents){
        this.getVerticalScrollBar().setUnitIncrement(20);

        for( File f : results){
            if(f == null)
                continue;
            ImageIcon img;
            String fileName = f.getName();
            if(f.isDirectory()){
                img = new ImageIcon("icons/folder.png");
            }
            else{
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
            }
            
            JButton fileButton = new JButton();
            String text = f.getAbsolutePath().replace("\\", "/");
            fileButton.setToolTipText(text);
            fileButton.setContentAreaFilled(false);
            fileButton.setIcon(img);
            fileButton.setText(f.getName());
            fileButton.setHorizontalTextPosition(JLabel.CENTER);
            fileButton.setVerticalTextPosition(JLabel.BOTTOM);
            fileButton.setActionCommand(f.getAbsolutePath().replace("\\","/"));
            fileButton.addActionListener(this);//actionListener gia aristero click
            
            contents.add(fileButton);    
        }
    }

    @Override
    public void actionPerformed(ActionEvent e){
        String buttonPressed = e.getActionCommand();
        
        File fileClicked = new File(buttonPressed);
       
        if(fileClicked.isFile()){
            try{
                Desktop.getDesktop().open(fileClicked);
            }
            catch(IOException ex){
                currFrame.contentScrollPane.errorFrame("No Default Program For This FileType!");
            }
       }
        else {
            enterDirectory(fileClicked);  
        }
    }
    //afairoume ta periexomena tou searchPathCompPanel kai ta enhmerwnoume:
    //1)olo to ContentAfterSearchPane kai antikauhstatai me to ContentsScrollPane
    //2)ta periexomena tou BreadcrumbPanel kai enhmerwnontai me to currentPath=clickedFilePath
    public void enterDirectory(File f){
        String clickedFilePath = f.getAbsolutePath().replace("\\", "/");
        currFrame.path = clickedFilePath;
        currFrame.currDirectory = new File(currFrame.path);
        
        currFrame.searchPathCompPanel.remove(currFrame.pathCompPanel);
        currFrame.searchPathCompPanel.validate();
        currFrame.searchPathCompPanel.repaint();
                    
        currFrame.pathCompPanel.remove(currFrame.breadcrumbPanel);
        currFrame.pathCompPanel.remove(currFrame.contentScrollPane);
        if(currFrame.contAfterSearchScrollPane!= null)
            currFrame.pathCompPanel.remove(currFrame.contAfterSearchScrollPane);
            
        currFrame.pathCompPanel.validate();
        currFrame.pathCompPanel.repaint();
            
        currFrame.contAfterSearchScrollPane = null;
        currFrame.currFileClicked = null;
        currFrame.file4Paste = null;
            
        currFrame.breadcrumbPanel = new BreadCrumbPanel(currFrame);
        currFrame.breadcrumbPanel.validate();
        currFrame.breadcrumbPanel.repaint();
        currFrame.pathCompPanel.add(currFrame.breadcrumbPanel,BorderLayout.NORTH);
            
        currFrame.pathCompPanel.remove(currFrame.contentPanel);
        currFrame.pathCompPanel.validate();
        currFrame.pathCompPanel.repaint();
            
        currFrame.contentPanel.removeAll();
        currFrame.contentPanel = new JPanel();
        currFrame.contentPanel.setLayout(new WrapLayout(FlowLayout.LEFT,3,3));
            
        currFrame.contentScrollPane = new ContentsScrollPane (currFrame.contentPanel,currFrame.currDirectory,currFrame);
        currFrame.contentScrollPane.getVerticalScrollBar().setUnitIncrement(15);
        currFrame.pathCompPanel.add(currFrame.contentScrollPane,BorderLayout.CENTER);
            
        currFrame.searchPathCompPanel.add(currFrame.pathCompPanel,BorderLayout.CENTER);
            
        currFrame.add(currFrame.searchPathCompPanel,BorderLayout.CENTER);
        currFrame.validate();
        currFrame.repaint();
    }
    
}
