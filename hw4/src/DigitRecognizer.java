package vision.hw4;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


class DigitRecognizer {

    private static int SIZE = 128;

    public static int[][] image;
    public static List<Integer> parent_arr;
    public static int[][] label_image;

    public static DRView view;

    public static void main(String[] args) throws Exception {
        int[][] image_a = init();
        int[][] image_b = init();
        int[][] image_c = init();
        int[][] image_d = init();
        int[][] image_e = init();

        image_a = read("six.raw");
        image_a = center(image_a);
        image_b = close(image_a, 26);
        image_c = dilate(erode(diff(image_b, image_a)));

        labelHoles(image_c);

        for(int i = 1; i < parent_arr.size(); i++){
            int[][] subimage = init();
            if(parent_arr.get(i) == 0){
                subimage = getHole(label_image, i); 
                image_d = diff(dilate(subimage), image_a);
                image_e = diff(image_d, subimage);
                
                //remove holes with area less than 2
//                image_e = clean(image_e, 2);
                
                for(int j = 0; j < SIZE; j++){
                    for(int k = 0; k < SIZE; k++){
                        System.out.print(image_e[j][k]);
                    }
                    System.out.println();
                }
               System.out.println(); 
               System.out.println(); 
            }
        }


       // view = new DRView(image);
      //  view.setup();

     //   view.updateImage(image_e);

        /*
                for(int i = 0; i < SIZE; i++){
                    for(int j = 0; j < SIZE; j++){
                        System.out.print(image[i][j]);
                    }
                    System.out.println();
                }
                */
    }

    public static int[][] init() {
        int[][] new_image = new int[SIZE][SIZE];
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                new_image[i][j] = 0;
            }
        }
        return new_image;
    }

    public static int[][] read(String file) throws IOException {
        FileReader inputStream = new FileReader(file);
        int[][] image = init();

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                /* internally background is represented as 0 and foreground as 1
                but when reading from raw image background is 255 and foreground is 0 */
                image[i][j] = (inputStream.read() == 0) ? 1 : 0;
            }
        }
        inputStream.close();
        return image;
    }

    public static int[][] center(int[][] image){
        int min_x = Integer.MAX_VALUE;
        int max_x = 0;
        int min_y = Integer.MAX_VALUE;
        int max_y = 0;

        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(image[i][j] == 1){
                    if(i > max_y){
                        max_y = i;
                    }
                    if(i < min_y){
                        min_y = i;
                    }
                    if(j > max_x){
                        max_x = j;
                    }
                    if(j < min_x){
                        min_x = j;
                    }
                }
            }
        }

        int center_x = (max_x + min_x) / 2;
        int center_y = (max_y + min_y) / 2;

        int offset_x = (SIZE/2) - center_x;
        int offset_y = (SIZE/2) - center_y;

        int[][] translated_image = init();

        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                try{
                    translated_image[i+offset_x][j+offset_y] = image[i][j];
                }catch(Exception e){}
            }
        }

        return translated_image;
    }

    public static int[][] dilate(int[][] image, int n) {
        int[][] dilated_image = image;

        for(int i = 0; i < n; i++) {
            if (i%2 == 0) {
                dilated_image = dilate(dilated_image);
            }
            else {
                dilated_image = dilate8(dilated_image);
            }
        }

        return dilated_image;
    }

    public static int[][] dilate(int[][] image) {
        int[][] dilated_image = init();

        for(int i = 1; i < SIZE-1; i++) {
            for(int j = 1; j < SIZE-1; j++) {
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
        int[][] dilated_image = init();

        for(int i = 1; i < SIZE-1; i++) {
            for(int j = 1; j < SIZE-1; j++) {
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
                eroded_image = erode(eroded_image);
            }
            else {
                eroded_image = erode8(eroded_image);
            }
        }

        return eroded_image;
    }

    public static int[][] erode(int[][] image) {
        int[][] eroded_image = init();

        for(int i = 1; i < SIZE-1; i++) {
            for(int j = 1; j < SIZE-1; j++) {
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
        int[][] eroded_image = init();

        for(int i = 1; i < SIZE-1; i++) {
            for(int j = 1; j < SIZE-1; j++) {
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
        int[][] diff_image = init();

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image_a[i][j] == 1 && image_b[i][j] != 1) {
                    diff_image[i][j] = 1;
                }
            }
        }

        return diff_image;
    }

    public static void labelHoles(int[][] image_a) {
        label_image = init();
        int label = 1;
        parent_arr = new ArrayList<Integer>();
        parent_arr.add(0);

        //1st pass - for each cell in the grid
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image_a[i][j] == 1) {

                    int[] pn = prior_neighbors(i, j, label_image);

                    //if pn is empty min = ++label
                    boolean empty = true;
                    int min = label;
                    for(int k = 0; k < 4; k++) {
                        int x= pn[k];
                        if (x != 0) {
                            empty = false;
                            if (x < min) {
                                min = x;
                            }
                        }
                    }
                    if(empty) {
                        parent_arr.add(0);
                        label++;
                    }

                    //assign label
                    label_image[i][j] = min;

                    //union labels
                    for(int k = 0; k < 4; k++) {
                        int x = pn[k];
                        if (x != 0 && x != min) {
                            union(min, x, parent_arr);
                        }
                    }
                }
            }
        }

        //2nd pass - for each cell in the grid
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image_a[i][j] == 1) {
                    label_image[i][j] = find(label_image[i][j], parent_arr);
                }
            }
        }

    }

    public static int find(int i, List<Integer> parent_arr) {
        while (parent_arr.get(i) != 0) {
            i = parent_arr.get(i);
        }
        return i;
    }

    public static void union(int x, int y, List<Integer> parent_arr) {
        int j = find(x, parent_arr);
        int k = find(y, parent_arr);
        if (j != k) {
            parent_arr.set(k, j);
        }
    }

    public static int[] prior_neighbors(int i, int j, int[][] cells) {
        //prior neighbors
        int[] prior_neighbors = new int[4];

        prior_neighbors[0] = 0;
        prior_neighbors[1] = 0;
        prior_neighbors[2] = 0;
        prior_neighbors[3] = 0;

        //check for ioob
        try {
            if(j > 0) {
                if(i > 0) {
                    prior_neighbors[0] = cells[i-1][j-1];
                }
                prior_neighbors[1] = cells[i][j-1];
                prior_neighbors[2] = cells[i+1][j-1];
            }
            if(i > 0) {
                prior_neighbors[3] = cells[i-1][j];
            }
        } catch(Exception e) {}

        return prior_neighbors;
    }

    public static int[][] getHole(int[][] image_a, int label){
        int[][] subimage = init();

        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(image_a[i][j] == label){
                    subimage[i][j] = 1;
                }
            }
        }

        return subimage;
    }
    
    public static int[][] clean(int[][] image_a, int min_area){
        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){

            }
        }
        return init();
    }
}
