package pedigree;

import java.util.PriorityQueue;
import java.util.Random;

import static pedigree.Event.eventType.Death;

public class Simulation {

    private static final double FIDELITE = 0.9;
    private AgeModel ageModel = new AgeModel();
    private Population population = new Population();               //Stockage de la population
    private PriorityQueue<Event> eventQ = new PriorityQueue<Event>();   //Stockage des events
    private Random RND = new Random();
    private double currentTime;
    private double birthRate;

    public void simulate(int n, double Tmax) {
        Sim fondateur;
        birthRate = 2.0 / ageModel.expectedParenthoodSpan(Sim.MIN_MATING_AGE_F, Sim.MAX_MATING_AGE_F);

        for(int i=0; i<n; i++) {
            if(RND.nextDouble() >= 0.5) {
                fondateur = new Sim(Sim.Sex.M);
            } else {
                fondateur = new Sim(Sim.Sex.F);
            }

            Event E = new Event(fondateur, 0.0, Event.eventType.Birth);
            eventQ.add(E);
        }

        while (!eventQ.isEmpty()) {
            Event E = eventQ.poll();    //retirer le premier event
            currentTime = 0.0;

            if (E.time > Tmax) break;

            if(E.time > currentTime+100) {
                System.out.println("Temps : " + E.time);
                System.out.println("Population : " + population.getSize());
                currentTime = E.time;
            }

            if (E.sim.getDeathTime() > E.time) {
                switch (E.type) {
                    case Birth:
                        //duree de vie du nouveau sim
                        double deathTime= ageModel.randomAge(RND) + E.time;

                        //attente de reproduction
                        if (E.sim.getSex() == Sim.Sex.F) {
                            double startReproductionTime = Sim.MIN_MATING_AGE_F + E.time + AgeModel.randomWaitingTime(RND, birthRate);
                            Event reproduction = new Event(E.sim, startReproductionTime, Event.eventType.Reproduction);
                            eventQ.add(reproduction);
                        }

                        E.sim.setDeathTime(deathTime);
                        Event death = new Event(E.sim, deathTime, Death);
                        eventQ.add(death);

                        population.add(E.sim);
                        break;

                    case Reproduction:
                        if (E.time > E.sim.getDeathTime()) break;

                        if (E.time - E.sim.getBirthTime() >= Sim.MIN_MATING_AGE_F) {
                            Sim mate = chooseMate(E.sim, E.time);
                            Sim child = reproduce(E.sim, mate, E.time);

                            Event birth = new Event(child, E.time, Event.eventType.Birth);
                            eventQ.add(birth);

                        }

                        double reproductionWaitTime = ageModel.randomWaitingTime(RND, birthRate);
                        Event reproEvent = new Event(E.sim, E.time + reproductionWaitTime, Event.eventType.Reproduction);
                        eventQ.add(reproEvent);

                        break;

                    case Death :
                        if (population.getSize()==0) throw new IllegalStateException("pas de population");
                        population.deleteMin();
                        break;
                }
            }
            currentTime+=5;
        }
    }

    //Choisir un pere, si possible rester avec le precedent
    public Sim chooseMate(Sim sim, double time) {
        Sim newMate = null;

        if(sim.isInARelationship(time) && RND.nextDouble() < FIDELITE) {
            return sim.getMate();
        } else {
            //nouveau partenaire
            Sim potentiel = population.randomSim();
            do {
                if (potentiel.getSex().equals(Sim.Sex.M) && potentiel.isMatingAge(time)) {
                    if(!potentiel.isInARelationship(time) || RND.nextDouble() < FIDELITE) {
                        newMate = potentiel;
                    }
                }

            } while (newMate == null) ;
        }

        return newMate;
    }

    //Naissance d'un nouveau sim de 2 sims
    public Sim reproduce(Sim mere, Sim pere, double time) {
        Sim.Sex sex = Math.random() < 0.5? Sim.Sex.M : Sim.Sex.F;
        Sim child = new Sim(mere, pere, time, sex);

        //ajouter le lien entre pere et mere
        mere.setMate(pere);
        pere.setMate(mere);

        return child;
    }


    public static void main(String[] args) {
        int n;
        int Tmax;
        if (args.length != 2) {
            System.out.println("Invalid number of arguments");
        } else {
            n = 5;
            Tmax = 150;
            Simulation test = new Simulation();

            test.simulate(n, Tmax);
            System.out.println("stop");
        }
    }
}