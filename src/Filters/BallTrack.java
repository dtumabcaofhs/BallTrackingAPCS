package Filters;

import Interfaces.Drawable;
import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;
import processing.core.PApplet;

public class BallTrack implements PixelFilter, Drawable, Interactive {
    ArrCoordinate c;
    ColorMask colorMask;

    @Override
    public DImage processImage(DImage img) {
        DImage newImg = img;
        /*for (PixelFilter filter : filters) {
            img = filter.processImage(img);
        }*/
        if(colorMask != null){
            newImg = colorMask.processImage(newImg);
            //System.out.println(colorMask.distThreshold);
            //System.out.println(colorMask.rT+","+colorMask.gT+","+colorMask.bT);
            findCenter(newImg);
        }

        return newImg;
    }

    public void findCenter(DImage img){
        short[][] BW = img.getBWPixelGrid();

        int rowTotal = 0, colTotal = 0, rowCount = 0, colCount = 0;
        for (int r = 0; r < BW.length; r++) {
            for (int c = 0; c < BW[r].length; c++) {
                if(BW[r][c] == 255){
                    rowTotal+=r;
                    colTotal+=c;
                    rowCount++;
                    colCount++;
                }
            }
        }
        int centerR = 0, centerC = 0;
        if(rowCount > 0) {
            centerR = rowTotal / rowCount;
        }if(colCount > 0) {
            centerC = colTotal / colCount;
        }

        c = new ArrCoordinate(centerR,centerC);
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        if(c != null) {
            window.fill(255,0,0);
            window.ellipse(c.c - 10, c.r - 10, 20, 20);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, DImage img) {
        int r, g, b;
        if(mouseX >= 0 && mouseX <= img.getWidth()){
            if(mouseY >= 0 && mouseY <= img.getHeight()){
                short[][] red = img.getRedChannel();
                r = red[mouseY][mouseX];
                short[][] green = img.getGreenChannel();
                g = green[mouseY][mouseX];
                short[][] blue = img.getBlueChannel();
                b = blue[mouseY][mouseX];

                if(colorMask == null){
                    colorMask = new ColorMask(r,g,b);
                }else{
                    colorMask.rT = r;
                    colorMask.gT = g;
                    colorMask.bT = b;
                }
            }
        }
    }

    @Override
    public void keyPressed(char key) {
        if(key == 'E' || key == 'e'){
            colorMask.distThreshold++;
        }if(key == 'D' || key == 'd'){
            colorMask.distThreshold--;
        }
    }
}
