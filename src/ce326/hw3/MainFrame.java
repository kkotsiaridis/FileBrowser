package ce326.hw3;



import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Konstantinos
 */
public class MainFrame extends JFrame{
    public static final int  FRAME_WIDTH = 790;
    public static final int  FRAME_HEIGHT = 469;
    public static int FRAME_X = 270;
    public static int FRAME_Y = 122;
    public static int NUMBER_OF_CHAR = 50;
    ContentsAfterSearchPane contAfterSearchScrollPane;
    JPanel searchPathCompPanel;
    JPanel favouritesPanel;
    FavouritesScrollPane favPane;
    JPanel pathCompPanel;
    JPanel searchPanel;
    BreadCrumbPanel breadcrumbPanel; //created in: this.appInit and used in: this.appContentsRefresh
    JPanel contentPanel;  //created in: this.appInit and used in: this.appContentsRefresh
    ContentsScrollPane contentScrollPane; //created in: this.appInit and used in: this.appContentsRefresh
    EditMenu editMenu;      //created in: this.appInit and used in: MyMouseAdapter.createPopUpMenu for using EditMenu as ActionListener
    String path;          //is updated from:(a)this.appInit (b)this.setCurrentPath (c)this.appContentsRefresh
    File currDirectory;   //is updated from:(a)this.appInit (b)this.appContentsRefresh
    File currFileClicked; //is updated from:(a)MyMouseApapter.mouseClicked (b)ContentsScrollPane.actionPerformed
    File file4Paste;      //is used and updated in editMenu.actionPerformed (for copy/cut paste) 
    
    
    public MainFrame(String AppName,int numberOfWindow){
        super(AppName);
        
        appInit(numberOfWindow); 
    }
    
    private void appInit(int mult){
        if(mult<7 || mult>21){
            if(mult>21){
                FRAME_X = 207;
                FRAME_Y = 2;
                mult = mult - 21;
            }
            setBounds(FRAME_X+mult*15,FRAME_Y+mult*15,FRAME_WIDTH,FRAME_HEIGHT);
        }
        else{
            int x = mult-6;
            mult = 7;
            setBounds((FRAME_X+mult*15-15*x),(FRAME_Y+mult*15-15*x),FRAME_WIDTH,FRAME_HEIGHT);
        }
        
        setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        String userHome = "user.home";
        path = System.getProperty(userHome);
        path = path.replace("\\", "/");
        currDirectory = new File(path);
        
        JPanel extraPanel = new JPanel();
        extraPanel.setLayout(new BorderLayout());
        
        JTextField favLabel = new JTextField("    Favourites    ");
        favLabel.setFont(new Font("Serif", Font.PLAIN, 17));
        favLabel.setBackground(new Color(255, 170, 181));
        favLabel.setBorder(BorderFactory.createEmptyBorder());
        favLabel.setHorizontalAlignment(JTextField.CENTER);
        extraPanel.add(favLabel,BorderLayout.NORTH);
        
        favouritesPanel = new JPanel();
        favouritesPanel.setBackground(new Color(243, 221, 216));
        favouritesPanel.setLayout(new GridLayout(0,1));
        favPane = new FavouritesScrollPane(favouritesPanel, this);
        extraPanel.add(favPane,BorderLayout.CENTER);
        
        add(extraPanel,BorderLayout.WEST);
        
        searchPathCompPanel = new JPanel(new BorderLayout());
        pathCompPanel = new JPanel(new BorderLayout());
        
        searchPanel = new SearchPanel(this);
        
        searchPathCompPanel.add(searchPanel,BorderLayout.NORTH);
       
        breadcrumbPanel = new BreadCrumbPanel(this);
        
        pathCompPanel.add(breadcrumbPanel,BorderLayout.NORTH);
        createMenuBar();
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new WrapLayout(FlowLayout.LEFT,2,2));
        contentScrollPane = new ContentsScrollPane (contentPanel,currDirectory,this);
        contentScrollPane.getVerticalScrollBar().setUnitIncrement(15);
        pathCompPanel.add(contentScrollPane,BorderLayout.CENTER);
        
        searchPathCompPanel.add(pathCompPanel,BorderLayout.CENTER);
        add(searchPathCompPanel,BorderLayout.CENTER);

    }
    
    public void createMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(207, 182, 226));
        
        FileMenu fileMenu = new FileMenu("File",this);
        
        editMenu = new EditMenu("Edit",this);
        
        JButton searchMenu = new JButton("Search");
        searchMenu.setBorder(BorderFactory.createEmptyBorder());
        searchMenu.setContentAreaFilled(false);
       
        searchMenu.addActionListener((ActionEvent e) -> {
            if(searchPanel.isVisible()== true ){
                searchPanel.setVisible(false);
            }
            else{
                searchPanel.setVisible(true);
            }
        });
        
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        
        add(menuBar,BorderLayout.NORTH);
    }
    
    public void appRefreshAfterSearch(File[] result){
        int totalResults = 0;
        pathCompPanel.removeAll();
        pathCompPanel.revalidate();
        pathCompPanel.repaint();
        pathCompPanel.setLayout(new BorderLayout());
        
        for(File f:result){
            if(f == null)
                continue;
            totalResults++;
        }
        
        breadcrumbPanel.BreadCrumbSearch(totalResults);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(0,1));
        contAfterSearchScrollPane = new ContentsAfterSearchPane(this, result,contentPanel);
        contAfterSearchScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contAfterSearchScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        pathCompPanel.add(breadcrumbPanel,BorderLayout.NORTH);
        pathCompPanel.add(contAfterSearchScrollPane,BorderLayout.CENTER);
        
       
        pathCompPanel.repaint();
        pathCompPanel.validate();

        validate();
        repaint();
    }
   
    public void appContentsRefresh(String nextPath){
        nextPath = nextPath.replace("\\", "/");
        currDirectory = new File(nextPath);
        if(currFileClicked == null){
            editMenu.setEnabled(false);
        }
        else{
            editMenu.setEnabled(true);
        }
        path = nextPath;
        contentPanel.removeAll();
        breadcrumbPanel.updateBreadCrumbPanel(nextPath);
        contentScrollPane.repaint();
        contentScrollPane.validate();
        contentScrollPane.updateContents(contentPanel,currDirectory,this);
        contentScrollPane.validate();
        validate();
    }
    
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainFrame mFr= new MainFrame("FileBrowser1",0);
            mFr.setVisible(true); 
        });
    }
}
    
   
    

