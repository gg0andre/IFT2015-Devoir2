package pedigree;

import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Random;

public class Simulation {
    private AgeModel ageModel;
    private Population population;
    private PriorityQueue<Event> eventQ;
    private Random RND = new Random();

    public double birthRate;
    private static final double FIDELITY = 0.9;

    public void simulate(int n, double Tmax) {
        this.eventQ = new PriorityQueue<Event>();    // file de priorité
        this.population = new Population();          //notre population
        this.ageModel = new AgeModel();

        birthRate = 2.0 / ageModel.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);
        Sim foundateur;

        for (int i = 0; i < n; i++) {
            Sim fondateur = new Sim(chooseSex()); // sexe au hasard, naissance à 0.0
            Event E = new Event(fondateur, 0.0, Event.eventType.Birth);
            this.eventQ.add(E); // insertion dans la file de priorité
        }

        while (!eventQ.isEmpty()) {
            Event E = eventQ.poll(); // prochain événement

            if (E.time > Tmax) break; // arrêter à Tmax

            if (E.sim.getDeathTime() > E.time) {
                //System.out.println("population : " + population.getSize() + "\tat time : " + E.time);
                switch (E.type) {
                    case Birth:
                        if (E.sim.getSex() == Sim.Sex.F) {
                            double reproductionTime = ageModel.randomWaitingTime(RND, birthRate);
                            Event reproductionEvent = new Event(E.sim, 0.0, Event.eventType.Reproduction);
                            this.eventQ.add(reproductionEvent);
                        }

                        //duree de vie du sim + temp de naissance
                        double deathTime = ageModel.randomAge(RND) + E.time;
                        //E.sim.setDeath(deathTime);  //Ajouter au sim son deathTime

                        //creer le event et l'ajouter a la PQ
                        Event death = new Event(E.sim, deathTime, Event.eventType.Death);
                        this.eventQ.add(death);

                        //ajouter la liste de population
                        this.population.add(E.sim);
                        break;

                    case Death:
                        this.population.deleteMin();
                        System.out.println("population-: " + population.getSize() + "\tat time : " + E.time);
                        break;

                    case Reproduction:
                        if (E.sim.getDeathTime() <= E.time) break;

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

                        double reproductionWaitTime = ageModel.randomWaitingTime(RND, birthRate);
                        Event reproductionEvent = new Event(E.sim, E.time + reproductionWaitTime,
                                Event.eventType.Reproduction);
                        this.eventQ.add(reproductionEvent);

                        break;

                    default:
                        this.population.deleteMin();
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

    //fonction pour choisir un (nouveau ou ancien) partenaire pour la mere
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

        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
        } else {
            n = Integer.parseInt(args[0]);
            Tmax = Integer.parseInt(args[1]);

            Simulation test = new Simulation();
            test.simulate(n, Tmax);

            Sim s = test.population.deleteMin();
            System.out.println(s);
        }

    }

}
