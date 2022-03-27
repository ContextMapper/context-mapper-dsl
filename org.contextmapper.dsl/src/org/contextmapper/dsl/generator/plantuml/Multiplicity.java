package org.contextmapper.dsl.generator.plantuml;

public class Multiplicity {
    private int min;
    private int max;
    
    public static int STAR = Integer.MAX_VALUE;

    public Multiplicity(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public Multiplicity withMin(int min)
    {
        return new Multiplicity(min, this.max);
    }

    public Multiplicity withMax(int max)
    {
        return new Multiplicity(this.min, max);
    }

    public Multiplicity clone() {
        return new Multiplicity(this.min, this.max);
    }

    public boolean isConstant(int i) {
        return min == i && max == i;
    }
}
