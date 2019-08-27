import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by TrentRand on 2019-08-26.
 */
public class Swarm extends Strategy {

    private int swarmSize;

    private ArrayList<Strategy> swarm = new ArrayList<Strategy>();
    private Vector allocations = new Vector();

    private Vector systemvariables = new Vector();

    public Swarm(int swarmSize, Vector systemVars, Vector allocs, Strategy strategy) {
        this.swarmSize = swarmSize;
        this.systemvariables = systemVars;
        this.allocations = allocs;


        for(int i = 0; i < swarmSize; i++) {

            for(int t = 0; t < systemVars.size(); t++) {




            }


        }


    }






}
