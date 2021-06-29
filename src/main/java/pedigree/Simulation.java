package pedigree;

import java.util.HashMap;
import java.util.Random;

/***
 * Fait par : Andre-Viet Tran et Adrian Necula
 *
 * Date : 30 Fevrier 2020
 *
 * But : Simuler la population selon la taille entree (n) et le temps maximum (Tmax)
 *
 * Attribut : - ageModel pour access aux regles definies par le prof
 *            - population pour stocker la liste de la population de type minHeap
 *            - PQ pour la liste des evenements qui fonctionne comme un minHeap
 *            - totalPopulation pour stocker la taille de la population pour un echantillon de 100 ans (qui nous aidera
 *            pour la class Coalescence)
 *
 *
 */

public class Simulation {
    private AgeModel ageModel;
    protected Population population;
    private PQ eventQ;
    private Random RND = new Random();
    protected HashMap<Double,Integer> totalPopulation; //attribut pour suivre la taille de la population selon le temps

    private double birthRate;
    private static final double FIDELITY = 0.9;

    public void simulate(int n, double Tmax) {
        this.eventQ = new PQ();                   // file de priorité
        this.population = new Population(Population.typeHeap.Min);  //notre population de type HeapMin
        this.ageModel = new AgeModel();
        this.totalPopulation = new HashMap<Double, Integer>();

        birthRate = 2.0 / ageModel.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        Sim foundateur;

        for (int i = 0; i < n; i++) {
            Sim fondateur = new Sim(chooseSex()); // sexe au hasard, naissance à 0.0
            Event E = new Event(fondateur, 0.0, Event.eventType.Birth);
            this.eventQ.add(E); // insertion dans la file de priorité
        }

        while (!eventQ.isEmpty()) {
            Event E = eventQ.deleteMin(); // prochain événement

            if (E.time > Tmax) break; // arrêter à Tmax

            if (E.sim.getDeathTime() > E.time) {
                //System.out.println("population : " + population.getSize() + "\tat time : " + E.time);
                switch (E.type) {
                    case Birth:     //naissance du nouveau sim
                        if (E.sim.getSex() == Sim.Sex.F) {
                            //creer un event de type Reproduction seulement au femme et l'ajouter au PQ
                            double reproductionTime = ageModel.randomWaitingTime(RND, birthRate) + E.time;
                            Event reproductionEvent = new Event(E.sim,  reproductionTime, Event.eventType.Reproduction);
                            this.eventQ.add(reproductionEvent);
                        }

                        //duree de vie du sim + temp de naissance
                        double deathTime = ageModel.randomAge(RND) + E.time;

                        //creer le event de type Death et l'ajouter a la PQ
                        Event death = new Event(E.sim, deathTime, Event.eventType.Death);
                        this.eventQ.add(death);

                        //ajouter le sim a la liste de population
                        this.population.add(E.sim);

                        //echantillonner pour chaque 100ans
                        this.totalPopulation.put((double) Math.round(E.time/100) * 100, population.getSize());
                        break;

                    case Reproduction:  //reproduction d'un nouveau sim
                        if (E.sim.getDeathTime() <= E.time) break;  //verifier si la mere est morte

                        if (E.time - E.sim.getBirthTime() >= Sim.MIN_MATING_AGE_F &&
                                E.time - E.sim.getBirthTime() <= Sim.MAX_MATING_AGE_F) {
                            Sim mate = chooseMate(E.sim, E.time);                   //choisir le partenaire
                            Sim child = new Sim(E.sim, mate, E.time, chooseSex());  //creer l'enfant

                            //setMate pour parents
                            E.sim.setMate(mate);
                            mate.setMate(E.sim);

                            //ajout a eventQ
                            Event childBirth = new Event(child, E.time, Event.eventType.Birth);
                            this.eventQ.add(childBirth);

                        }

                        //ajouter une autre reproduction a la mere (avec un temps)
                        double reproductionWaitTime = ageModel.randomWaitingTime(RND, birthRate) + E.time;
                        Event reproductionEvent = new Event(E.sim, reproductionWaitTime, Event.eventType.Reproduction);
                        this.eventQ.add(reproductionEvent);

                        break;

                    case Death:     //mort du sim
                    default:
                        E.sim.setDeathTime(E.time);
                        System.out.println("Population remaining : " + population.getSize() + "\tat time : " + E.time);

                        //J'ai decider de forcer le remove direct dans le arraylist, car ca fonctionnait mieux
                        // que deleteMin() de la class Population
                        population.population.remove(E.sim);    //enlever de la population
                        //population.deleteMin();
                        break;
                }
            } // else rien à faire avec E car son sujet est mort
        }
    }

    //fonction pour choisir le sexe aleatoire
    public Sim.Sex chooseSex() {
        if(RND.nextInt() > 0.5) {
            return Sim.Sex.M;
        } else {
            return Sim.Sex.F;
        }
    }

    //fonction pour choisir un nouveau ou ancien partenaire pour la mere
    public Sim chooseMate(Sim x, double time) {
        Sim y = null;
        Sim z;
        if(!x.isInARelationship(time) || RND.nextDouble() > FIDELITY) {
            do {
                z = population.randomSim();
                // isMatingAge() vérifie si z est de l'age adéquat
                if (z.getSex() != x.getSex() && z.isMatingAge(time)) {
                    if (x.isInARelationship(time)   // z accepte si x est infidèle
                        || !z.isInARelationship(time)
                        || RND.nextDouble() > FIDELITY)
                    {   y = z; }
                }
            } while (y == null);
        } else { y = x.getMate(); } //retourne le partenaire precedent
        return y;
    }

    public static void main(String[] args) {
        int n; int Tmax;
        double startTime = System.nanoTime();

        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
        } else {
            n = Integer.parseInt(args[0]);
            Tmax = Integer.parseInt(args[1]);

            Simulation test = new Simulation();
            test.simulate(n,Tmax);

        }
        double endTime = System.nanoTime();
        System.out.println("Le programme a pris (en ms) : " + (endTime - startTime)/1000000);
    }

}
