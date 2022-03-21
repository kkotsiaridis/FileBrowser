/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw3;

/**
 *
 * @author Konstantinos
 */
import static ce326.hw3.EditMenu.COPY_OPTION;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author Konstantinos
 */
public class OverWriteWarningFrame extends  JDialog implements  ActionListener{
    private static final DecimalFormat DF2 = new DecimalFormat("#.##");
    public static final int FRAME_WIDTH = 330;
    public static final int FRAME_HEIGHT = 170;
    public static final int FRAME_X = 440;
    public static final int FRAME_Y = 175;
    File destinationFile;   
    File sourceFile;
    MainFrame currFrame; //gia prosvash se metablhtes tou currFrame
    boolean pasteIntoPressed;
    
    public OverWriteWarningFrame(String name,File srcFile, File dstfile,MainFrame frame,boolean psteIntoPressed){
        super();
        
        pasteIntoPressed = psteIntoPressed;
        destinationFile = dstfile;
        sourceFile =srcFile; 
        currFrame = frame;
        init(name);
    }
    
    private void init(String onoma){
        this.setModal(true);
        this.setTitle(onoma);
        
        this.setBounds(FRAME_X,FRAME_Y,FRAME_WIDTH,FRAME_HEIGHT);
        
        
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new GridLayout(0,1));
        
        JPanel propertiesPanel = new JPanel();
        propertiesPanel.setLayout(new GridLayout(0,1));
        
      
        JTextArea fileNameLabel = new JTextArea("  Name:    ");
        fileNameLabel.setEditable(false);
        fileNameLabel.setBackground(new Color(243, 221, 216));
        
        JTextArea pathLabel = new JTextArea("  Path:");
        pathLabel.setEditable(false);
        pathLabel.setBackground(new Color(243, 221, 216));
        
        JTextArea sizeLabel = new JTextArea("  Size: ");
        sizeLabel.setEditable(false);
        sizeLabel.setBackground(new Color(243, 221, 216));
        
        labelsPanel.add(fileNameLabel);
        labelsPanel.add(pathLabel);
        labelsPanel.add(sizeLabel);
        
        this.add(labelsPanel,BorderLayout.WEST);
        
        JTextArea warningMessage = new JTextArea(" Are you sure you want to overwrite this file?");
        warningMessage.setEditable(false);
        warningMessage.setBackground(new Color(207, 182, 226));
        warningMessage.setFont(new Font("Serif", Font.BOLD, 15));
        this.add(warningMessage,BorderLayout.NORTH);
        
        JTextArea fileName = new JTextArea(" "+destinationFile.getName()+"  ");
        fileName.setEditable(false);
        fileName.setBackground(new Color(235, 235, 255));
        
        
        JTextArea path = new JTextArea(" "+destinationFile.getAbsolutePath().replace("\\", "/")+ "  ");
        path.setEditable(false);
        path.setBackground(new Color(235, 235, 255));
       
        long n= 1024;
        double fileSize = destinationFile.length()/n;
        String units = " KB (";
        if(fileSize>1024){
            fileSize = fileSize/n;
            units = " MB (";
            if(fileSize>1024){
                fileSize = fileSize/n;
                units = " GB (";
            }
        }
        
        JTextArea size = new JTextArea(" "+ DF2.format(fileSize) +units+ destinationFile.length()+ " bytes)  ");
        size.setEditable(false);
        size.setBackground(new Color(235, 235, 255));
        
        propertiesPanel.add(fileName);
        propertiesPanel.add(path);
        propertiesPanel.add(size);
        
        this.add(propertiesPanel,BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
            
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);
        
        JButton confirmButton = new JButton("Overwrite");
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.addActionListener(this);
        buttonPanel.add(confirmButton);

        this.add(buttonPanel,BorderLayout.SOUTH);
        this.setVisible(true);
        this.repaint();
        this.revalidate();
    }
    
    @Override
    public void actionPerformed(ActionEvent e){
        boolean errorOccurred = false;
        String buttonClicked = e.getActionCommand();
        if(buttonClicked.equals("Cancel")){
            currFrame.editMenu.dirOverWrite = -1;
            currFrame.editMenu.cutPressed = false;
            currFrame.editMenu.copyPressed = false;
            this.dispose();
        }
        else{
            Path source = Paths.get(sourceFile.getAbsolutePath().replace("\\", "/"));
            Path dest = Paths.get(destinationFile.getAbsolutePath().replace("\\", "/"));
            try {
                if(currFrame.file4Paste.isDirectory()){
                    currFrame.editMenu.copyDirectory(sourceFile,destinationFile);
                }
                else{
                    Files.copy(source, dest ,COPY_OPTION);
                }
            } catch (IOException ex) {
                errorOccurred = true;
                if(currFrame.editMenu.copyPressed == true){
                    currFrame.contentScrollPane.errorFrame("Error in copy-paste File: "
                        +sourceFile.getName()+".\nCheck file permissions!");
                }
                else{
                    currFrame.contentScrollPane.errorFrame("Error in cut-paste File: "
                        +sourceFile.getName()+"Check file permissions");
                }
            }
            try{
            if(pasteIntoPressed){
                if(currFrame.editMenu.cutPressed == true && (errorOccurred==false)){
                    if(currFrame.file4Paste.isFile()){
                        boolean ret = currFrame.file4Paste.delete();
                        if(ret == false)
                            throw new IOException();
                    }
                }
            }
            else{
                if(currFrame.editMenu.cutPressed == true && (!currFrame.path.replace("\\", "/").
                  equals(currFrame.file4Paste.getParent().replace("\\", "/"))) && (errorOccurred==false)){
                    if(currFrame.file4Paste.isFile()){
                        boolean ret = currFrame.file4Paste.delete();
                        if(ret == false)
                            throw new IOException();
                    }
                }
            }
            }catch(IOException exp){
                currFrame.contentScrollPane.errorFrame("Error while deleting file!\nTry again.");
            }
            currFrame.editMenu.cutPressed = false;
            currFrame.editMenu.copyPressed = false;
            currFrame.appContentsRefresh(currFrame.path.replace("\\", "/"));    

            this.dispose();

        }
        
    }
    
}