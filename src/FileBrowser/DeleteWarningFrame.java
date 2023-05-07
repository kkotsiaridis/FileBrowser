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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/**
 *
 * @author Konstantinos
 */
public class DeleteWarningFrame extends JDialog implements ActionListener {
    private static final DecimalFormat DF2 = new DecimalFormat("#.##");
    public static final int FRAME_WIDTH = 330;
    public static final int FRAME_HEIGHT = 170;
    public static final int FRAME_X = 440;
    public static final int FRAME_Y = 175;
    File clickedFile; // apo8hkeush trexontos fakelou topika
    MainFrame currFrame; // gia prosvash se metablhtes tou currFrame

    public DeleteWarningFrame(String name, File file, MainFrame frame) {
        super();

        clickedFile = file;
        currFrame = frame;

        init(name);
    }

    private void init(String onoma) {
        this.setModal(true);
        this.setTitle(onoma);

        createComponents();
        this.setVisible(true);
        this.repaint();
        this.revalidate();
    }

    private void createComponents() {

        this.setBounds(FRAME_X, FRAME_Y, FRAME_WIDTH, FRAME_HEIGHT);

        JPanel labelsPanel = new JPanel();
        labelsPanel.setLayout(new GridLayout(0, 1));

        JPanel propertiesPanel = new JPanel();
        propertiesPanel.setLayout(new GridLayout(0, 1));

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

        this.add(labelsPanel, BorderLayout.WEST);

        JTextArea warningMessage = new JTextArea(" Are you sure you want to delete this file?");
        warningMessage.setEditable(false);
        warningMessage.setBackground(new Color(207, 182, 226));
        warningMessage.setFont(new Font("Serif", Font.BOLD, 15));
        this.add(warningMessage, BorderLayout.NORTH);

        JTextArea fileName = new JTextArea(" " + clickedFile.getName() + "  ");
        fileName.setEditable(false);
        fileName.setBackground(new Color(235, 235, 255));

        JTextArea path = new JTextArea(" " + clickedFile.getAbsolutePath().replace("\\", "/") + "  ");
        path.setEditable(false);
        path.setBackground(new Color(235, 235, 255));

        long n = 1024;
        double fileSize = clickedFile.length() / n;
        String units = " KB (";
        if (fileSize > 1024) {
            fileSize = fileSize / n;
            units = " MB (";
            if (fileSize > 1024) {
                fileSize = fileSize / n;
                units = " GB (";
            }
        }

        JTextArea size = new JTextArea(" " + DF2.format(fileSize) + units + clickedFile.length() + " bytes)  ");
        size.setEditable(false);
        size.setBackground(new Color(235, 235, 255));

        propertiesPanel.add(fileName);
        propertiesPanel.add(path);
        propertiesPanel.add(size);

        this.add(propertiesPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.addActionListener(this);
        buttonPanel.add(cancelButton);

        JButton confirmButton = new JButton("Delete");
        confirmButton.setBackground(Color.LIGHT_GRAY);
        confirmButton.addActionListener(this);
        buttonPanel.add(confirmButton);

        this.add(buttonPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String buttonClicked = e.getActionCommand();
        if (buttonClicked.equals("Cancel")) {
            this.dispose();
        } else {
            try {
                if (clickedFile.isFile()) {
                    boolean ret = clickedFile.delete();
                    if (ret == false)
                        throw new IOException();
                } else {
                    deleteDirectory(clickedFile);
                    currFrame.favPane.removeFromFavourites(clickedFile);
                }
            } catch (IOException ex) {
                currFrame.contentScrollPane.errorFrame("Error while deleting file!\nTry again.");
            }
            currFrame.currFileClicked = null;
            currFrame.contentScrollPane.currButtonClicked = null;
            currFrame.appContentsRefresh(currFrame.path.replace("\\", "/"));

            this.dispose();
        }

    }

    public static void deleteDirectory(File directory) throws IOException {
        if (directory != null && directory.isDirectory()) {
            File[] fileList = directory.listFiles();
            if (fileList != null) {
                for (File f : fileList) {
                    if (f == null)
                        continue;
                    if (f.isFile()) {
                        boolean ret = f.delete();
                        if (ret == false)
                            throw new IOException();
                    } else
                        deleteDirectory(f);
                }
            }
            boolean ret = directory.delete();
            if (ret == false)
                throw new IOException();
        }
    }
}
