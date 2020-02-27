package pedigree;

import java.security.InvalidParameterException;

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



    @Override
    public int compareTo(Event o) {
        if(this.time<o.time) { return -1; }
        if(this.time==o.time) { return 0; }
        else { return 1;}
    }
}
