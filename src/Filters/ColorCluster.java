package Filters;

import java.util.ArrayList;

public class ColorCluster {
    short centerR, centerG, centerB;
    ArrayList<ColorPoint> ownedColorPoints = new ArrayList<>();

    public ColorCluster(short centerR, short centerG, short centerB){
        this.centerR = centerR;
        this.centerG = centerG;
        this.centerB = centerB;
    }
    public void clearCluster(){
        ownedColorPoints.clear();
    }

    public void addOwnedPoint(ColorPoint p){
        ownedColorPoints.add(p);
    }

    public void centerCluster(){
        if(!ownedColorPoints.isEmpty()) {
            double totalR = 0;
            double totalG = 0;
            double totalB = 0;
            for (ColorPoint p : ownedColorPoints) {
                totalR += p.r;
                totalG += p.g;
                totalB += p.b;
            }
            centerR = (short) (totalR / ownedColorPoints.size() - 1);
            centerG = (short) (totalG / ownedColorPoints.size() - 1);
            centerB = (short) (totalB / ownedColorPoints.size() - 1);
        }
    }
}
