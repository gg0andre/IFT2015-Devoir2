package pedigree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.PriorityQueue;

public class Coalescence {
    private PriorityQueue<Sim> coalescenceQM;
    private PriorityQueue<Sim> coalescenceQF;
    public HashMap<Double, Integer> PA;
    public HashMap<Double, Integer> MA;

    public Coalescence(Population pop) {
        coalescenceQM = new PriorityQueue<Sim>();
        coalescenceQF = new PriorityQueue<Sim>();

        Iterator<Sim> popIterator = pop.population.iterator();
        while (popIterator.hasNext()) {
            Sim nextSim = popIterator.next();
            if (nextSim.getSex() == Sim.Sex.F) {
                coalescenceQF.add(nextSim);
            } else {
                coalescenceQM.add(nextSim);
            }
        }
        PA = new HashMap<Double, Integer>();
        MA = new HashMap<Double, Integer>();
    }

    public HashMap<Double, Integer> makePA() {
        while (!coalescenceQM.isEmpty()) {
            Sim youngestSim = coalescenceQM.poll();
            double birthtime = youngestSim.getBirthTime();
            Sim father = youngestSim.getFather();
            if (coalescenceQM.contains(father) || father ==null) {
                PA.put(birthtime, coalescenceQM.size());
                if (father == null) {
                    break;
                }
            } else {
                coalescenceQM.add(father);
            }
        }
        return PA;
    }

    public HashMap<Double, Integer> makeMA() {
        while (!coalescenceQF.isEmpty()) {
            Sim youngestSim = coalescenceQF.remove();
            double birthtime = youngestSim.getBirthTime();
            Sim mother = youngestSim.getMother();
            if (coalescenceQF.contains(mother) || mother == null) {
                MA.put(birthtime, coalescenceQF.size());
                if (mother == null) {
                    break;
                }
            } else {
                coalescenceQF.add(mother);
            }
        }
        return MA;
    }

}
