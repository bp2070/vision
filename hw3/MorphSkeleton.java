import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

class MorphSkeleton {
    private static final int SIZE = 140;

    public static void main(String[] args)throws Exception {
        int[][] image = read("f1.raw");
        Stack<int[][]> skeletons = findSkeleton(image);

        int[][] skeleton = init();
        for(int[][] s : skeletons){
            skeleton = union(skeleton, s);
        }

        write(skeleton, "skeleton.raw");

        buildImage(skeletons);
    }

    public static int[][] read(String file) throws IOException {
        FileReader inputStream = new FileReader(file);
        int[][] image = init();

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if (inputStream.read() == 0) image[i][j] = 1;
                else image[i][j] = 0;
            }
        }
        inputStream.close();
        return image;
    }

    public static void write(int[][] image, String outfile) throws IOException {
        FileWriter outputStream = new FileWriter(outfile);

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image[i][j] == 1) {
                    outputStream.write(0);
                }
                else outputStream.write(Byte.MAX_VALUE);
            }
        }
        outputStream.close();
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

    public static int[][] open(int[][] image) {
        return dilate(erode(image));
    }

    public static int[][] close(int[][] image) {
        return erode(dilate(image));
    }

    public static Stack<int[][]> findSkeleton(int[][] image) {
        return findSkeleton(image, new Stack<int[][]>());
    }

    public static Stack<int[][]> findSkeleton(int[][]image_a, Stack<int[][]> skeletons) {
        //base case
        if(isEmpty(image_a)) {
            return skeletons;
        }

        int[][] image_b = erode(image_a);
        //recursive case
        int[][] sn = diff(image_a, dilate(image_b));
        skeletons.push(sn);
        //skeleton_image = union(skeleton_image, sn); 
        return findSkeleton(image_b, skeletons);
    }

    public static int[][] buildImage(Stack<int[][]> skeletons){
        int[][] image = init();

        while(!skeletons.empty()){
            int[][] s = skeletons.pop();
            for(int i = 0; i < skeletons.size(); i++){
                s = dilate(s);
            }
            image = union(image, s);
            try{
                write(image, "rebuilt_image"+skeletons.size()+".raw");
            }catch(IOException ioe){}
        }

        return image;
    }

    //set difference
    public static int[][] diff(int[][] image_a, int[][] image_b) {
        int[][] diff_image = init();

        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image_a[i][j] == 1 && image_b[i][j] == 0) {
                    diff_image[i][j] = 1;
                }
                else diff_image[i][j] = 0;
            }
        }
        return diff_image;
    }

    public static Boolean isEmpty(int[][] image) {
        for(int i = 0; i < SIZE; i++) {
            for(int j = 0; j < SIZE; j++) {
                if(image[i][j] == 1) return false;
            }
        }
        return true;
    }

    public static int[][] union(int[][] image_a, int[][] image_b){
        int[][] union_image = init();

        for(int i = 0; i < SIZE; i++){
            for(int j = 0; j < SIZE; j++){
                if(image_a[i][j] == 1 || image_b[i][j] == 1){
                    union_image[i][j] = 1;
                }
            }
        }

        return union_image;
    }
}
