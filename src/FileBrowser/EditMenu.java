package FileBrowser;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

/**
 *
 * @author Konstantinos
 */
public class EditMenu extends JMenu implements ActionListener {
    public static final CopyOption COPY_OPTION = StandardCopyOption.REPLACE_EXISTING;
    MainFrame currFrame;
    boolean cutPressed, copyPressed, pastePressed, renamePressed;
    JMenuItem pasteItem, pasteItemp, pasteIntoItemp, favouritesItem, favouritesItemp;
    JPopupMenu popupMenu, rightClickPopupMenu;
    File file4Edit;
    int dirOverWrite;

    public EditMenu(String name, MainFrame frame) {
        super(name);
        currFrame = frame;
        file4Edit = currFrame.currFileClicked;
        popupMenu = new JPopupMenu();
        rightClickPopupMenu = new JPopupMenu();
        cutPressed = false;
        copyPressed = false;
        pastePressed = false;
        renamePressed = false;

        createMenuItems();
    }

    private void createMenuItems() {

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(this);
        this.add(cutItem);

        JMenuItem cutItemp = new JMenuItem("Cut");
        cutItemp.addActionListener(this);
        popupMenu.add(cutItemp);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(this);
        this.add(copyItem);

        JMenuItem copyItemp = new JMenuItem("Copy");
        copyItemp.addActionListener(this);
        popupMenu.add(copyItemp);

        pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(this);
        if (!copyPressed && !cutPressed) {
            pasteItem.setEnabled(false);
        }
        this.add(pasteItem);

        pasteItemp = new JMenuItem("Paste");
        pasteItemp.addActionListener(this);
        if (!copyPressed && !cutPressed) {
            pasteItemp.setEnabled(false);
        }
        popupMenu.add(pasteItemp);

        JMenuItem pasteItemRightClick = new JMenuItem("Paste");
        pasteItemRightClick.addActionListener(this);
        rightClickPopupMenu.add(pasteItemRightClick);

        pasteIntoItemp = new JMenuItem("Paste Into");
        pasteIntoItemp.addActionListener(this);

        JMenuItem renameItem = new JMenuItem("Rename");
        renameItem.addActionListener(this);
        this.add(renameItem);

        JMenuItem renameItemp = new JMenuItem("Rename");
        renameItemp.addActionListener(this);
        popupMenu.add(renameItemp);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(this);
        this.add(deleteItem);

        JMenuItem deleteItemp = new JMenuItem("Delete");
        deleteItemp.addActionListener(this);
        popupMenu.add(deleteItemp);

        favouritesItem = new JMenuItem("Add to Favourites");
        favouritesItem.addActionListener(this);
        this.add(favouritesItem);

        favouritesItemp = new JMenuItem("Add to Favourites");
        favouritesItemp.addActionListener(this);
        popupMenu.add(favouritesItemp);

        JMenuItem propertiesItem = new JMenuItem("Properties");
        propertiesItem.addActionListener(this);
        this.add(propertiesItem);

        JMenuItem propertiesItemp = new JMenuItem("Properties");
        propertiesItemp.addActionListener(this);
        popupMenu.add(propertiesItemp);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String fileEditString = e.getActionCommand();

        switch (fileEditString) {
            case "Cut":
                if (currFrame.currFileClicked != null) {
                    copyPressed = false;
                    cutPressed = true;
                    pasteItem.setEnabled(true);
                    pasteItemp.setEnabled(true);
                    currFrame.editMenu.setEnabled(true);
                    currFrame.file4Paste = currFrame.currFileClicked;

                }
                break;
            case "Copy":
                if (currFrame.currFileClicked != null) {
                    cutPressed = false;
                    copyPressed = true;
                    pasteItem.setEnabled(true);
                    pasteItemp.setEnabled(true);
                    currFrame.editMenu.setEnabled(true);
                    currFrame.file4Paste = currFrame.currFileClicked;

                }
                break;
            case "Paste":
                pasteActions();
                if (currFrame.currFileClicked == null) {
                    currFrame.editMenu.setEnabled(true);
                } else {
                    currFrame.editMenu.setEnabled(true);
                }
                break;
            case "Paste Into":
                PasteIntoAction();
                break;
            case "Rename":
                if (currFrame.currFileClicked != null) {
                    new RenameFrame(" Rename: " +
                            currFrame.currFileClicked.getName(), currFrame.currFileClicked, currFrame);

                }
                renamePressed = true;
                break;
            case "Delete":
                if (currFrame.currFileClicked != null) {
                    new DeleteWarningFrame(" Delete File: " +
                            currFrame.currFileClicked.getName(), currFrame.currFileClicked, currFrame);
                }
                break;
            case "Add to Favourites":
                AddFavouritesAction(currFrame.currFileClicked);
                break;
            default:
                if (currFrame.currFileClicked != null) {
                    new PropertiesFrame(" Properties: " +
                            currFrame.currFileClicked.getName(), currFrame.currFileClicked);
                }
                break;
        }
    }

    private void AddFavouritesAction(File file4Favs) {

        if (file4Favs.isDirectory()) {
            Boolean check = checkForDoubleFav(file4Favs);
            if (check == true) {
                currFrame.contentScrollPane.errorFrame("Directory already in Favourites! "
                        + "\nTry to add another Directory.");
                return;
            }
            FavFile newAddition = new FavFile();
            newAddition.setAbsolutePath(file4Favs.getAbsolutePath().replace("\\", "/"));
            newAddition.setName(file4Favs.getName());

            if (currFrame.favPane.favList.getFavourites() != null) {
                currFrame.favPane.favList.getFavourites().add(newAddition);
            } else {
                currFrame.favPane.favList.setFavourites(new ArrayList<>());
                currFrame.favPane.favList.getFavourites().add(newAddition);
            }

            try {
                JAXBContext jaxbContext = JAXBContext.newInstance(Favourites.class);
                Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
                jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                jaxbMarshaller.marshal(currFrame.favPane.favList, currFrame.favPane.xmlFile);

            } catch (JAXBException ex) {
                System.err.println("Error in Add Favourite");
                return;
            }

            currFrame.favPane.updateFavourites();
        }
    }

    private boolean checkForDoubleFav(File check) {
        List<FavFile> favs = currFrame.favPane.favList.getFavourites();

        if (favs == null)
            return (false);
        return favs.stream().filter((f) -> !(f == null)).anyMatch(
                (f) -> (f.getAbsolutePath().replace("\\", "/").equals(check.getAbsolutePath().replace("\\", "/"))));
    }

    private void PasteIntoAction() {
        boolean errorOccurred = false;
        File newDestPaste;

        newDestPaste = new File(currFrame.currFileClicked.getAbsolutePath().replace("\\", "/")
                + "/" + currFrame.file4Paste.getName());

        if (newDestPaste.exists()) {
            new OverWriteWarningFrame(" Overwrite File: " +
                    newDestPaste.getName(), currFrame.file4Paste, newDestPaste, currFrame, true);

            return;
        }

        Path source = Paths.get(currFrame.file4Paste.getAbsolutePath().replace("\\", "/"));
        Path dest = Paths.get(newDestPaste.getAbsolutePath().replace("\\", "/"));
        try {
            if (currFrame.file4Paste.isDirectory()) {
                copyDirectory(currFrame.file4Paste, newDestPaste);
            } else {
                Files.copy(source, dest, COPY_OPTION);
            }
        } catch (IOException ex) {
            errorOccurred = true;
            if (copyPressed == true) {
                currFrame.contentScrollPane.errorFrame("Error in copy-paste File: "
                        + currFrame.file4Paste.getName());
            } else {
                currFrame.contentScrollPane.errorFrame("Error in cut-paste File: "
                        + currFrame.file4Paste.getName());
            }
        }

        if (cutPressed == true && (errorOccurred == false)) {
            try {
                if (currFrame.file4Paste.isFile()) {
                    boolean ret = currFrame.file4Paste.delete();
                    if (ret == false)
                        throw new IOException();
                } else {
                    DeleteWarningFrame.deleteDirectory(currFrame.file4Paste);
                }
            } catch (IOException ex) {
                currFrame.contentScrollPane.errorFrame("Error while deleting file!\nTry again.");
            }
        }
        cutPressed = false;
        copyPressed = false;
        pasteItem.setEnabled(false);
        pasteItemp.setEnabled(false);

        currFrame.appContentsRefresh(currFrame.path.replace("\\", "/"));
    }

    private void pasteActions() {
        boolean errorOccurred = false;
        File newDestPaste;

        if (currFrame.path.replace("\\", "/").endsWith("/")) {
            newDestPaste = new File(currFrame.path.replace("\\", "/") + currFrame.file4Paste.getName());
        } else {
            newDestPaste = new File(currFrame.path.replace("\\", "/") + "/" + currFrame.file4Paste.getName());
        }

        if (newDestPaste.exists()) {
            new OverWriteWarningFrame(" Overwrite File: " +
                    newDestPaste.getName(), currFrame.file4Paste, newDestPaste, currFrame, false);
            return;
        }

        Path source = Paths.get(currFrame.file4Paste.getAbsolutePath().replace("\\", "/"));
        Path dest = Paths.get(newDestPaste.getAbsolutePath().replace("\\", "/"));
        try {
            if (currFrame.file4Paste.isDirectory()) {
                copyDirectory(currFrame.file4Paste, newDestPaste);
            } else {
                Files.copy(source, dest, COPY_OPTION);
            }
        } catch (IOException ex) {
            errorOccurred = true;
            if (copyPressed == true) {
                currFrame.contentScrollPane.errorFrame("Error in copy-paste File: "
                        + currFrame.file4Paste.getName());
            } else {
                currFrame.contentScrollPane.errorFrame("Error in cut-paste File: "
                        + currFrame.file4Paste.getName());
            }
        }

        if (cutPressed == true
                && (!currFrame.path.replace("\\", "/").equals(currFrame.file4Paste.getParent().replace("\\", "/")))
                && (errorOccurred == false)) {
            try {
                if (currFrame.file4Paste.isFile()) {
                    boolean ret = currFrame.file4Paste.delete();
                    if (ret == false)
                        throw new IOException();
                } else {
                    DeleteWarningFrame.deleteDirectory(currFrame.file4Paste);
                    currFrame.favPane.removeFromFavourites(currFrame.file4Paste);
                }
            } catch (IOException ex) {
                currFrame.contentScrollPane.errorFrame("Error while deleting file!\nTry again.");
            }
        }
        cutPressed = false;
        copyPressed = false;
        pasteItem.setEnabled(false);
        pasteItemp.setEnabled(false);
        currFrame.appContentsRefresh(currFrame.path.replace("\\", "/"));
    }

    public void copyDirectory(File fileSource, File fileDest) throws IOException {

        if (fileSource != null && fileSource.isDirectory()) {

            if (!fileDest.exists()) {
                Path destPath = Paths.get(fileDest.getAbsolutePath());
                Path srcPath = Paths.get(fileSource.getAbsolutePath());
                Files.copy(srcPath, destPath, COPY_OPTION);
            }

            // dhmiourgia listas me ta periexomena tou directory gia antigrafh,h opoia
            // exei prwtous tous dirs kai meta ta arxeia
            File[] fileListSource = fileSource.listFiles();
            if (fileListSource == null) {
                return;
            }
            File[] filesList1 = fileListSource.clone();
            filesList1 = currFrame.contentScrollPane.getAlphabeticalFolders(filesList1);
            File[] filesList2 = currFrame.contentScrollPane.getAlphabeticalFiles(fileListSource);
            fileListSource = currFrame.contentScrollPane.joinLists(filesList1, filesList2);

            if (fileListSource != null) {
                for (File fSource : fileListSource) {
                    if (fSource == null)
                        continue;
                    if (fSource.isDirectory()) {

                        String src = fSource.getAbsolutePath().replace("\\", "/");
                        Path sourcePath = Paths.get(src);

                        String dst = fileDest.getAbsolutePath().replace("\\", "/") + "/" + fSource.getName();
                        Path destPath = Paths.get(dst);

                        File destFile = new File(dst);

                        if (destFile.exists()) {
                            copyDirectory(fSource, destFile);
                        } else {
                            Files.copy(sourcePath, destPath, COPY_OPTION);
                            copyDirectory(fSource, destFile);
                        }
                    } else {
                        String src = fSource.getAbsolutePath().replace("\\", "/");
                        Path sourcePath = Paths.get(src);

                        String dst = fileDest.getAbsolutePath().replace("\\", "/") + "/" + fSource.getName();
                        Path destPath = Paths.get(dst);
                        File dstF = new File(dst);

                        if (dstF.exists()) {
                            new OverWriteWarningFrame(" Overwrite File: " +
                                    fileDest.getName(), fSource, dstF, currFrame, false);
                            if (dirOverWrite == -1) {
                                dirOverWrite = 0;
                                return;
                            }
                            continue;
                        }

                        Files.copy(sourcePath, destPath, COPY_OPTION);
                    }
                }
            }

        }
    }
}
