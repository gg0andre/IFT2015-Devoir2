package pedigree;

import java.security.InvalidParameterException;

/**
 * Fait par : Andre-Viet Tran et Adrian Necula
 *
 * Date : 24 Fevrier 2020
 *
 * But : Classe pour instancier les events pour la simulation
 *
 * Attributs : - type de l'evenement (Birth, Reproduction, Death)
 *             - Sim dont l'evenement est attribue
 *             - time : temps lorsque l'evenement est realise
 *
 */

public class Event implements Comparable<Event> {
    public enum eventType{Birth, Reproduction, Death};
    public double time;
    public Sim sim;
    public eventType type;

    public Event(Sim x, double time, eventType type) {
        this.type = type;
        this.sim=x;
        if(time >= 0) { this.time = time; }
        else { throw new InvalidParameterException("Le temps doit etre non-negatif");
        }
    }

    public int compareTo(Event o) {
        if(this.time<o.time) { return -1; }
        if(this.time==o.time) { return 0; }
        else { return 1;}
    }
}
