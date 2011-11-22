import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class ANN {

    private List<List<Neuron>> layers;
    private List<Double> _features;
    private int num_features;

    /*
     * first layer is input (features)
     * subsequent layers are neurons
     */
    public ANN(int... layer_sizes) {
        num_features = layer_sizes[0];
        layers = new ArrayList<List<Neuron>>(layer_sizes.length - 1);

        Random random = new Random();
        for(int i = 1; i < layer_sizes.length; i++) {
            List<Neuron> layer = new ArrayList<Neuron>(layer_sizes[i]);
            for(int j = 0; j < layer_sizes[i]; j++) {
                //neurons have number of weights equal to size of previous layer
                Neuron neuron = new Neuron(layer_sizes[i-1]);
                for(int k = 0; k < layer_sizes[i-1]; k++) {
                    neuron.setWeight(k, random.nextDouble() - 0.5);
                }
                layer.add(neuron);
            }
            layers.add(layer);
        }
    }

    /*
     * @param features size must be equal to the number
     * of features this ANN was initialized for
     */
    public List<Neuron> run(List<Double> features) {
        assert features != null;
        assert num_features == features.size();

        _features = features;

        //for each layer
        for(int i = 0; i < layers.size(); i++) {
            List<Neuron> layer = layers.get(i);

            //for each neuron in layer
            for(int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                double result = 0;

                //if this is the first layer use features as input
                if(i == 0) {
                    for(int k = 0; k < features.size(); k++) {
                        result += features.get(k) * n.getWeight(k);
                    }
                }

                //if this is _not_ the first layer use axons from previous layer as input
                else {
                    List<Neuron> prev_layer = layers.get(i-1);
                    for(int k = 0; k < prev_layer.size(); k++) {
                        result += prev_layer.get(k).getAxon() * n.getWeight(k);
                    }
                }
                //add in hidden weight
                result += n.getHiddenWeight();
                n.setAxon(activationFunction(result));
            }
        }
        return layers.get(layers.size()-1);
    }

    /*
     * @param gain magnitude of correction
     * @param target the index of the output layer
     * that should be 1 (all others should be 0)
     */
    public void adjustWeights(double gain, int target) {
        //for each layer
        for(int i = layers.size()-1; i >= 0; i--) {
            List<Neuron> layer = layers.get(i);

            //for each neuron
            for(int j = 0; j < layer.size(); j++) {
                Neuron n = layer.get(j);
                double count = 0;
                for(int k = 0; k < n.size(); k++){
                    count += n.getWeights().get(k);
                }

                //calculate error
                double error = calcError(i, j, target);
                n.setError(error);

                //calculate total input to activation function
                double activation_in = calcActivationIn(i);

                double delta_w = 0;
                for(int k = 0; k < n.size(); k++) {
                    double prev_node_out;
                    if(i != 0){
                        prev_node_out =  layers.get(i-1).get(k).getAxon();
                    }
                    else{
                        prev_node_out = _features.get(k);
                    }

                    delta_w = gain * error * activation_in * prev_node_out;
                    double weight = delta_w + n.getWeight(k);
                    n.setWeight(k, weight);
                }
                n.setHiddenWeight(delta_w + n.getHiddenWeight());
            }
        }
    }

    /*
     * Calculate error
     * @param layer_index the index of they layer that contains the nueron to calculate error for
     * @param neuron_index the index of the neuron to calculate error for
     * @param target_index the index of the neuron in the output layer whose output should be 1
     */
    private double calcError(int layer_index, int neuron_index, int target_index){
        double error = 0;
        
        //error calculation for output layer
        if(layer_index == layers.size()-1){
            double ideal_output = (neuron_index == target_index) ? 1 : 0;
            double actual_output = layers.get(layer_index).get(neuron_index).getAxon();
            error = ideal_output - actual_output;
        }
        //error calculation for hidden layers
        else{
            List<Neuron> next_layer = layers.get(layer_index+1);
            for(Neuron neuron : next_layer){
                for(Double weight : neuron.getWeights()){
                    error+=weight * neuron.getError();
                }
                error += neuron.getHiddenWeight() * neuron.getError();
            }
        }
        return error;
    }

    private double calcActivationIn(int layer_index){
        double activation_in = 0;
        
        //if first layer, use features instead of previous layer
        if(layer_index == 0){
           for(Double d : _features){
                activation_in += d;
            }
        }        
        else{
            List<Neuron> prev_layer = layers.get(layer_index-1);
            for(int k = 0; k < prev_layer.size(); k++) {
                activation_in += prev_layer.get(k).getAxon();
            }            
        }
        return activation_in;
    }

    private double activationFunction(double x) {
        double t = 0;
        return 1 / (1 + Math.pow(Math.E, -(x-t)));
    }
}
