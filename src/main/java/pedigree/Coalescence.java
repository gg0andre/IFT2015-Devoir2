package pedigree;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Fait par : Andre-Viet Tran et Adrian Necula
 *
 * Date : 1 mars 2020
 *
 * But :-remonter la lignee de temps pour tracer le nombre d'ancetres a la population courante (celle apres Tmax)
 *      -faire une serie pour une coalescence pour les peres et les meres
 *      -faire une serie pour la taille de la population (echantillon chaque 100 ans)
 *      (on genere celle-ci pendant la simulation de la classe Simulation afin de faciliter le programme)
 *
 *
 * ---------------------------- NOTE -------------------------------
 * Effacer le contenu des fichiers mere.csv, pere.csv et population.csv
 * pour tester les simulations. Ces fichiers contiennet les resultats de
 * la simulation que j'ai effectuer avec n = 1000 et Tmax = 10000
 *
 *
 */

public class Coalescence {
    private Simulation simulation;
    private Population coalescencePA;
    private Population coalescenceMA;

    protected HashMap<Double, Integer> PA;  //coalescence des peres
    protected HashMap<Double, Integer> MA;  //coalescence des meres
    //protected HashMap<Double, Integer> sizePopulation;  //taille de la population selon le temps
    
    public Coalescence(int n, double Tmax) throws Exception {
        simulation = new Simulation();
        simulation.simulate(n, Tmax);

        coalescencePA = new Population(Population.typeHeap.Max);
        coalescenceMA = new Population(Population.typeHeap.Max);

        PA = new HashMap<Double, Integer>();
        MA = new HashMap<Double, Integer>();
        //sizePopulation = new HashMap<Double, Integer>();
        
        int taille = simulation.population.getSize();
        //sizePopulation.put(Tmax, taille);       //size final au Tmax

        if (taille == 0) throw new Exception("Population morte. Recommencer la simulation");

        //separer la population a Tmax selon son sexe
        for (int i=0; i<simulation.population.getSize(); i++) {
            Sim nextSim = simulation.population.deleteMin();
            //distribuer le sim selon le sexe
            if (nextSim.getSex() == Sim.Sex.M) {
                coalescencePA.add(nextSim);
            } else {
                coalescenceMA.add(nextSim);
            }
        }
    }

    //fonction pour creer le HashMap pour les peres
    public HashMap<Double, Integer> makePA() {
        while (coalescencePA.peek() != null) {
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

    //fonction pour creer le HashMap pour les meres
    public HashMap<Double, Integer> makeMA() {
        while (coalescenceMA.peek() != null) {
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

    //retirer le HashMap de classe simulation
    public HashMap<Double, Integer> makePop() {
        return simulation.totalPopulation;
    }

    //Fonction pour creer le csv
    public static void writeToCsvFile (String fileName, HashMap<Double, Integer> hashMap, String s) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(fileName, true);
        PrintWriter pw = new PrintWriter(fos);

        pw.println("Annee, " + s);
        Object[] keyList = hashMap.keySet().toArray();
        Arrays.sort(keyList);

        for (Object key : keyList) {
            pw.println(Math.round((Double) key)+ "," + hashMap.get(key));
        }

        pw.close();
        System.out.println("\nfile " + fileName + " has been written");
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