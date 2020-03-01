package pedigree;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Coalescence {
    private Simulation simulation;
    private Population coalescencePA;
    private Population coalescenceMA;

    protected HashMap<Double, Integer> PA;
    protected HashMap<Double, Integer> MA;
    protected HashMap<Double, Integer> sizePopulation;
    
    public Coalescence(int n, double Tmax) throws Exception {
        simulation = new Simulation();
        simulation.simulate(n, Tmax);

        coalescencePA = new Population(Population.typeHeap.Max);
        coalescenceMA = new Population(Population.typeHeap.Max);

        PA = new HashMap<Double, Integer>();
        MA = new HashMap<Double, Integer>();
        sizePopulation = new HashMap<Double, Integer>();
        
        int taille;
        
        ArrayList<Sim> pereListe = new ArrayList<Sim>();
        ArrayList<Sim> mereListe = new ArrayList<Sim>();
        
        taille = simulation.population.getSize();
        sizePopulation.put(Tmax, taille);       //size initial
        if (taille == 0) throw new Exception("Population morte");

        for (int i=0; i<simulation.population.getSize(); i++) {
            Sim nextSim = simulation.population.deleteMin();
            //distribuer la population selon le sexe
            if (nextSim.getSex() == pedigree.Sim.Sex.F) {
                mereListe.add(nextSim);
            } else {
                pereListe.add(nextSim);
            }
        }
        
        while (pereListe.size() > 1) {
            Sim youngestSim = getSim(pereListe);
            if (!coalescencePA.population.contains(youngestSim.getFather())) {
                coalescencePA.add(youngestSim);
            } else {
                PA.put(youngestSim.getBirthTime(), coalescencePA.getSize());
            }
            pereListe.remove(pereListe.indexOf(youngestSim));
            taille--;
            sizePopulation.put(youngestSim.getBirthTime(), taille);
        }
        
       
        while (mereListe.size() > 1) {
            Sim youngestSim = getSim(mereListe);
            if (!coalescenceMA.population.contains(youngestSim.getMother())) {
                coalescenceMA.add(youngestSim);
            } else {
                MA.put(youngestSim.getBirthTime(), coalescenceMA.getSize());
            }
            mereListe.remove(mereListe.indexOf(youngestSim));
            taille--; 
            sizePopulation.put(youngestSim.getBirthTime(), taille);
        }
    }
    
    
    public Sim getSim(ArrayList<Sim> population) {
        Sim youngestSim = population.get(0);
        
        for (int i = 0; i < population.size(); i++) {
            if (population.get(i).getBirthTime() < youngestSim.getBirthTime()) {
                youngestSim = population.get(i);
            }
        }
        return youngestSim;
    }

    public static void main(String[] args) throws Exception {
        int n; int Tmax;

        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
        } else {
            n = Integer.parseInt(args[0]);
            Tmax = Integer.parseInt(args[1]);

            Coalescence coalescence = new Coalescence(n, Tmax);
            FileOutputStream fos1 = new FileOutputStream("pere.csv", true);
            PrintWriter pw1 = new PrintWriter(fos1);

            pw1.println("Annee, pere");
            Object[] keysPA = coalescence.PA.keySet().toArray();
            Arrays.sort(keysPA);

            for (Object key1: keysPA) {
                pw1.println(key1 + "," + coalescence.PA.get(key1));
            }

            pw1.close();
            System.out.println("file pere.csv has been written");

            FileOutputStream fos2 = new FileOutputStream("mere.csv", true);
            PrintWriter pw2 = new PrintWriter(fos2);
            pw2.println("Annee, mere");
            Object[] keysMA = coalescence.MA.keySet().toArray();
            Arrays.sort(keysMA);

            for (Object key2: keysMA) {
                pw2.println(key2 + "," + coalescence.MA.get(key2));
            }

            pw2.close();
            System.out.println("file mere.csv has been written");

            FileOutputStream fos3 = new FileOutputStream("population.csv", true);
            PrintWriter pw3 = new PrintWriter(fos3);
            pw3.println("Annee, population");
            Object[] keysPop = coalescence.sizePopulation.keySet().toArray();
            Arrays.sort(keysPop);

            for (Object key3: keysPop) {
                pw3.println(key3 + "," + coalescence.sizePopulation.get(key3));
            }

            pw3.close();
            System.out.println("file population.csv has been written");

        }
    }
}