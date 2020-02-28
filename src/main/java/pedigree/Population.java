package pedigree;

/**
 * MinHeap
 *
 * fonctions principales : deleteMin() et add(Sim newSim)
 * implementent les methodes sink() et swim()
 *
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class Population implements Comparator<Sim> {
    protected ArrayList<Sim> population;        //arrayList pour la population
    protected enum typeHeap{Min, Max};
    protected typeHeap type;
    private static int count = 0;

    public Population(Population.typeHeap type) {
        population = new ArrayList<Sim>();
        this.type=type;
        count++;
    }

    private int getLeftChildIndex(int parentIndex) {return 2 * parentIndex + 1;}
    private int getRightChildIndex(int parentIndex) {return 2 * parentIndex + 2;}
    private int getParentIndex(int childIndex) {return (childIndex - 1) / 2;}

    private boolean hasLeftChild(int index) {return getLeftChildIndex(index) < population.size();}
    private boolean hasRightChild(int index) {return getRightChildIndex(index) < population.size();}
    private boolean hasParent(int index) {return getParentIndex(index) < population.size();}

    private Sim getSim(int index) { return population.get(index);}

    //echanger 2 sims
    private void swap(int index1, int index2) {
        Collections.swap(population, index1, index2);
    }

    public Sim peek() {
        if(population.size() == 0) throw new IllegalStateException();
        return population.get(0);
    }

    public Sim deleteMin() {
        if(population.size() == 0) throw new IllegalStateException("No population ");

        Sim sim = population.get(0);
        int last = population.size() - 1;

        swap(0, last);
        population.remove(last);
        sink();

        return sim;
    }

    private void sink() {
        int indexC = 0;
        while (hasLeftChild(indexC)) {  //pas besoin de regarder l'enfant droit s'il n'y a pas d'enfant a gauche
            int smallerChildIndex = getLeftChildIndex(indexC);

            if(hasRightChild(indexC) && compare(population.get(getLeftChildIndex(indexC)) ,
                    population.get(getRightChildIndex(indexC))) == 1) {     //comparer l'enfant gauche et droit
                smallerChildIndex = getRightChildIndex(indexC);
            }

            if(compare(population.get(smallerChildIndex),population.get(indexC)) == -1) {   //comparer l'enfant au parent
                swap(smallerChildIndex,indexC);

            } else { break;}
            indexC = smallerChildIndex; //recommencer
        }

    }

    public void add(Sim newSim) {
        population.add(newSim);
        swim(population.size()-1);
    }

    private void swim(int i) {
        int indexP = 0;
        while (hasParent(i)) {
            indexP = getParentIndex(i);
            if(compare(population.get(i), population.get(indexP)) == -1) {  //comparer l'enfant courant avec son parent
                swap(i, indexP);    //enfant devient le parent maintenant
                i = indexP;
            } else { break;}
        }
    }


    @Override
    public int compare(Sim o1, Sim o2) {
        double x = o1.getDeathTime(), y = o2.getDeathTime();
        if (x < y) {
            return type == typeHeap.Min? -1 : 1;
        } else if (x > y) {
            return type == typeHeap.Min? 1 : -1;
        } else {
            return 0;
        }
    }

    public Sim randomSim() {
        Random random = new Random();

        return getSim(random.nextInt(population.size()));
    }

    public int getSize() {
        return population.size();
    }

//    public static void main(String[] args) {
//        Population M = new Population();
//        Sim s0 = new Sim(Sim.Sex.F);
//        Sim s1 = new Sim(Sim.Sex.M);
//        Sim s2 = new Sim(s0,s1,10.0, Sim.Sex.F);
//        Sim s3 = new Sim(s0,s1,20, Sim.Sex.F);
//        Sim s4 = new Sim(s2,s3, 21, Sim.Sex.M);
//        Sim s5 = new Sim(s4,s4, 50, Sim.Sex.F);
//        s0.setDeath(50);
//        s1.setDeath(60);
//        s2.setDeath(75);
//        s3.setDeath(78);
//        s4.setDeath(100);
//        s5.setDeath(99);
//
//        M.add(s4);
//        M.add(s1);
//        M.add(s5);
//        M.add(s2);
//        M.add(s3);
//        M.add(s0);
//        Sim s = M.deleteMin();
//        System.out.println(s);
//        s=M.deleteMin();
//        System.out.println(s);
//        s = M.randomSim();
//        System.out.println(s);
//    }
}
