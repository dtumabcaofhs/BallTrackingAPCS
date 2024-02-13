package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import javax.swing.*;
import java.util.ArrayList;

public class ColorClustering implements PixelFilter {
    private int k;
    private ArrayList<ColorCluster> colorClusters = new ArrayList<>();
    private ArrayList<ColorCluster> oldColorClusters = new ArrayList<>();
    private ArrayList<ColorPoint> colorPoints = new ArrayList<>();
    private int timesToCalibrateClusters;

    public ColorClustering(){
        k = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter k (# of color clusters):"));
        timesToCalibrateClusters = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter times to calibrate clusters (Entering 0 will make the clusters calibrate till they can't change anymore):"));
    }

    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        colorPoints = makePoints(red, green, blue);

        colorClusters = initClusters(k);

        if(timesToCalibrateClusters > 0) {
            for (int i = 0; i < timesToCalibrateClusters; i++) {
                clearClusters();
                assignPointsToClusters(colorClusters, colorPoints);
                centerClusters(colorClusters);
            }
        }else{
            oldColorClusters = colorClusters;
            boolean oldClusterNotNewCluster = false;
            while (!oldClusterNotNewCluster) {
                clearClusters();
                assignPointsToClusters(colorClusters, colorPoints);
                centerClusters(colorClusters);
                oldClusterNotNewCluster = areOldNNewClustersSame(oldClusterNotNewCluster);
            }
        }

        reColorImage(red, green, blue);

        img.setColorChannels(red, green, blue);
        return img;
    }

    private void reColorImage(short[][] red, short[][] green, short[][] blue) {
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                ColorPoint p = new ColorPoint(red[r][c],green[r][c],blue[r][c]);

                ColorCluster closest = p.findClosestCluster(colorClusters);
                red[r][c] = closest.centerR;
                green[r][c] = closest.centerG;
                blue[r][c] = closest.centerB;
            }
        }
    }

    private boolean areOldNNewClustersSame(boolean oldClusterNotNewCluster) {
        for(int i = 0; i < colorClusters.size(); i++){
            if(colorClusters.get(i).centerR != oldColorClusters.get(i).centerR){
                oldClusterNotNewCluster = true;
            }if(colorClusters.get(i).centerG != oldColorClusters.get(i).centerG){
                oldClusterNotNewCluster = true;
            }if(colorClusters.get(i).centerB != oldColorClusters.get(i).centerB){
                oldClusterNotNewCluster = true;
            }
        }
        return oldClusterNotNewCluster;
    }

    private ArrayList<ColorPoint> makePoints(short[][] red, short[][] green, short[][] blue) {
        ArrayList<ColorPoint> out = new ArrayList<>();
        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                ColorPoint p = new ColorPoint(red[r][c], green[r][c], blue[r][c]);
                out.add(p);
            }
        }
        return out;
    }

    private void centerClusters(ArrayList<ColorCluster> colorClusters) {
        for(ColorCluster c : colorClusters){
            c.centerCluster();
        }
    }

    private void assignPointsToClusters(ArrayList<ColorCluster> colorClusters, ArrayList<ColorPoint> colorPoints) {
        for(ColorPoint p : colorPoints){
            ColorCluster closestToP = p.findClosestCluster(colorClusters);
            closestToP.addOwnedPoint(p);
            colorClusters.add(closestToP);
        }
    }

    private void clearClusters() {
        for(ColorCluster c : colorClusters){
            c.clearCluster();
        }
    }

    private ArrayList<ColorCluster> initClusters(int k) {
        ArrayList<ColorCluster> out = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            short r = (short) (Math.random()*256);
            short g = (short) (Math.random()*256);
            short b = (short) (Math.random()*256);

            ColorCluster c = new ColorCluster(r,g,b);
            out.add(c);
        }

        return out;
    }
}
