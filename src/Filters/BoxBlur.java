package Filters;

import Interfaces.PixelFilter;
import core.DImage;

public class BoxBlur implements PixelFilter {
    @Override
    public DImage processImage(DImage img) {
        short[][] red = img.getRedChannel();
        short[][] green = img.getGreenChannel();
        short[][] blue = img.getBlueChannel();

        for (int r = 0; r < red.length; r++) {
            for (int c = 0; c < red[r].length; c++) {
                int totalR = 0, totalG = 0, totalB = 0;
                int pixelCount = 0;
                for (int i = r-1; i < r+3; i++) {
                    for (int j = c-1; j < c+3; j++) {
                        if(i >= 0 && i < red.length && j >= 0 && j < red[0].length){
                            totalR+=red[r][c];
                            totalG+=green[r][c];
                            totalB+=blue[r][c];
                            pixelCount++;
                        }
                    }
                }
                red[r][c] = (short) (totalR/pixelCount);
                green[r][c] = (short) (totalG/pixelCount);
                blue[r][c] = (short) (totalB/pixelCount);
            }
        }
        img.setColorChannels(red,green,blue);
        return img;
    }
}
