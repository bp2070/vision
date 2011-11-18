import java.util.List;
import java.util.ArrayList;

class Neuron{
    private List<Double> weights;
    private double axon;
    private double hiddenWeight;
    private double error;

    public Neuron(){
        this(0);
    }

    /*
     * @param size number of neurons in previous layer
     */
    public Neuron(int size){
        hiddenWeight = 1.0;
        error = 0.0;
        this.weights = new ArrayList<Double>(size);
        for(int i = 0; i < size; i++){
            weights.add(1.0);
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
    public Double getWeight(int i){
        return weights.get(i);
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
