package Filters;

import Interfaces.PixelFilter;
import core.DImage;

import java.util.ArrayList;

public class BallTrack implements PixelFilter {
    ArrayList<PixelFilter> filters = new ArrayList<>();

    public BallTrack(){
        PixelFilter colorMask = new ColorMask();
        filters.add(colorMask);
        PixelFilter clustering = new ColorClustering();
        //filters.add(clustering);
    }

    @Override
    public DImage processImage(DImage img) {
        for (PixelFilter filter : filters) {
            img = filter.processImage(img);
        }

        return img;
    }
}
