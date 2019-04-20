package org.contextmapper.dsl.refactoring;

import java.util.List;
import java.util.stream.Collectors;

import org.contextmapper.dsl.contextMappingDSL.Aggregate;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.ContextMappingDSLFactory;
import org.contextmapper.dsl.contextMappingDSL.LikelihoodForChange;
import org.contextmapper.dsl.refactoring.henshin.Refactoring;
import org.eclipse.xtext.EcoreUtil2;

public class ExtractAggregatesLikelyToChange extends AbstractRefactoring implements Refactoring {

	private String boundedContextName;
	private BoundedContext originalBC;

	public ExtractAggregatesLikelyToChange(String boundedContextName) {
		this.boundedContextName = boundedContextName;
	}

	@Override
	protected void doRefactor() {
		initOriginalBC();

		// do nothing if there is only one aggregate
		if (originalBC.getAggregates().size() < 2)
			return;

		List<Aggregate> aggregates = collectAggregatesWhichAreLikelyToChange();
		if (aggregates.size() < 1)
			return;

		BoundedContext newBC = createNewBoundedContext();
		for (Aggregate aggregate : aggregates) {
			// move the matching aggregates to the new Bounded Context
			newBC.getAggregates().add(aggregate);
			this.originalBC.getAggregates().remove(aggregate);
		}
		this.model.getBoundedContexts().add(newBC);
		saveResource();
	}

	private BoundedContext createNewBoundedContext() {
		BoundedContext newBC = ContextMappingDSLFactory.eINSTANCE.createBoundedContext();
		newBC.setName(boundedContextName + "_Volatile");
		return newBC;
	}

	private List<Aggregate> collectAggregatesWhichAreLikelyToChange() {
		return this.originalBC.getAggregates().stream().filter(agg -> agg.getLikelihoodForChange().equals(LikelihoodForChange.OFTEN)).collect(Collectors.toList());
	}

	private void initOriginalBC() {
		List<BoundedContext> allBCs = EcoreUtil2.<BoundedContext>getAllContentsOfType(model, BoundedContext.class);
		List<BoundedContext> bcsWithGivenInputName = allBCs.stream().filter(bc -> bc.getName().equals(boundedContextName)).collect(Collectors.toList());
		this.originalBC = bcsWithGivenInputName.get(0);
	}

}
