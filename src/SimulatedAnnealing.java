import java.util.*;

public class SimulatedAnnealing {
    public static final Random rand = new Random();

    public void run(double T, double Tfinal, double alpha, double iterations) {

    }

    public boolean pass(int dC, double T) {
        return rand.nextDouble() < Math.exp(-dC / T);
    }
}
