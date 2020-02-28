package pedigree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Coalescence {
    private Simulation simulation;
    private PriorityQueue<Sim> coalescencePA;   //pere
    private PriorityQueue<Sim> coalescenceMA;   //mere

    protected HashMap<Double, Integer> PA;
    protected HashMap<Double, Integer> MA;
    protected HashMap<Double, Integer> Pop;

    public Coalescence(int n, double Tmax) {
        simulation = new Simulation();
        simulation.simulate(n, Tmax);

        coalescencePA = new PriorityQueue<Sim>();
        coalescenceMA = new PriorityQueue<Sim>();
        Iterator<Sim> iter = simulation.population.population.iterator();     //iterator du arraylist

        //on parcourt le arraylist au complet pour le sim a la liste de sexe
        while (iter.hasNext()) {
            Sim nextSim = iter.next();

            if (nextSim.getSex() == Sim.Sex.M) {
                coalescencePA.add(nextSim);
            } else {
                coalescenceMA.add(nextSim);
            }
        }
        
    }


}
