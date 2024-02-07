package Filters;

import java.util.ArrayList;

public class ColorPoint {
    short r, g, b;

    public ColorPoint(short r, short g, short b){
        this.r = r;
        this.g = g;
        this.b = b;
    }
    public ColorCluster findClosestCluster(ArrayList<ColorCluster> colorClusters){
        double minDist = calcPointToCenterDist(colorClusters.getFirst());
        ColorCluster closestColorCluster = colorClusters.getFirst();
        for (int i = 1; i < colorClusters.size(); i++) {
            double pDist = calcPointToCenterDist(colorClusters.get(i));
            if(pDist < minDist){
                minDist = pDist;
                closestColorCluster = colorClusters.get(i);
            }
        }

        return closestColorCluster;
    }

    public double calcPointToCenterDist(ColorCluster c){
        return Math.sqrt(Math.pow(r-c.centerR,2)+Math.pow(g-c.centerG,2)+Math.pow(b-c.centerB,2));
    }
}
