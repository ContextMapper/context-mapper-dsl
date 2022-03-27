package org.contextmapper.dsl.generator.plantuml;

public class AssociationInfo {
    private AssociationLink link;
    private Multiplicity multiplicityFirstParticipant;
    private Multiplicity multiplicitySecondParticipant;
    private boolean secondIsNavigableFromFirst;
    private boolean firstIsNavigableFromSecond;
    private String aggregateSource;
    private String aggregateTarget;

    AssociationInfo(AssociationLink link) {
        this.link = link;
        this.multiplicityFirstParticipant = null;
        this.multiplicitySecondParticipant = null;
        this.secondIsNavigableFromFirst = false;
        this.firstIsNavigableFromSecond = false;
    }

    public boolean getIsSecondNavigableFromFirst() {
        return secondIsNavigableFromFirst;
    }

    public boolean getIsFirstNavigableFromSecond() {
        return firstIsNavigableFromSecond;
    }

    public void setNavigability(String source, String target) {
        // Note that self-reference becomes bi-directional
        if (link.getFirstParticipant().equals(source) && link.getSecondParticipant().equals(target))
            this.secondIsNavigableFromFirst = true;
        if (link.getSecondParticipant().equals(source) && link.getFirstParticipant().equals(target))
            this.firstIsNavigableFromSecond = true;
    }

    public Multiplicity getMultiplicityFirstParticipant() {
        return multiplicityFirstParticipant;
    }

    public Multiplicity getMultiplicitySecondParticipant() {
        return multiplicitySecondParticipant;
    }

    public void setMultiplicity(String participant, Multiplicity multiplicity) {
        if (link.isSelfReference())
            return;

        if (participant.equals(link.getFirstParticipant()))
            this.multiplicityFirstParticipant = multiplicity.clone();
        else
            this.multiplicitySecondParticipant = multiplicity.clone();
    }

    public Multiplicity getMultiplicity(String participant) {
        if (participant.equals(link.getFirstParticipant()))
            return multiplicityFirstParticipant;
        else
            return multiplicitySecondParticipant;
    }

    public String getAggregateSource() {
        return aggregateSource;
    }

    public String getAggregateTarget() {
        return aggregateTarget;
    }

    public void setAggregateSource(String aggregateSource) {
        this.aggregateSource = aggregateSource;
    }

    public void setAggregateTarget(String aggregateTarget) {
        this.aggregateTarget = aggregateTarget;
    }
}