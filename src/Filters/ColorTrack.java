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
    private int k = 3, clusterCalibrationAmt = 10;
    private ColorMask selectedColorMask;
    private ArrayList<ColorMask> colorMasks = new ArrayList<>();
    private ArrayList<ColorPoint> targetColorPoints = new ArrayList<>();
    private ArrayList<PixelCluster> pixelClusters = new ArrayList<>();
    private ArrayList<PixelCluster> filledPixelClusters = new ArrayList<>();
    private ArrayList<Pixel> pixels = new ArrayList<>();
    private boolean drawClusterCenter = false;
    DImage coloredOverlay;

    public ColorTrack(){
        addTargetColors();
        makeColorMasks();
    }

    private void addTargetColors() {
        ColorPoint red = new ColorPoint((short) 154, (short) 110, (short) 89);
        targetColorPoints.add(red);
        ColorPoint green = new ColorPoint((short) 77, (short) 132, (short) 64);
        targetColorPoints.add(green);
        ColorPoint blue = new ColorPoint((short) 5, (short) 30, (short) 110);
        targetColorPoints.add(blue);
        ColorPoint yellow = new ColorPoint((short) 253, (short) 231, (short) 130);
        targetColorPoints.add(yellow);
    }

    private void makeColorMasks() {
        for(ColorPoint c : targetColorPoints){
            ColorMask colorMask = new ColorMask(c.r,c.g,c.b);
            System.out.println("DEV: Added color mask: ["+c.r+","+c.g+","+c.b+"]");
            colorMasks.add(colorMask);
        }
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] newBW = img.getBWPixelGrid();

        short[][] newMaskR = img.getRedChannel();
        short[][] newMaskG = img.getGreenChannel();
        short[][] newMaskB = img.getBlueChannel();

        img = findColorCentersMakeNewImg(selectedColorMask,img, newMaskR,newMaskG,newMaskB);
        for(ColorMask c : colorMasks){
            img = findColorCentersMakeNewImg(c,img, newMaskR,newMaskG,newMaskB);
        }

        return img;
    }

    private DImage findColorCentersMakeNewImg(ColorMask colorMask, DImage img, short[][] newMaskR, short[][] newMaskG, short[][] newMaskB){
        if(colorMask != null){
            img = colorMask.processImage(img);
            clusterPixels(img);

            short[][] newImg = img.getBWPixelGrid();

            for (int r = 0; r < newImg.length; r++) {
                for (int c = 0; c < newImg[r].length; c++) {
                    if(newImg[r][c] == 255){
                        newMaskR[r][c] = (short) colorMask.targetRed;
                        newMaskG[r][c] = (short) colorMask.targetGreen;
                        newMaskB[r][c] = (short) colorMask.targetBlue;
                    }
                }
            }
        }

        return img;
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
