package Filters;

import java.util.ArrayList;

public class Pixel {
    int row, col, bwVal;
    public Pixel(int row, int col){
        this.row = row;
        this.col = col;
    }

    public Pixel(int row, int col, int bwVal){
        this.row = row;
        this.col = col;
        this.bwVal = bwVal;
    }

    public PixelCluster findClosestCluster(ArrayList<PixelCluster> pixelClusters){
        double minDist = calcPointToCenterDist(pixelClusters.getFirst());
        PixelCluster closestPixelCluster = pixelClusters.getFirst();
        for (int i = 1; i < pixelClusters.size(); i++) {
            double pDist = calcPointToCenterDist(pixelClusters.get(i));
            if(pDist < minDist){
                minDist = pDist;
                closestPixelCluster = pixelClusters.get(i);
            }
        }

        return closestPixelCluster;
    }

    public double calcPointToCenterDist(PixelCluster c){
        return Math.sqrt(Math.pow(row -c.centerRow,2)+Math.pow(col-c.centerCol,2));
    }
}
