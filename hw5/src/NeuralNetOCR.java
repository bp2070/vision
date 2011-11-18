import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.IOException;
import java.util.Random;

class NeuralNetOCR {

    //square image
    private static final int IMAGE_SIZE = 7;
    private static double gain = .5;
    private static ANN net;

    public static void main(String[] args) {
        //read in 128x128 image
        //map 128x128 image to 7x7 image
        //input: each row x col is a feature of the ANN (14 total)
        //output: recognize letters A - J (10 total)
        //three layers: 1st layer 14 nodes, 2nd layer 12 nodes, 3rd layer 10 nodes

        List<String> filenames = new ArrayList<String>();
        filenames.add("1");
        net = new ANN(14, 12, 10);
        train(filenames);
    }

    private static void train(List<String> filenames) {
        Random r = new Random();
        boolean trained = false;

        List<int[][]> images = new ArrayList<int[][]>(filenames.size());
        for(String filename : filenames){
            try {
                images.add(VisionUtil.read(filename, IMAGE_SIZE));
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        while(!trained) {
            int result = recognize(images.get(r.nextInt(filenames.size())));
            //@todo get target from filename
            int target = 0;
            boolean correct = (target == result) ? true : false;
            if(!correct) {
                //result incorrect, adjust weights
                net.adjustWeights(gain, target);
            }
            //@todo check if network is trained yet
        }
    }

    /*
     * @return the character recognized
     */
    private static int recognize(int[][] image){

        //count by row
        List<Double> features = new ArrayList<Double>();
        for(int i = 0; i < IMAGE_SIZE; i++) {
            int count = 0;
            for(int j = 0; j < IMAGE_SIZE; j++) {
                if(image[i][j] == 1) {
                    count++;
                }
            }
            features.add(new Integer(count).doubleValue());
        }

        //count by column
        for(int i = 0; i < IMAGE_SIZE; i++) {
            int count = 0;
            for(int j = 0; j < IMAGE_SIZE; j++) {
                if(image[j][i] == 1) {
                    count++;
                }
            }
            features.add(new Integer(count).doubleValue());
        }

        List<Neuron> ann_out = net.run(features);
        double max = -1;
        int max_pos = -1;

        for(int i = 0; i < ann_out.size(); i++) {
            Neuron n = ann_out.get(i);
            System.out.print(n.getAxon() + " ");

            if(n.getAxon() > max) {
                max = n.getAxon();
                max_pos = i;
            }
        }

        //@todo check if max if sufficiently greater than other axons (std dev?)        
        return max_pos;
    }
}
