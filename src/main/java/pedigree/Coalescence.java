package pedigree;

import java.io.FileNotFoundException;
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

        
        taille = simulation.population.getSize();
        sizePopulation.put(Tmax, taille);       //size final au Tmax
        if (taille == 0) throw new Exception("Population morte");

        for (int i=0; i<simulation.population.getSize(); i++) {
            Sim nextSim = simulation.population.deleteMin();
            //distribuer la population selon le sexe
            if (nextSim.getSex() == Sim.Sex.M) {
                coalescencePA.add(nextSim);
            } else {
                coalescenceMA.add(nextSim);
            }
        }
    }

    public HashMap<Double, Integer> makePA() {
        while (coalescencePA.getSize() != 0) {
            Sim youngestSim = coalescencePA.deleteMin(); //fonctionne comme un deleteMax a cause du compare de la classe Population
            double birthTime = youngestSim.getBirthTime();
            Sim pere = youngestSim.getFather();
            if (coalescencePA.population.contains(pere) || pere == null) {
                PA.put(birthTime, coalescencePA.getSize());
                if (pere == null) break;
            } else {
                coalescencePA.add(pere);
            }
        }
        return PA;
    }
    
    public HashMap<Double, Integer> makeMA() {
        while (coalescenceMA.getSize() != 0) {
            Sim youngestSim = coalescenceMA.deleteMin();
            double birthTime = youngestSim.getBirthTime();
            Sim mere = youngestSim.getMother();
            if (coalescenceMA.population.contains(mere) || mere == null) {
                MA.put(birthTime, coalescenceMA.getSize());
                if (mere == null) break;
            } else {
                coalescenceMA.add(mere);
            }
        }
        return MA;
    }

    public HashMap<Double, Integer> makePop() {
        return sizePopulation;
    }

    public static void writeToCsvFile (String fileName, HashMap<Double, Integer> hashMap, String s) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(fileName, true);
        PrintWriter pw = new PrintWriter(fos);

        pw.println("Annee, " + s);
        Object[] keyList = hashMap.keySet().toArray();
        Arrays.sort(keyList);

        for (Object key : keyList) {
            pw.println(key + "," + hashMap.get(key));
        }

        pw.close();
        System.out.println("file " + fileName + " has been written");
    }

    public static void main(String[] args) throws Exception {
        int n; int Tmax;

        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
        } else {
            n = Integer.parseInt(args[0]);
            Tmax = Integer.parseInt(args[1]);

            Coalescence coalescence = new Coalescence(n, Tmax);
            String f1, f2, f3, s1, s2, s3;
            f1 = "pere.csv"; f2 = "mere.csv"; f3 = "population.csv";
            s1 = "pere"; s2 = "mere"; s3 = "population";
            HashMap<Double, Integer> PA = coalescence.makePA();
            HashMap<Double, Integer> MA = coalescence.makeMA();
            HashMap<Double, Integer> Pop = coalescence.makePop();

            writeToCsvFile(f1, PA, s1);
            writeToCsvFile(f2, MA, s2);
            writeToCsvFile(f3, Pop, s3);

        }
    }
}