package Filters;

import java.util.ArrayList;

public class PixelCluster {
    int centerRow, centerCol;
    ArrayList<Pixel> pixels = new ArrayList<>();

    public PixelCluster(int centerRow, int centerCol){
        this.centerRow = centerRow;
        this.centerCol = centerCol;
    }

    public void clearCluster(){pixels.clear();}

    public void addPixel(Pixel p){pixels.add(p);}

    public void centerCluster(){
        if(!pixels.isEmpty()) {
            double totalR = 0;
            double totalC = 0;
            for (Pixel p : pixels) {
                totalR += p.row;
                totalC += p.col;
            }
            centerRow = (short) (totalR / pixels.size() - 1);
            centerCol = (short) (totalC / pixels.size() - 1);
        }
    }
}
