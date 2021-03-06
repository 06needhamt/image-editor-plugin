/*
 * Lightweight Image Editor Plugin For Intellij 14
 * Copyright (c) 2015 Thomas Needham
 */

package root.tom.needham.imageeditor;

import com.intellij.openapi.project.Project;
import root.tom.needham.imageeditor.ImageEditor;
import root.tom.needham.imageeditor.ImageFileFilter;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Thomas Needham on Mon Apr 27 17:21:39 BST 2015
 * @author Thomas Needham
 */

public class MainForm extends JFrame {
    public JPanel rootPanel;
    public JMenuBar menuStrip;
    public JMenu fileMenu;
    public JMenuItem openImageItem;
    public JMenuItem closeImageItem;
    public JMenuItem saveImageItem;
    public JMenuItem saveAsItem;
    public JMenuItem exitItem;
    public JMenu aboutMenu;
    public JMenuItem aboutThisPluginItem;
    public JMenu editMenu;
    public JMenuItem resizeItem;
    public JMenuItem androidItem;
    public JLabel imagePane;
    public ImageEditor editor;
    public JMenuItem convertItem;
    public JMenuItem rotateItem;
    public String openfile;

    public MainForm(ImageEditor editor) {
        this.editor = editor;
        this.setTitle("Lightweight Image Editor");
        initComponents();
    }

    private void initComponents() {
        createUI();
    }

    private void createUI() {
        rootPanel = new JPanel();
        menuStrip = new JMenuBar();
        fileMenu = new JMenu();
        openImageItem = new JMenuItem();
        closeImageItem = new JMenuItem();
        saveImageItem = new JMenuItem();
        exitItem = new JMenuItem();
        aboutMenu = new JMenu();
        editMenu = new JMenu();
        resizeItem = new JMenuItem();
        androidItem = new JMenuItem();
        aboutThisPluginItem = new JMenuItem();
        imagePane = new JLabel();
        convertItem = new JMenuItem();
        rotateItem = new JMenuItem();

        //======== this ========
        setLayout(new BorderLayout());

        //======== rootPanel ========
        {
            rootPanel.setLayout(null);

            //======== menuStrip ========
            {

                //======== fileMenu ========
                {
                    fileMenu.setText("File");

                    //---- openImageItem ----
                    openImageItem.setText("Open Image");
                    openImageItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            OpenFileDialog();
                        }
                    });
                    fileMenu.add(openImageItem);

                    //---- closeImageItem ----
                    closeImageItem.setText("Close Image");
                    closeImageItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SaveBeforeClose(false);
                        }
                    });
                    fileMenu.add(closeImageItem);

                    //---- saveImageItem ----
                    saveImageItem.setText("Save Image");
                    saveImageItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SaveFileDialog();
                        }
                    });
                    fileMenu.add(saveImageItem);

                    //---- exitItem ----
                    exitItem.setText("Exit");
                    exitItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SaveBeforeClose(true);
                        }
                    });
                    fileMenu.add(exitItem);


                }
                menuStrip.add(fileMenu);

                //======== editMemu ========

                {
                    editMenu.setText("Edit");

                    //---- resizeItem ----
                    resizeItem.setText("Resize Image");
                    resizeItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            ResizeImage();
                        }
                    });
                    editMenu.add(resizeItem);

                    //---- androidItem ----
                    androidItem.setText("Resize Image for Android");
                    androidItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            AndroidResize();
                        }
                    });
                    editMenu.add(androidItem);

                    //---- convertItem ----
                    convertItem.setText("Convert Image");
                    convertItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final String[] FILE_FORMATS = new String[]{".png", ".jpg"};
                            final String format = (String) JOptionPane.showInputDialog(null, "Select a format",
                                    "Select a format", JOptionPane.QUESTION_MESSAGE, new ImageIcon(), FILE_FORMATS, FILE_FORMATS[0]);
                            ConvertImage(format);
                        }
                    });
                    editMenu.add(convertItem);
                    rotateItem.setText("Rotate Image");
                    rotateItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            final double degrees = Double.parseDouble(JOptionPane.showInputDialog(null,"Enter Degrees to rotate"));
                            try {
                                ImageIcon icon = new ImageIcon(rotateImage(degrees));
                                imagePane.setIcon(icon);
                            }
                            catch (NullPointerException ex){
                                openfile = "";
                                ex.printStackTrace();
                            }
                        }
                    });
                    editMenu.add(rotateItem);
                }
                menuStrip.add(editMenu);
                //======== aboutMenu ========
                {
                    aboutMenu.setText("About");

                    //---- aboutThisPluginItem ----
                    aboutThisPluginItem.setText("About This Plugin");
                    aboutThisPluginItem.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            DisplayAbout();
                        }
                    });
                    aboutMenu.add(aboutThisPluginItem);
                }
                menuStrip.add(aboutMenu);
            }
            rootPanel.add(menuStrip);
            menuStrip.setBounds(0, 0, 395, menuStrip.getPreferredSize().height);
            rootPanel.add(imagePane);
            imagePane.setBounds(5, 30, 390, 230);

            { // compute preferred size
                Dimension preferredSize = new Dimension();
                for (int i = 0; i < rootPanel.getComponentCount(); i++) {
                    Rectangle bounds = rootPanel.getComponent(i).getBounds();
                    preferredSize.width = Math.max(bounds.x + bounds.width, preferredSize.width);
                    preferredSize.height = Math.max(bounds.y + bounds.height, preferredSize.height);
                }
                Insets insets = rootPanel.getInsets();
                preferredSize.width += insets.right;
                preferredSize.height += insets.bottom;
                rootPanel.setMinimumSize(preferredSize);
                rootPanel.setPreferredSize(preferredSize);

            }
        }
        add(rootPanel, BorderLayout.CENTER);
        this.pack();
    }

    private void ConvertImage(String format) {
        ImageIcon icon = null;
        BufferedImage bi = null;
        Graphics g = null;
        if(imagePane.getIcon() == null){
            return;
        }
        icon = (ImageIcon) imagePane.getIcon();
        if(format.equals(".jpg")) {
            bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            g = bi.createGraphics();
        }
        else if(format.equals(".png")){
            bi = new BufferedImage(icon.getIconWidth(),icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            g = bi.createGraphics();
        }
        if (bi != null) {
            Image image = bi.getScaledInstance(bi.getWidth(),bi.getHeight(),Image.SCALE_DEFAULT);
            SaveFile(new File(openfile));
        }
        else{
            return;
        }
    }

    private void AndroidResize() {
        AndroidFileResizer afr = new AndroidFileResizer(this);
        afr.resizeImage();
    }

    private void ResizeImage() {
        int width;
        int height;
        if(imagePane.getIcon() == null){
            return;
        }
        try {

            width = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter image width"));
            height = Integer.parseInt(JOptionPane.showInputDialog(this, "Enter image Height"));
        }
        catch (NumberFormatException ex){
            ex.printStackTrace();
            return;
        }
        ImageIcon icon = (ImageIcon) imagePane.getIcon();
        BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
        icon.paintIcon(null, g, 0, 0);
        g.dispose();
        Image image = bi.getScaledInstance(width,height,Image.SCALE_DEFAULT);
        ImageIcon newicon = new ImageIcon(image);
        imagePane.setIcon(newicon);


    }

    private void DisplayAbout() {
        JOptionPane.showConfirmDialog(this,"Lightweight image editor plugin for intellij" + "\n" + "Copyright Thomas Needham 2015","About this plugin",JOptionPane.DEFAULT_OPTION);
    }

    private void SaveBeforeClose(boolean exiting) {
        if(imagePane.getIcon() == null){
            if(exiting){
                this.dispose();
            }
            return;
        }
        int result = JOptionPane.showConfirmDialog(this,"Do you want to save your changes?","Save Changes",JOptionPane.YES_NO_CANCEL_OPTION);
        if(result == JOptionPane.YES_OPTION){
            SaveFileDialog();
        }
        else if(result == JOptionPane.CANCEL_OPTION){
            return;
        }

        imagePane.setText("");
        imagePane.setIcon(null);
        openfile = "";
        if(exiting){
            this.dispose();
        }

    }

    private void SaveFileDialog() {
        Project proj = editor.event.getProject();
        if(proj != null){
            final JFileChooser jfc = new JFileChooser(proj.getBasePath());
            jfc.addChoosableFileFilter(new ImageFileFilter());
            int returnval = jfc.showSaveDialog(this);
            if(returnval == JFileChooser.APPROVE_OPTION){
                SaveFile(jfc.getSelectedFile());
            }
        }
    }

    private void SaveFile(File f) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(f,false);
            String filetype = openfile.substring(openfile.length() - 3, openfile.length());
            ImageIcon icon = (ImageIcon) imagePane.getIcon();
            BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.createGraphics();
        // paint the Icon to the BufferedImage.
            icon.paintIcon(null, g, 0,0);
            ImageIO.write(bi, filetype, fileOutputStream);
            g.dispose();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void OpenFileDialog() {

        Project proj = editor.event.getProject();
        if(proj != null){
            final JFileChooser jfc = new JFileChooser(proj.getBasePath());
            jfc.addChoosableFileFilter(new ImageFileFilter());
            int returnval = jfc.showOpenDialog(this);
           if(returnval == JFileChooser.APPROVE_OPTION){
               ReadFrle(jfc.getSelectedFile());
           }
        }
    }

    private void ReadFrle(File f) {
        FileInputStream inputStream;
        if(f == null){
            return;
        }
        openfile = f.getAbsolutePath();
        try {
            inputStream = new FileInputStream(f);
            byte[] bytes = new byte[(int)f.length()];
            inputStream.read(bytes);
            imagePane.setIcon(new ImageIcon(bytes));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean openImage(byte[] bytes){
        imagePane.setIcon(new ImageIcon(bytes));
        this.repaint();
        return true;
    }

    public BufferedImage rotateImage(double degrees){
        try {
            String filetype = openfile.substring(openfile.length() - 3, openfile.length());
            int imagetype = 0;
            if (filetype.equals("jpg"))
                imagetype = BufferedImage.TYPE_INT_RGB;
            else if (filetype.equals("png"))
                imagetype = BufferedImage.TYPE_INT_ARGB;
            else {JOptionPane.showMessageDialog(this,"Unsupported file type"); return null;}
            BufferedImage image = new BufferedImage(imagePane.getWidth(), imagePane.getHeight(), imagetype);
            double rotation = Math.toRadians(degrees);
            double locX = imagePane.getWidth() / 2;
            double locY = imagePane.getHeight() / 2;
            AffineTransform transform = new AffineTransform();
            transform.rotate(rotation,locX,locY);
            AffineTransformOp transformOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
            BufferedImage dest = new BufferedImage(imagePane.getWidth(), imagePane.getHeight(), imagetype);
            return transformOp.filter(image,dest);
        }
        catch (NullPointerException ex){
            ex.printStackTrace();
            JOptionPane.showConfirmDialog(this,"No image open", "Error",JOptionPane.DEFAULT_OPTION);
            return null;
        }
    }

}
