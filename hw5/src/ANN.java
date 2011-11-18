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
                n.setAxon(activationFunction(result));
            }
        }

        return layers.get(layers.size()-1);
    }

    /*
     * @param gain magnitude of correction
     * @param target the index of the output that should be 1 (all others should be 0)
     */
    public void adjustWeights(double gain, int target) {

        for(int k = layers.size()-1; k >= 0; k--) {
            List<Neuron> layer = layers.get(k);
            for(int i = 0; i < layer.size(); i++) {
                Neuron n = layer.get(i);
                
                double error;
                //error calculation for output layer
                if(k == layers.size()-1){
                    double ideal_output = (i == target) ? 1 : 0;
                    double actual_output = n.getAxon();
                    error = ideal_output - actual_output;
                    n.setError(error);
                }
                //error calculation for hidden layers
                else{
                    if(k > 0){
                        List<Neuron> prev_layer = layers.get(k-1);
                        for(Neuron n2 : prev_layer){
                            error += n2.getWeight() * n2.getError();
                        }

                    n.setError(error);
                }

                double sum_in = 0;
                
                if(k != 0){
                List<Neuron> prev_layer = layers.get(k-1);
                for(int j = 0; j < prev_layer.size(); j++) {
                    sum_in += prev_layer.get(j).getAxon();
                }
                for(int j = 0; j < n.size(); j++) {
                    double prev_node_out = prev_layer.get(j).getAxon();
                    double delta_w = gain * error * sum_in * prev_node_out;
                    double weight = delta_w * n.getWeight(j);
                    n.setWeight(j, weight);
                }
                }
                //if first layer use features instead of previous layer
                else{
                for(Double d : _features){
                    sum_in += d;
                }
                for(int j = 0; j < n.size(); j++) {
                    double prev_node_out = _features.get(j);
                    double delta_w = gain * error * sum_in * prev_node_out;
                    double weight = delta_w * n.getWeight(j);
                    n.setWeight(j, weight);
                }
                }

            }
        }
    }

    private double activationFunction(double x) {
        double t = 0;
        return 1 / (1 + Math.pow(Math.E, -(x-t)));
    }
}
