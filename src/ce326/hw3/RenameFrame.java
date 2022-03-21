/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;
import javax.swing.JTextArea;

/**
 *
 * @author Konstantinos
 */
public class RenameFrame extends JDialog implements ActionListener{   
    public static final int FRAME_WIDTH = 250;
    public static final int FRAME_HEIGHT = 110;
    public static final int FRAME_X = 440;
    public static final int FRAME_Y = 175;
    File clickedFile;    //apo8hkeush trexontos fakelou topika se periptwsh pou 
    //o xrhsths kanei click se allon fakelo na mhn xa8ei h plhroforia
    MainFrame currFrame; //gia prosvash se metablhtes tou currFrame
    JTextField fileName;
    
    public RenameFrame(String name,File file,MainFrame frame){
        super();
        clickedFile = file;
        currFrame = frame;
        init(name);
    }
    private void init(String onoma){
        this.setModal(true);
        this.setTitle(onoma);

        createComponentsRename();
        
        this.setVisible(true);
        this.repaint();
        this.revalidate();
    }
    
    private void createComponentsRename(){
        
        this.setBounds(FRAME_X,FRAME_Y,FRAME_WIDTH,FRAME_HEIGHT);
 
        JTextArea newNameLabel = new JTextArea("\n  Set New Name:");
        newNameLabel.setEditable(false);
        newNameLabel.setBackground(new Color(243, 221, 216));
        add(newNameLabel,BorderLayout.WEST);
        
        JTextArea warningMessage = new JTextArea(" Rename: "+clickedFile.getName()+ "?");
        warningMessage.setEditable(false);
        warningMessage.setBackground(new Color(207, 182, 226));
        warningMessage.setFont(new Font("Serif", Font.CENTER_BASELINE, 15));
        this.add(warningMessage,BorderLayout.NORTH);
        
        fileName = new JTextField(clickedFile.getName());
        fileName.addActionListener(this);
        fileName.setBackground(new Color(235, 235, 255));
        fileName.setFont(new Font("Serif", Font.PLAIN, 17));
        fileName.requestFocus();
        fileName.selectAll();
        fileName.setCaretPosition(0);
        fileName.selectAll();
        add(fileName,BorderLayout.CENTER); 
    }
    
    @Override
    public void actionPerformed(ActionEvent e){   
        File newName = new File(currFrame.currFileClicked.getParent().replace("\\", "/")+"/"+fileName.getText());

        if(newName.exists()){
            if(newName.isFile()){
                currFrame.contentScrollPane.errorFrame("File with this name already exists! "
                        + "\nTry a different name.");
            }
            else{
                currFrame.contentScrollPane.errorFrame("Dir with this name already exists! "
                        + "\nTry a different name.");
            }
            this.dispose();
            return;
        }
        currFrame.currFileClicked.renameTo(newName);
        currFrame.appContentsRefresh(currFrame.path.replace("\\", "/")); 
        this.dispose();
    }
    
}
    
    
    

