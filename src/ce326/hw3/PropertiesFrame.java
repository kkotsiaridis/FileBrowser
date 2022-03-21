/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 *
 * @author Konstantinos
 */
public class PropertiesFrame extends JDialog implements ActionListener{
    public static final int FRAME_WIDTH = 340;
    public static final int FRAME_HEIGHT = 250;
    public static final int FRAME_X = 470;
    public static final int FRAME_Y = 175;
    private static final DecimalFormat DF2 = new DecimalFormat("#.##");
    
    File clickedFile; //apo8hkeush trexontos fakelou topika se periptwsh pou 
    //o xrhsths kanei click se allon fakelo na mhn xa8ei h plhroforia
    
    public PropertiesFrame(String fileName,File file){
        super();
        clickedFile = file;
        initWindow(fileName);
        vis();
    }
    
    private void vis(){
        this.setVisible(true);
        this.repaint();
        this.revalidate();
    }
    
    private void initWindow(String onoma){
        JTextArea size;
        this.setModal(true);
        this.setTitle(onoma);
        setBounds(FRAME_X,FRAME_Y,FRAME_WIDTH,FRAME_HEIGHT);
        setDefaultCloseOperation(PropertiesFrame.DISPOSE_ON_CLOSE);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.X_AXIS));
        
        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new GridLayout(0,1));
        
        JPanel propertiesPanel = new JPanel();
        propertiesPanel.setLayout(new GridLayout(0,1));
        
        JTextArea fileNameLabel = new JTextArea("  Name:    ");
        fileNameLabel.setEditable(false);
        fileNameLabel.setBackground(new Color(207, 182, 226));
        
        JTextArea pathLabel = new JTextArea("  Path:");
        pathLabel.setEditable(false);
        pathLabel.setBackground(new Color(207, 182, 226));
        
        JTextArea sizeLabel = new JTextArea("\n  Size: ");
        sizeLabel.setEditable(false);
        sizeLabel.setBackground(new Color(207, 182, 226));
        
        JTextArea permissionsLabel = new JTextArea("\n  Permissions: ");
        permissionsLabel.setBackground(new Color(207, 182, 226));
        
        labelsPanel.add(fileNameLabel);
        labelsPanel.add(pathLabel);
        labelsPanel.add(sizeLabel);
        labelsPanel.add(permissionsLabel);
        
        JTextArea fileName = new JTextArea(" "+clickedFile.getName()+"  ");
        fileName.setEditable(false);
        fileName.setBackground(new Color(235, 235, 255));
        JScrollPane nameScrollPane = new JScrollPane(fileName);
        nameScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        JTextArea path = new JTextArea(" "+clickedFile.getAbsolutePath().replace("\\", "/")+ "  ");
        path.setEditable(false);
        path.setBackground(new Color(235, 235, 255));
        JScrollPane pathScrollPane = new JScrollPane(path);
        pathScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        if(clickedFile.isFile()){
            long n= 1024;
            double fileSize = clickedFile.length()/n;
            String units = " KB (";
            if(fileSize>1024){
                fileSize = fileSize/n;
                units = " MB (";
                if(fileSize>1024){
                    fileSize = fileSize/n;
                    units = " GB (";
                }
            }
            size = new JTextArea("\n "+ DF2.format(fileSize) +units+ clickedFile.length()+ " bytes)  ");
        }
        else{
            long n= 1024;
            long fSize = folderSize(clickedFile);
            
            double fileSize = (fSize)/n;
            
            String units = " KB (";
            if(fileSize>1024){
                fileSize = fileSize/n;
                units = " MB (";
                if(fileSize>1024){
                    fileSize = fileSize/n;
                    units = " GB (";
                }
            }
            size = new JTextArea("\n "+ DF2.format(fileSize) +units+ fSize + " bytes)  ");
        }
        
        size.setEditable(false);
        size.setBackground(new Color(235, 235, 255));
        JScrollPane sizeScrollPane = new JScrollPane(size);
        sizeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JPanel permissionsPanel = new JPanel();
        permissionsPanel.setLayout(new BoxLayout(permissionsPanel, BoxLayout.X_AXIS));
        permissionsPanel.setBackground(new Color(235, 235, 255));
        JScrollPane permissionScrollPane = new JScrollPane(permissionsPanel);
        permissionScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        JCheckBox readButton = new JCheckBox("Read");
        readButton.addActionListener(this);
        
        JCheckBox writeButton = new JCheckBox("Write");
        writeButton.addActionListener(this);
        
        JCheckBox executeButton = new JCheckBox("Execute");
        executeButton.addActionListener(this); 
    //Se auto to kommati kwdika elegxoume 3exwrista gia ta tria checkBox an mporoume
    //na diabasoume,na grapsoume h na ektelesoume to arxeio kai 8etoume antistoixa 
    //isSelected(true/false).
    //Epipleon gia ka8e mia katastash tou arxeiou elegxoume an mporoume na to alla3oume
    //sthn allh(px:readable->unreadable).An mporoume epanaferoume thn katastash toy arxeiou
    //sthn arxikh tou (dhladh:readable->unreadable->readable),enw an den mporesoume
    //na alla3oume thn katastash tou arxeiou apergopoioume to antistoixo checkBox. 
        if(clickedFile.canRead()){
            readButton.setSelected(true);
            
            boolean changeableRead = clickedFile.setReadable(false);
            if(changeableRead==false){
                readButton.setEnabled(false);
            }
            else{
                clickedFile.setReadable(true);
            }
        }
        else{
            boolean changeableRead = clickedFile.setReadable(true);
            if(changeableRead==false){
                readButton.setEnabled(false);
            }
            else{
                clickedFile.setReadable(false);
            }
        }
        
        if(clickedFile.canWrite()){
            writeButton.setSelected(true);
            
            boolean changeableWrite = clickedFile.setWritable(false);
            if(changeableWrite==false){
                writeButton.setEnabled(false);
            }
            else{
                clickedFile.setWritable(true);
            }
        }
        else{
           boolean changeableWrite = clickedFile.setWritable(true);
            if(changeableWrite == false){
                writeButton.setEnabled(false);
            }
            else{
                clickedFile.setWritable(false);
            }
        }
        
        if(clickedFile.canExecute()){
            executeButton.setSelected(true);
            
            boolean changeableExecute = clickedFile.setExecutable(false);
            if(changeableExecute == false){
                executeButton.setEnabled(false);
            }
            else{
                clickedFile.setExecutable(true);
            }
        }
        else{
           boolean changeableExecute = clickedFile.setExecutable(true);
            if(changeableExecute == false){
                executeButton.setEnabled(false);
            }
            else{
                clickedFile.setExecutable(false);
            }
        }
        
        permissionsPanel.add(readButton);
        permissionsPanel.add(writeButton);
        permissionsPanel.add(executeButton);

        propertiesPanel.add(nameScrollPane);
        propertiesPanel.add(pathScrollPane);
        propertiesPanel.add(sizeScrollPane);
        propertiesPanel.add(permissionScrollPane);

        add(labelsPanel,BorderLayout.WEST);
        add(propertiesPanel,BorderLayout.CENTER);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e){
        String checkBoxClicked = e.getActionCommand();
        JCheckBox currCheckBox = (JCheckBox)e.getSource();
          
        switch (checkBoxClicked) {
            case "Read":
                if(!currCheckBox.isSelected()){
                    clickedFile.setReadable(false);
                }
                else{
                    clickedFile.setReadable(true);
                }   break;
            case "Write":
                if(!currCheckBox.isSelected()){
                    clickedFile.setWritable(false);
                }
                else{
                    clickedFile.setWritable(true);
                }   break;
            default:
                if(!currCheckBox.isSelected()){
                    clickedFile.setExecutable(false);
                }
                else{
                    clickedFile.setExecutable(true);
                    
                }   break;
        }
    }
    
    public  long folderSize(File directory) {
    long totalSize = 0;
    
        if(directory != null && directory.isDirectory()){
            totalSize = totalSize + directory.length();
            File[] fileList = directory.listFiles();
            if(fileList != null){
                for (File f : fileList) {
                    if(f==null)
                        continue;
                    if (f.isFile())
                        totalSize = totalSize + f.length();
                    else
                        totalSize = totalSize + folderSize(f);
                }
            }
        }
        return totalSize;
    }
  
}
