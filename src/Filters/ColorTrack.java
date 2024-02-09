package Filters;

import Interfaces.Drawable;
import Interfaces.Interactive;
import Interfaces.PixelFilter;
import core.DImage;
import processing.core.PApplet;

import java.util.ArrayList;

public class ColorTrack implements PixelFilter, Drawable, Interactive {
    //Made by Dean Tumabcao using D. Dobervich's VideoFilter code.
    private Pixel c;
    private int k = 3, clusterCalibrationAmt = 50;
    private ColorMask selectedColorMask;
    private ArrayList<ColorMask> colorMasks = new ArrayList<>();
    private ArrayList<ColorPoint> targetColorPoints = new ArrayList<>();
    private ArrayList<PixelCluster> pixelClusters = new ArrayList<>();
    private ArrayList<PixelCluster> filledPixelClusters = new ArrayList<>();
    private ArrayList<Pixel> pixels = new ArrayList<>();
    private boolean drawClusterCenter = false;

    public ColorTrack(){
        addTargetColors();
        makeColorMasks();
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] newBW = img.getBWPixelGrid();
        newBW = findClusterCenter(selectedColorMask,img, newBW);
        for(ColorMask c : colorMasks){
            newBW = findClusterCenter(c,img, newBW);
        }

        img.setPixels(newBW);
        return img;
    }

    private void addTargetColors() {
        ColorPoint red = new ColorPoint((short) 255, (short) 0, (short) 0);
        targetColorPoints.add(red);
        ColorPoint green = new ColorPoint((short) 0, (short) 255, (short) 0);
        targetColorPoints.add(green);
        ColorPoint blue = new ColorPoint((short) 0, (short) 0, (short) 255);
        targetColorPoints.add(blue);
        ColorPoint yellow = new ColorPoint((short) 255, (short) 255, (short) 0);
        targetColorPoints.add(yellow);
        ColorPoint orange = new ColorPoint((short) 255, (short) 127, (short) 0);
        targetColorPoints.add(orange);
    }

    private void makeColorMasks() {
        for(ColorPoint c : targetColorPoints){
            ColorMask colorMask = new ColorMask(c.r,c.g,c.b);
            System.out.println("Color mask"+c.r+" "+c.g+" "+c.b+" ");
            colorMasks.add(colorMask);
        }
    }

    private short[][] findClusterCenter(ColorMask colorMask, DImage img, short[][] newBW){
        if(colorMask != null){
            img = colorMask.processImage(img);
            clusterPixels(img);

            short[][] newImg = img.getBWPixelGrid();

            for (int r = 0; r < newImg.length; r++) {
                for (int c = 0; c < newImg[r].length; c++) {
                    if(newImg[r][c] == 255){
                        newBW[r][c] = 255;
                    }
                }
            }
        }

        return newBW;
    }


    private void clusterPixels(DImage img){
        pixels.clear();
        pixelClusters.clear();

        short[][] BW = img.getBWPixelGrid();

        makePixels(BW);

        makeClusters(k,BW);

        if(clusterCalibrationAmt > 0) {
            for (int i = 0; i < clusterCalibrationAmt; i++) {
                clearClusters();
                assignPixelsToClusters();
                centerClusters();
            }
        }

        for(PixelCluster p : pixelClusters){
            if(!p.pixels.isEmpty()){
                filledPixelClusters.add(p);
            }
        }

        drawClusterCenter = true;
    }

    private void makeClusters(int k, short[][] bw) {
        for (int i = 0; i < k; i++) {
            int row = (int) (Math.random()*bw.length);
            int col = (int) (Math.random()*bw[0].length);

            PixelCluster c = new PixelCluster(row,col);
            pixelClusters.add(c);
        }
    }

    private void makePixels(short[][] bw){
        for (int r = 0; r < bw.length; r++) {
            for (int c = 0; c < bw[r].length; c++) {
                Pixel p = new Pixel(r, c, bw[r][c]);
                pixels.add(p);
            }
        }
    }

    private void clearClusters(){
        for(PixelCluster p : pixelClusters){
            p.clearCluster();
        }
    }

    private void assignPixelsToClusters() {
        for (Pixel pi : pixels) {
            if (pi.bwVal == 255) {
                for(PixelCluster p : pixelClusters) {
                    if (p == pi.findClosestCluster(pixelClusters)) {
                        p.addPixel(pi);
                    }
                }
            }
        }
    }

    private void centerClusters(){
        for (PixelCluster p : pixelClusters) {
            p.centerCluster();
        }
    }

    @Override
    public void drawOverlay(PApplet window, DImage original, DImage filtered) {
        for(PixelCluster c : filledPixelClusters){
            window.fill(255,0,0);
            window.ellipse(c.centerCol - 10, c.centerRow - 10, 20, 20);
        }
        filledPixelClusters.clear();
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

                if(selectedColorMask == null){
                    selectedColorMask = new ColorMask(r,g,b);
                }else{
                    selectedColorMask.targetRed = r;
                    selectedColorMask.targetGreen = g;
                    selectedColorMask.targetBlue = b;
                }
            }
        }
    }

    @Override
    public void keyPressed(char key) {
        if(key == 'E' || key == 'e'){
            if(selectedColorMask != null){
            selectedColorMask.distThreshold++;
            }
            for(ColorMask c : colorMasks){
                c.distThreshold++;
            }
        }if(key == 'D' || key == 'd'){
            if(selectedColorMask != null) {
            selectedColorMask.distThreshold--;
            }
            for(ColorMask c : colorMasks){
                c.distThreshold--;
            }
        }

        if(key == ' '){
            selectedColorMask = null;
        }
    }
}
