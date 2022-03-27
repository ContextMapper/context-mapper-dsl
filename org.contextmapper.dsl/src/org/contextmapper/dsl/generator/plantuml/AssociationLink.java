package org.contextmapper.dsl.generator.plantuml;

public class AssociationLink {
    private String participant1;
    private String participant2;
    private String label;

    public AssociationLink(String participant1, String participant2, String label)
    {
            this.participant1 = participant1;
            this.participant2 = participant2;
            this.label = label;
    }   

    public String getFirstParticipant() {
        return participant1;
    }

    public String getSecondParticipant() {
        return participant2;
    }

    public String getLabel() {
        return label;
    }

    public boolean equals(Object obj) {
        if (this==obj)
            return true;
        if((obj == null) || (obj.getClass() != this.getClass()))
            return false;            
        AssociationLink other = (AssociationLink)obj;

        String thisP1, thisP2, otherP1, otherP2;
        if (participant1.compareTo(participant2) < 0)
        {
            thisP1 = this.participant1;
            thisP2 = this.participant2;
        } else {
            thisP1 = this.participant2;
            thisP2 = this.participant1;
        }

        if (other.participant1.compareTo(other.participant2) < 0)
        {
            otherP1 = other.participant1;
            otherP2 = other.participant2;
        } else {
            otherP1 = other.participant2;
            otherP2 = other.participant1;
        }

        return thisP1.equals(otherP1) && thisP2.equals(otherP2) && this.label.equals(other.label);
    }
    
    public int hashCode() {
        return participant1.hashCode() + participant2.hashCode() + label.hashCode();
    }

    public boolean isSelfReference() {
        return participant1.equals(participant2);
    }
}
