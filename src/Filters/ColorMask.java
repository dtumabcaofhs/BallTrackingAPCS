package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import javax.swing.*;

public class ColorMask implements PixelFilter {
    double rT, gT, bT, distThreshold;
    public ColorMask(){
        rT = Double.parseDouble(JOptionPane.showInputDialog("Enter red value (0-255):"));
        gT = Double.parseDouble(JOptionPane.showInputDialog("Enter green value (0-255):"));
        bT = Double.parseDouble(JOptionPane.showInputDialog("Enter blue value (0-255):"));
        distThreshold = Double.parseDouble(JOptionPane.showInputDialog("Enter difference threshold value (integer):"));
    }

    public ColorMask(int r, int g, int b){
        rT = r;
        gT = g;
        bT = b;
    }
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        short[][] grayscale = img.getBWPixelGrid();

        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                double dist = Math.sqrt(Math.pow(Math.abs(rT-red[r][c]),2) + Math.pow(Math.abs(gT-green[r][c]),2) + Math.pow(Math.abs(bT-blue[r][c]),2));
                if(dist > distThreshold){
                    grayscale[r][c] = 0;
                }else{
                    grayscale[r][c] = 255;
                }
            }
        }

        img.setPixels(grayscale);
        return img;
    }
}
