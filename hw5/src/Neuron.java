import java.util.List;
import java.util.ArrayList;
import java.util.Random;

class Neuron{
    private List<Double> weights;
    private double axon;
    private double hiddenWeight;
    private double error;

    public Neuron(){
        this(0);
    }

    /*
     * @param numWeights number of weights, i.e. size of previous layer
     */
    public Neuron(int numWeights){
        Random r = new Random();
        hiddenWeight = r.nextDouble() - .5;
        error = 0.0;
        this.weights = new ArrayList<Double>(numWeights);
        for(int i = 0; i < numWeights; i++){
            weights.add(r.nextDouble() - .5);
        }
    }

    public double getError(){
        return error;
    }

    public void setError(double error){
        this.error = error;
    }

    public double getHiddenWeight(){
        return hiddenWeight;
    }

    public void setHiddenWeight(double d){
        hiddenWeight = d;
    }

    public double getAxon(){
        return axon;
    }

    public double getWeight(int i){
        return weights.get(i);
    }

    public List<Double> getWeights(){
        return weights;
    }

    public int size(){
        return weights.size();
    }

    public void setAxon(Double axon){
        this.axon = axon;
    }

    public void setWeight(int i, double weight){
        weights.set(i, weight);
    }
}
