package vision.hw4;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;


class DigitRecognizer {

    private static int SIZE = 128;

    public static DRView view;

    public static void main(String[] args) throws Exception {
        int[][] image;
        
        image = read("zero.raw");
        System.out.println(recognizeDigit(image));

        image = read("one.raw");
        System.out.println(recognizeDigit(image));

        image = read("two.raw");
        System.out.println(recognizeDigit(image));

        image = read("three.raw");
        System.out.println(recognizeDigit(image));
        
        image = read("four.raw");
        System.out.println(recognizeDigit(image));

        image = read("five.raw");
        System.out.println(recognizeDigit(image));

        image = read("six.raw");
        System.out.println(recognizeDigit(image));

        image = read("seven.raw");
        System.out.println(recognizeDigit(image));

        image = read("eight.raw");
        System.out.println(recognizeDigit(image));

        image = read("nine.raw");
        System.out.println(recognizeDigit(image));
    }

    public static int recognizeDigit(int[][] image){
        int[] bbox = getBoundingBox(image);
        int max_len = Math.max(bbox[0] + bbox[2], bbox[1] + bbox[3]);
        int close_amount = max_len * 1/7;
        return recognizeDigit(image, close_amount);
    }

    public static int recognizeDigit(int[][] image, int n){
        int[][] image_a = init();
        int[][] image_b = init();
        int[][] image_c = init();
        int[][] image_d = init();
        int[][] image_e = init();

        image_a = center(image);
        image_a = close(image_a, 3);
        image_b = close(image_a, n);
        image_c = dilate(erode(diff(image_b, image_a)));
        
        List labelHoles_result = labelHoles(image_c);
        int[][] labeled_image = (int[][])labelHoles_result.get(0);
        List<Integer> parents = (List<Integer>)labelHoles_result.get(1);

        int num_lids = 0;
        int num_lakes = 0;
        List<double[]> lid_vectors = new ArrayList<double[]>();

        for(int i = 1; i < parents.size(); i++) {
            int[][] image_component = init();
            if(parents.get(i) == 0) {
                image_component = getHole(labeled_image, i);
                image_d = diff(dilate(image_component), image_a);
                image_e = diff(image_d, image_component);
                image_e = clean(image_e);


                //check if lid
                boolean has_lid = false;
                for(int j = 0; j < SIZE; j++){
                    for(int k = 0; k < SIZE; k++){
                        if(image_e[j][k] == 1){
                            has_lid = true;
                        }
                    }
                }

                if(has_lid){
                    num_lids++;
                    int[] component_center = getCenter(image_component);
                    int[] lid_center = getCenter(image_e);

                    //lid unit direction vector
                    double[] lid_vector = new double[2];
                    lid_vector[0] = lid_center[0] - component_center[0];
                    lid_vector[1] = lid_center[0] - component_center[1];

                    double len = Math.sqrt(Math.pow(lid_vector[0], 2)+ Math.pow(lid_vector[1], 2));

                    lid_vector[0] = (lid_vector[0]/len);
                    lid_vector[1] = (lid_vector[1]/len);

                    lid_vectors.add(lid_vector);
                }
                else{
                    num_lakes++;
                }
            }
        }
       
        int number = -1; 
        if(num_lakes >=2){
           number = 8; 
        }
        else if(num_lakes == 1){
            if(num_lids == 0){
                number = 0;
            }
            else if(num_lids == 1){
                if(lid_vectors.get(0)[0] > 0){
                number = 6;
                }
                else if(lid_vectors.get(0)[0] < 0){
                     number = 9;
                }
            }
            else{
                number = 4;
            }
        }
        else{
            if(num_lids == 0){
                number = 1;
            }
            else if(num_lids == 1){
                number = 7;
            }
            else if(num_lids == 2){
                if(lid_vectors.get(0)[0] < 0 && lid_vectors.get(1)[0] > 0){
                    number = 2;
                }
                else if(lid_vectors.get(0)[0] > 0 && lid_vectors.get(1)[0] < 0){
                    number = 5;
                }
            }
            else{
                number = 3;
            }
        }
        return number;
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

    public static int[][] center(int[][] image) {
        int[] center = getCenter(image);

        int offset_x = (SIZE/2) - center[0];
        int offset_y = (SIZE/2) - center[1];

        int[][] translated_image = init();

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
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
        int min_x = Integer.MAX_VALUE;
        int max_x = 0;
        int min_y = Integer.MAX_VALUE;
        int max_y = 0;

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
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

    public static List labelHoles(int[][] image) {
        int[][] labeled_image = init();
        int label = 1;
        List<Integer> parents = new ArrayList<Integer>();
        parents.add(0);

        //1st pass - for each cell in the grid
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image[i][j] == 1) {

                    int[] pn = prior_neighbors(i, j, labeled_image);

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
                        parents.add(0);
                        label++;
                    }

                    //assign label
                    labeled_image[i][j] = min;

                    //union labels
                    for(int k = 0; k < 4; k++) {
                        int x = pn[k];
                        if (x != 0 && x != min) {
                            union(min, x, parents);
                        }
                    }
                }
            }
        }

        //2nd pass - for each cell in the grid
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image[i][j] == 1) {
                    labeled_image[i][j] = find(labeled_image[i][j], parents);
                }
            }
        }
        List result = new ArrayList(2);
        result.add(labeled_image);
        result.add(parents);
        return result;
    }

    public static int find(int i, List<Integer> parents) {
        while (parents.get(i) != 0) {
            i = parents.get(i);
        }
        return i;
    }

    public static void union(int x, int y, List<Integer> parents) {
        int j = find(x, parents);
        int k = find(y, parents);
        if (j != k) {
            parents.set(k, j);
        }
    }

    public static int[] prior_neighbors(int i, int j, int[][] image) {
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
                    prior_neighbors[0] = image[i-1][j-1];
                }
                prior_neighbors[1] = image[i][j-1];
                prior_neighbors[2] = image[i+1][j-1];
            }
            if(i > 0) {
                prior_neighbors[3] = image[i-1][j];
            }
        } catch(Exception e) {}

        return prior_neighbors;
    }

    public static int[][] getHole(int[][] image, int label) {
        int[][] image_component = init();

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image[i][j] == label) {
                    image_component[i][j] = 1;
                }
            }
        }

        return image_component;
    }

    public static int[][] clean(int[][] image) {
        int[][] cleaned_image = image;

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                try {
                    if( image[i-1][j-1] == 0 &&
                            image[i][j-1]   == 0 &&
                            image[i+1][j-1] == 0 &&
                            image[i-1][j]   == 0 &&
                            image[i][j]     == 1 &&
                            image[i+1][j]   == 0 &&
                            image[i-1][j+1] == 0 &&
                            image[i][j+1]   == 0 &&
                            image[i+1][j+1] == 0) {

                        cleaned_image[i][j] = 0;
                    }
                } catch(Exception e) {}
            }
        }

        return cleaned_image;
    }
}
