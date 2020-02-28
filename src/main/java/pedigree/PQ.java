package pedigree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PQ implements Comparator<Event> {
    protected ArrayList<Event> PQ;

    public PQ (){
        this.PQ = new ArrayList<Event>();
    }

    private int getLeftChildIndex(int parentIndex) {return 2 * parentIndex + 1;}
    private int getRightChildIndex(int parentIndex) {return 2 * parentIndex + 2;}
    private int getParentIndex(int childIndex) {return (childIndex - 1) / 2;}

    private boolean hasLeftChild(int index) {return getLeftChildIndex(index) < PQ.size();}
    private boolean hasRightChild(int index) {return getRightChildIndex(index) < PQ.size();}
    private boolean hasParent(int index) {return getParentIndex(index) < PQ.size();}
    
    private Event getEvent(int index) { return PQ.get(index); }
    
    private void swap(int index1, int index2) {
        Collections.swap(PQ, index1, index2);
    }

    public Event deleteMin() {
        if(PQ.size() == 0) throw new IllegalStateException("No PQ ");

        Event e = PQ.get(0);
        int last = PQ.size() - 1;

        swap(0, last);
        PQ.remove(last);
        sink();

        return e;
    }

    private void sink() {
        int indexC = 0;
        while (hasLeftChild(indexC)) {  //pas besoin de regarder l'enfant droit s'il n'y a pas d'enfant a gauche
            int smallerChildIndex = getLeftChildIndex(indexC);

            if(hasRightChild(indexC) && compare(PQ.get(getLeftChildIndex(indexC)) ,
                    PQ.get(getRightChildIndex(indexC))) == 1) {     //comparer l'enfant gauche et droit
                smallerChildIndex = getRightChildIndex(indexC);
            }

            if(compare(PQ.get(smallerChildIndex),PQ.get(indexC)) == -1) {   //comparer l'enfant au parent
                swap(smallerChildIndex,indexC);

            } else { break;}
            indexC = smallerChildIndex; //recommencer
        }

    }

    public void add(Event newEvent) {
        PQ.add(newEvent);
        swim(PQ.size()-1);
    }

    private void swim(int i) {
        int indexP = 0;
        while (hasParent(i)) {
            indexP = getParentIndex(i);
            if(compare(PQ.get(i), PQ.get(indexP)) == -1) {  //comparer l'enfant courant avec son parent
                swap(i, indexP);    //enfant devient le parent maintenant
                i = indexP;
            } else { break;}
        }
    }

    @Override
    public int compare(Event o1, Event o2) {
        if(o1.time<o2.time) { return -1; }
        if(o1.time==o2.time) { return 0; }
        else { return 1;}
    }

    public boolean isEmpty() {
        return PQ.size() != 0? false : true;
    }
}
