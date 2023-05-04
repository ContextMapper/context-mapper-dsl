---
status: "accepted"
date: "2023-05-04"
deciders: "stefan-ka, socadk"
---
# Layout for Use Case Diagrams (PlantUML Generator)

## Context and Problem Statement

While implementing our Use Case diagram generator, we realised that there are some differences regarding how people visualize use case diagrams. The [PlantUML documentation](https://plantuml.com/use-case-diagram) by default suggests that arrows point from actors to use cases. It does not clearly state how [secondary actors](https://socadk.github.io/design-practice-repository/artifact-templates/DPR-UseCase.html) shall be visualized. socadk legitimately pointed out that for a secondary actor it would make more sense to point the arrow from the use case to the secondary actor, as only the primary actor initiates the use case.
Based on that discussion we checked out [Larman](https://www.amazon.de/Applying-UML-Patterns-Introduction-Object-Oriented/dp/0131489062); a popular book on UML and OOAD, serving as a 'de-facto' standard for many modelers. Larman suggests to not make arrows but just connect the actors with the use cases and draw the primary actors on the left and secondary actors on the right. Use cases are visualized top to bottom.

## Considered Options

* Default PlantUML with arrows
* Stick to Larman

## Decision Outcome

Chosen option: "Stick to Larman", because we try to use and align with the literature as such as possible. For example: For the Context Map generator we tried to stick to the visualization of [Vaughn Vernon](https://www.amazon.de/s?k=implementing+domain+driven+design&adgrpid=63456564181&hvadid=381008329565&hvdev=c&hvlocphy=1030659&hvnetw=g&hvqmt=e&hvrand=11057192946270756118&hvtargid=kwd-299405302030&hydadcr=14770_1980674&tag=hydrach-21&ref=pd_sl_9tt0fqrrb3_e). Thereby, our decision is consistent with our earlier design principles.
