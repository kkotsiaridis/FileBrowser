/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ce326.hw3;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Konstantinos
 */

public class FavouritesScrollPane extends  JScrollPane{
    MainFrame currFrame;
    Favourites favList;
    File xmlFile;
    MouseAdapter mAd;
    JPopupMenu favPopupMenu;
    File clickedFile;
    
    public FavouritesScrollPane(JPanel fav, MainFrame currentFrame){
        super(fav);
        currFrame = currentFrame;
        init();
    }
    private void init(){
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.getVerticalScrollBar().setUnitIncrement(12);
        
        
        xmlFileInit();
        
        favPopupMenu = new JPopupMenu();
        JMenuItem removeFavItem = new JMenuItem("Remove from Favourites");
        removeFavItem.addActionListener((ActionEvent e) -> {
            removeFromFavourites(clickedFile);
        });
        favPopupMenu.add(removeFavItem);
        
        mAd = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JButton clickedButton = (JButton) e.getSource();
                String buttonPressed = clickedButton.getActionCommand();
        
                clickedFile = new File(buttonPressed);
                
                if(e.getButton() == MouseEvent.BUTTON3){
                     favPopupMenu.show(e.getComponent(),e.getX(), e.getY());
                }
                else{
                    enterFavDirectory(clickedFile);  
                }
            }
        };
        
    
        if(favList == null)
            return;
        List<FavFile> favs = favList.getFavourites();
        if(favs == null){
            return;
        }
        
        favList.getFavourites().stream().filter((ff) -> !(ff == null)).forEachOrdered((ff) -> {
            File f = new File(ff.getAbsolutePath().replace("\\", "/"));
            if (f.exists()) {
                ImageIcon img;
                if (f.isDirectory()) {
                    img = new ImageIcon("icons/folder.png");
                    JButton fileButton = new JButton();
                    fileButton.setToolTipText(f.getAbsolutePath().replace("\\", "/"));
                    Image image = img.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                    img = new ImageIcon(image);
                    fileButton.setIcon(img);
                    String FileName = f.getName();
                    if(FileName.length()<=14){
                        fileButton.setText(f.getName()+ "  ");
                    }
                    else{
                        String tempName = "<html><center>"; 
                        while(FileName.length()>14){
                            tempName = tempName + FileName.substring(0,14)+"  "+"<br>";
                            FileName = FileName.substring(14);
                        }
                        tempName = tempName+FileName+"</center></html>";

                        fileButton.setText(tempName);
                    }
                    fileButton.setBackground(new Color(243, 221, 216));
                    fileButton.setHorizontalTextPosition(JLabel.CENTER);
                    fileButton.setVerticalTextPosition(JLabel.BOTTOM);
                    fileButton.setActionCommand(f.getAbsolutePath().replace("\\", "/"));
                    
                    //Same  MouseListener for every Directory for right and left click
                    fileButton.addMouseListener(mAd);
                    currFrame.favouritesPanel.add(fileButton);
                }  
            }
        });

    }
   
    public void enterFavDirectory(File f){
        String userHome = f.getAbsolutePath().replace("\\", "/");
        currFrame.path = userHome;
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
        
    private void xmlFileInit(){
        String xmlDirPath = currFrame.path.replace("\\", "/")+"/"+".java-file-browser";
        File xmlDir = new File(xmlDirPath);
        String xmlFilePath = xmlDirPath.replace("\\", "/")+"/"+"properties.xml";
        xmlFile = new File(xmlFilePath);
        boolean xmlExists = false;
        
        if(xmlDir.exists()){
            if(xmlFile.exists()){
                xmlExists = true;
            }
            else{
                try {
                    xmlFile.createNewFile();
                } catch (IOException ex) {
                    System.err.println("Properties.xml didn't init properly(Not existent File)");
                }
            }
        }
        else{
            try {
                xmlDir.mkdir();
                xmlFile.createNewFile();
            } catch (IOException ex) {
                System.err.println("Properties.xml didn't init properly(Not existent Directory)");
            }
        }

        if(xmlExists == false || xmlFile.length()<10){
            favList = new Favourites();
            favList.setFavourites(new ArrayList<>());
            
            FavFile home = new FavFile();
            home.setAbsolutePath(currFrame.currDirectory.getAbsolutePath().replace("\\", "/"));
            home.setName(currFrame.currDirectory.getName());
            
            favList.getFavourites().add(home);

            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Favourites.class);
                Marshaller jaxbMarshaller= jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(favList, xmlFile);   
            } catch (JAXBException ex) {
                System.err.println("Home dir wasn't written in Properties.xml");
            }
        }
        else{
            try{
                JAXBContext jaxbContext = JAXBContext.newInstance(Favourites.class);
                Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
                if(xmlFile != null){
                    favList = (Favourites) jaxbUnmarshaller.unmarshal(xmlFile);
                }
                else{
                    System.out.println("xmlFile is null- line 230(FavouritesScrollPane)");
                }
            } catch (JAXBException e) {
		System.err.println("Didn't retrieve existing Favourite Files");
            }
        }

    }

    public void removeFromFavourites(File f){
        FavFile fileToRemove = new FavFile();
        fileToRemove.setAbsolutePath(f.getAbsolutePath().replace("\\", "/"));
        fileToRemove.setName(f.getName());
        
        List<FavFile> favs = favList.getFavourites();
        Iterator it = favs.iterator(); 
        while (it.hasNext()) { 
            FavFile favInList = (FavFile)it.next(); 
            if (favInList.absolutePath.equals(fileToRemove.absolutePath))
                it.remove(); 
        } 
        
        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(Favourites.class);
            Marshaller jaxbMarshaller= jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(favList, xmlFile);   
        }catch (JAXBException ex) {
            System.err.println("Properties.xml wasn't updated correctly");
        }

        updateFavourites();
        
    }    
    
    public void updateFavourites(){
        currFrame.favouritesPanel.removeAll();
        currFrame.favouritesPanel.setLayout(new GridLayout(0,1));
        
        
        favList.getFavourites().stream().filter((ff) -> !(ff == null)).map((ff) -> new File(ff.getAbsolutePath().replace("\\", "/"))).filter((f) -> (f.exists())).forEachOrdered((f) -> {
            ImageIcon img;
            if(favList == null)
                return;
            if (f.isDirectory()) {
                img = new ImageIcon("icons/folder.png");
                JButton fileButton = new JButton();
                fileButton.setToolTipText(f.getAbsolutePath().replace("\\", "/"));
                Image image = img.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
                img = new ImageIcon(image);
                fileButton.setIcon(img);
                String FileName = f.getName();
                if(FileName.length()<=14){
                    fileButton.setText(f.getName()+"  ");
                }
                else{
                    String tempName = "<html><center>";
                    while(FileName.length()>14){
                        tempName = tempName + FileName.substring(0,14)+"  "+"<br>";
                        FileName = FileName.substring(14);
                    }
                    tempName = tempName+FileName+"</center></html>";
                    
                    fileButton.setText(tempName);
                }
                fileButton.setBackground(new Color(243, 221, 216));
                fileButton.setHorizontalTextPosition(JLabel.CENTER);
                fileButton.setVerticalTextPosition(JLabel.BOTTOM);
                fileButton.setActionCommand(f.getAbsolutePath().replace("\\", "/"));
                fileButton.addMouseListener(mAd);
                
                currFrame.favouritesPanel.add(fileButton); 
            }
        });
        currFrame.favouritesPanel.repaint();
        currFrame.favouritesPanel.revalidate();
        
        currFrame.revalidate();
        currFrame.repaint();
    }
         
}
    
    
    
    
    
    

