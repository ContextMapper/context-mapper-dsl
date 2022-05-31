package org.contextmapper.dsl.generator.plantuml;

import java.util.Objects;
import java.util.Set;

import com.google.common.collect.Sets;

public class AssociationLink {
	private String participant1;
	private String participant2;
	private String label;

	public AssociationLink(String participant1, String participant2, String label) {
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

	@Override
	public int hashCode() {
		return Objects.hash(label, createParticipantSet(this));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociationLink other = (AssociationLink) obj;
		return Objects.equals(label, other.label)
				&& Objects.equals(createParticipantSet(this), createParticipantSet(other));
	}

	private Set<String> createParticipantSet(final AssociationLink association) {
		Set<String> participants = Sets.newTreeSet();
		participants.add(association.participant1);
		participants.add(association.participant2);
		return participants;
	}

	public boolean isSelfReference() {
		return participant1.equals(participant2);
	}
}
