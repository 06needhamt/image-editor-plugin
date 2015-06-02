/*
 * Lightweight Image Editor Plugin For Intellij 14
 * Copyright (c) 2015 Thomas Needham
 */

package root.tom.needham.imageeditor;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Tom on 28/04/2015.
 */
public class AndroidFileResizer implements IAppConstants{

    MainForm form;
    public AndroidFileResizer(MainForm form){
        this.form = form;
        resizeImage();
    }

    public void resizeImage() {
        for (int i = 0; i < ANDROID_SIZES.length; i++) {
            ImageIcon icon = (ImageIcon) form.imagePane.getIcon();
            BufferedImage bi = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.createGraphics();
            // paint the Icon to the BufferedImage.
            icon.paintIcon(null, g, 0, 0);
            g.dispose();
            Image image = bi.getScaledInstance(ANDROID_SIZES[i], ANDROID_SIZES[i], Image.SCALE_DEFAULT);
            BufferedImage bufferedImage = toBufferedImage(image);
            try {

                File outputdir = new File(form.editor.event.getProject().getBasePath() + "/Android/");
                if(!outputdir.exists()) {
                    outputdir.mkdir();
                }
                File outputfile = new File(outputdir.getAbsolutePath() + "/" + ANDROID_SIZE_NAMES[i] + ".png");
                ImageIO.write(bufferedImage,"png", outputfile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Converts a given Image into a BufferedImage
     *
     * @param img The Image to be converted
     * @return The converted BufferedImage
     */
    public static BufferedImage toBufferedImage(Image img)
    {
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
