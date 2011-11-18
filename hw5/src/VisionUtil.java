import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;


class VisionUtil {

    public static int[][] init(int size) {
        int[][] new_image = new int[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                new_image[i][j] = 0;
            }
        }
        return new_image;
    }

    public static int[][] read(String file, int size) throws IOException {
        FileReader inputStream = new FileReader(file);
        int[][] image = init(size);

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                /* internally background is represented as 0 and foreground as 1
                but when reading from raw image background is 255 and foreground is 0 */
                image[i][j] = (inputStream.read() == 0) ? 1 : 0;
            }
        }
        inputStream.close();
        return image;
    }

    public static BufferedImage getBufferedImage(int[][] image) throws IOException {
        int size = image.length;
         BufferedImage bufferedImage = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
 
         for(int i = 0; i < size; i++) {
             for(int j = 0; j < size; j++) {
                 /*
                 notes on creating buffered image:
                 -setRGB is column major order
                 -internally background is represented as 0 and foreground as 1
                 but when writing to image background is 0xff and foreground is 0
                 */
                 bufferedImage.setRGB(j, i, (image[i][j] == 1 ? 0 : (byte)0xff));
             }
         }
 
         return bufferedImage;
     }

    public static int[][] center(int[][] image) {
        int size = image.length;
        int[] center = getCenter(image);

        int offset_x = (size/2) - center[0];
        int offset_y = (size/2) - center[1];

        int[][] translated_image = init(size);

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                try {
                    translated_image[i+offset_x][j+offset_y] = image[i][j];
                } catch(Exception e) {}
            }
        }

        return translated_image;
    }

    public static int[] getCenter(int[][] image){
        int[] bbox = getBoundingBox(image);
        int center_x = (bbox[0] + bbox[2]) / 2;
        int center_y = (bbox[1] + bbox[3]) / 2;

        int[] center = {center_x, center_y};
        return center;
    }

    public static int[] getBoundingBox(int[][] image){
        int size = image.length;
        int min_x = Integer.MAX_VALUE;
        int max_x = 0;
        int min_y = Integer.MAX_VALUE;
        int max_y = 0;

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(image[i][j] == 1) {
                    if(i > max_y) {
                        max_y = i;
                    }
                    if(i < min_y) {
                        min_y = i;
                    }
                    if(j > max_x) {
                        max_x = j;
                    }
                    if(j < min_x) {
                        min_x = j;
                    }
                }
            }
        }

        int[] bounding_box = new int[4];
        bounding_box[0] = min_x;
        bounding_box[1] = min_y;
        bounding_box[2] = max_x;
        bounding_box[3] = max_y;
        return bounding_box;
    }

    public static int[][] dilate(int[][] image, int n) {
        int[][] dilated_image = image;

        for(int i = 0; i < n; i++) {
            if (i%2 == 0) {
                dilated_image = dilate4(dilated_image);
            }
            else {
                dilated_image = dilate8(dilated_image);
            }
        }

        return dilated_image;
    }

    public static int[][] dilate4(int[][] image) {
        int size = image.length;
        int[][] dilated_image = init(size);

        for(int i = 1; i < size-1; i++) {
            for(int j = 1; j < size-1; j++) {
                if(     image[i][j]   == 1 ||
                        image[i-1][j] == 1 ||
                        image[i+1][j] == 1 ||
                        image[i][j-1] == 1 ||
                        image[i][j+1] == 1) {
                    dilated_image[i][j] = 1;
                }
            }
        }

        return dilated_image;
    }

    public static int[][] dilate8(int[][] image) {
        int size = image.length;
        int[][] dilated_image = init(size);

        for(int i = 1; i < size-1; i++) {
            for(int j = 1; j < size-1; j++) {
                if(     image[i-1][j-1]   == 1 ||
                        image[i][j-1] == 1 ||
                        image[i+1][j-1] == 1 ||
                        image[i-1][j] == 1 ||
                        image[i][j] == 1 ||
                        image[i+1][j] == 1 ||
                        image[i-1][j+1] == 1 ||
                        image[i][j+1] == 1 ||
                        image[i+1][j+1] == 1) {
                    dilated_image[i][j] = 1;
                }
            }
        }

        return dilated_image;
    }

    public static int[][] erode(int[][] image, int n) {
        int[][] eroded_image = image;

        for(int i = 0; i < n; i++) {
            if (i%2 == 0) {
                eroded_image = erode4(eroded_image);
            }
            else {
                eroded_image = erode8(eroded_image);
            }
        }

        return eroded_image;
    }

    public static int[][] erode4(int[][] image) {
        int size = image.length;
        int[][] eroded_image = init(size);

        for(int i = 1; i < size-1; i++) {
            for(int j = 1; j < size-1; j++) {
                if(     image[i][j]   == 1 &&
                        image[i-1][j] == 1 &&
                        image[i+1][j] == 1 &&
                        image[i][j-1] == 1 &&
                        image[i][j+1] == 1) {
                    eroded_image[i][j] = 1;
                }
            }
        }

        return eroded_image;
    }

    public static int[][] erode8(int[][] image) {
        int size = image.length;
        int[][] eroded_image = init(size);

        for(int i = 1; i < size-1; i++) {
            for(int j = 1; j < size-1; j++) {
                if(     image[i-1][j-1]   == 1 &&
                        image[i][j-1] == 1 &&
                        image[i+1][j-1] == 1 &&
                        image[i-1][j] == 1 &&
                        image[i][j] == 1 &&
                        image[i+1][j] == 1 &&
                        image[i-1][j+1] == 1 &&
                        image[i][j+1] == 1 &&
                        image[i+1][j+1] == 1) {
                    eroded_image[i][j] = 1;
                }
            }
        }

        return eroded_image;
    }

    public static int[][] open(int[][] image, int n) {
        return dilate(erode(image, n), n);
    }

    public static int[][] close(int[][] image, int n) {
        return erode(dilate(image, n), n);
    }

    public static int[][] diff(int[][] image_a, int[][] image_b) {
        int size = image_a.length;
        int[][] diff_image = init(size);

        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                if(image_a[i][j] == 1 && image_b[i][j] != 1) {
                    diff_image[i][j] = 1;
                }
            }
        }

        return diff_image;
    }
}
