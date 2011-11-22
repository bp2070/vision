import java.util.List;
import java.util.ArrayList;
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
        
        net = new ANN(14, 14, 12, 10);

        List<String> filenames = new ArrayList<String>();
        filenames.add("res/0.raw");
        filenames.add("res/1.raw");
        filenames.add("res/2.raw");
        filenames.add("res/3.raw");
        filenames.add("res/4.raw");
        filenames.add("res/5.raw");
        filenames.add("res/6.raw");
//        filenames.add("res/7.raw");
//        filenames.add("res/8.raw");
//        filenames.add("res/9.raw");
        
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
            int i = r.nextInt(filenames.size());
            int target = Integer.parseInt(filenames.get(i).substring(4, 5));            
            int result = recognize(images.get(i));
            
            if(!(target == result)) {
                //result incorrect, adjust weights
                net.adjustWeights(gain, target);
//                gain *= .95;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {}
            System.out.println(" target: " + target + " result: " + result);
            
        }
    }

    /*
     * @return the index of the character recognized (1 = A, 2 = B, etc...)
     * or -1 if no char recognized
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
            features.add((double)count/IMAGE_SIZE);
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

        //calc mean and the two highest values
        double mean = 0;
        int first_index = -1;
        double first = Double.MIN_VALUE;
        double second = Double.MIN_VALUE;
        for(int i = 0; i < ann_out.size(); i++) {
            double axon = ann_out.get(i).getAxon();
            System.out.printf("%1.3f ", axon);
            if(axon >= first){
                second = first;
                first = axon;
                first_index = i;
            }
            mean += axon;
        } 
        mean = mean / IMAGE_SIZE;

        //calc standard deviation
        double std_dev = 0;
        for(int i = 0; i < ann_out.size(); i++) {
            double axon = ann_out.get(i).getAxon();
            std_dev += Math.pow(axon - mean, 2);
        }
        std_dev = Math.sqrt(std_dev / IMAGE_SIZE);

        if(first - second > std_dev)
            return first_index;
        else return -1;
    }
}
