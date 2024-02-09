package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import javax.swing.*;

public class ColorMask implements PixelFilter {
    double targetRed, targetGreen, targetBlue, distThreshold;
    public ColorMask(){
        targetRed = Double.parseDouble(JOptionPane.showInputDialog("Enter red value (0-255):"));
        targetGreen = Double.parseDouble(JOptionPane.showInputDialog("Enter green value (0-255):"));
        targetBlue = Double.parseDouble(JOptionPane.showInputDialog("Enter blue value (0-255):"));
        distThreshold = Double.parseDouble(JOptionPane.showInputDialog("Enter difference threshold value (integer):"));
    }

    public ColorMask(int targetRed, int targetGreen, int targetBlue){
        this.targetRed = targetRed;
        this.targetGreen = targetGreen;
        this.targetBlue = targetBlue;
    }
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        short[][] grayscale = img.getBWPixelGrid();

        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                double dist = Math.sqrt(Math.pow(Math.abs(targetRed -red[r][c]),2) + Math.pow(Math.abs(targetGreen -green[r][c]),2) + Math.pow(Math.abs(targetBlue -blue[r][c]),2));
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
