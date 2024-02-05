package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class BallTracking implements PixelFilter {
    ArrayList<PixelFilter> filters = new ArrayList<>();

    public BallTracking(){
        PixelFilter ColorClustering = new ColorMasking();
        filters.add(ColorClustering);
    }

    @Override
    public DImage processImage(DImage img) {
        for (PixelFilter filter : filters) {
            img = filter.processImage(img);
        }

        return img;
    }
}
