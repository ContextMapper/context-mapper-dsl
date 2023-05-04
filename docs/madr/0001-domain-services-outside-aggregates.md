---
status: "accepted"
date: "2023-04-06"
deciders: "stefan-ka, socadk"
---
# Allow modeling of domain services besides aggregates, instead of inside aggregates

## Context and Problem Statement

We originally took the [Sculptor DSL](http://sculptorgenerator.org/) for the tactic DDD part. For some reason we do not exactly know, they decided that services are modelled inside aggregates. This does not correspond with our interpretation of the DDD patterns and was additionally reported with [#326](https://github.com/ContextMapper/context-mapper-dsl/issues/326). Domain services should be modeled on the same level as aggregates (for CML, this means inside the bounded context object).

The problem: correcting this issue in a clean way would lead to a non-backwards-compatible change.

## Considered Options

* Not backwards-compatible: Make non-backwards-compatible change and release a new major version of Context Mapper.
* Backwards-compatible: Allow to model domain services inside bounded contexts, but still support the old option to stay backwards-compatible.

## Decision Outcome

Chosen option: "backwards-compatible", because a breaking change is risky, causes a lot of documentation work for us, and lots of migration work for the users (adjust their models). 

### Consequences

* Good, because we lower the risk and cause less migration and documentation work for us and the users.
* Bad, because it is still possible to model domain services in a way that does not reflect our understanding of the patterns.
