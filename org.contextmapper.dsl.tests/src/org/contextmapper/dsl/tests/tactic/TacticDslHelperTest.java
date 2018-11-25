package org.contextmapper.dsl.tests.tactic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.contextmapper.tactic.dsl.TacticDslHelper;
import org.contextmapper.tactic.dsl.tacticdsl.Application;
import org.contextmapper.tactic.dsl.tacticdsl.CommandEvent;
import org.contextmapper.tactic.dsl.tacticdsl.DataTransferObject;
import org.contextmapper.tactic.dsl.tacticdsl.DomainEvent;
import org.contextmapper.tactic.dsl.tacticdsl.Entity;
import org.contextmapper.tactic.dsl.tacticdsl.Module;
import org.contextmapper.tactic.dsl.tacticdsl.SimpleDomainObject;
import org.contextmapper.tactic.dsl.tacticdsl.TacticdslFactory;
import org.contextmapper.tactic.dsl.tacticdsl.ValueObject;
import org.junit.jupiter.api.Test;

class TacticDslHelperTest {

	@Test
	void canGetSubClasses() {
		// given
		Entity superEntity = TacticdslFactory.eINSTANCE.createEntity();
		Entity childEntity = TacticdslFactory.eINSTANCE.createEntity();
		superEntity.setName("SuperClass");
		childEntity.setName("SubClass");
		childEntity.setExtends(superEntity);

		Application app = TacticdslFactory.eINSTANCE.createApplication();
		Module module = TacticdslFactory.eINSTANCE.createModule();
		app.getModules().add(module);
		module.getDomainObjects().add(superEntity);
		module.getDomainObjects().add(childEntity);

		// when
		List<? extends SimpleDomainObject> subClasses = TacticDslHelper.getAllSubclasses((SimpleDomainObject) superEntity);

		// then
		assertEquals(1, subClasses.size());
		assertEquals(childEntity, subClasses.get(0));
	}

	@Test
	void canGetSubClassesByExtendName() {
		// given
		Entity superEntity = TacticdslFactory.eINSTANCE.createEntity();
		Entity childEntity = TacticdslFactory.eINSTANCE.createEntity();
		superEntity.setName("SuperClass");
		childEntity.setName("SubClass");
		childEntity.setExtendsName(superEntity.getName());

		Application app = TacticdslFactory.eINSTANCE.createApplication();
		Module module = TacticdslFactory.eINSTANCE.createModule();
		app.getModules().add(module);
		module.getDomainObjects().add(superEntity);
		module.getDomainObjects().add(childEntity);

		// when
		List<? extends SimpleDomainObject> subClasses = TacticDslHelper.getAllSubclasses((SimpleDomainObject) superEntity);

		// then
		assertEquals(1, subClasses.size());
		assertEquals(childEntity, subClasses.get(0));
	}

	@Test
	void canGetClassByName() {
		// given
		Entity entity1 = TacticdslFactory.eINSTANCE.createEntity();
		Entity entity2 = TacticdslFactory.eINSTANCE.createEntity();
		entity1.setName("Entity1");
		entity2.setName("Entity2");

		Application app = TacticdslFactory.eINSTANCE.createApplication();
		Module module = TacticdslFactory.eINSTANCE.createModule();
		app.getModules().add(module);
		module.getDomainObjects().add(entity1);
		module.getDomainObjects().add(entity2);

		// when
		SimpleDomainObject object = TacticDslHelper.findDomainObjectByName("Entity1", app);
		SimpleDomainObject notExistingObject = TacticDslHelper.findDomainObjectByName("JustAName", app);

		// then
		assertEquals("Entity1", object.getName());
		assertNull(notExistingObject);
	}

	@Test
	void cannotGetSubClassesForOthersThanDomainObjectsAndDTOs() {
		// no enums
		assertThrows(IllegalArgumentException.class, () -> {
			TacticDslHelper.getSubclasses(TacticdslFactory.eINSTANCE.createEnum());
		});

		// no traits
		assertThrows(IllegalArgumentException.class, () -> {
			TacticDslHelper.getSubclasses(TacticdslFactory.eINSTANCE.createTrait());
		});

		// no basic type
		assertThrows(IllegalArgumentException.class, () -> {
			TacticDslHelper.getSubclasses(TacticdslFactory.eINSTANCE.createBasicType());
		});
	}

	@Test
	void canStopGeneratorWithError() {
		assertThrows(RuntimeException.class, () -> {
			TacticDslHelper.error("just an error");
			;
		});
	}

	@Test
	void canGetSubClassesOfDTOs() {
		// given
		DataTransferObject superDto = TacticdslFactory.eINSTANCE.createDataTransferObject();
		DataTransferObject childDto = TacticdslFactory.eINSTANCE.createDataTransferObject();
		superDto.setName("SuperClass");
		childDto.setName("SubClass");
		childDto.setExtends(superDto);

		Application app = TacticdslFactory.eINSTANCE.createApplication();
		Module module = TacticdslFactory.eINSTANCE.createModule();
		app.getModules().add(module);
		module.getDomainObjects().add(superDto);
		module.getDomainObjects().add(childDto);

		// when
		List<? extends SimpleDomainObject> subClasses = TacticDslHelper.getAllSubclasses((SimpleDomainObject) superDto);

		// then
		assertEquals(1, subClasses.size());
		assertEquals("SubClass", subClasses.get(0).getName());
	}

	@Test
	void canGetSubClassesOfDTOsByExtendsName() {
		// given
		DataTransferObject superDto = TacticdslFactory.eINSTANCE.createDataTransferObject();
		DataTransferObject childDto = TacticdslFactory.eINSTANCE.createDataTransferObject();
		superDto.setName("SuperClass");
		childDto.setName("SubClass");
		childDto.setExtendsName(superDto.getName());

		Application app = TacticdslFactory.eINSTANCE.createApplication();
		Module module = TacticdslFactory.eINSTANCE.createModule();
		app.getModules().add(module);
		module.getDomainObjects().add(superDto);
		module.getDomainObjects().add(childDto);

		// when
		List<? extends SimpleDomainObject> subClasses = TacticDslHelper.getAllSubclasses((SimpleDomainObject) superDto);

		// then
		assertEquals(1, subClasses.size());
		assertEquals("SubClass", subClasses.get(0).getName());
	}

	@Test
	void canGetValueObjectsSuperClass() {
		// given
		ValueObject vo1 = TacticdslFactory.eINSTANCE.createValueObject();
		vo1.setName("Super");
		ValueObject vo2 = TacticdslFactory.eINSTANCE.createValueObject();
		vo2.setName("Child");
		vo2.setExtends(vo1);
		vo2.setExtendsName(vo1.getName());

		// when
		SimpleDomainObject result = TacticDslHelper.getSuperclass(vo2);

		// then
		assertEquals("Super", result.getName());
	}
	
	@Test
	void canGetCommandEventsSuperClass() {
		// given
		CommandEvent commandEvent1 = TacticdslFactory.eINSTANCE.createCommandEvent();
		commandEvent1.setName("Super");
		CommandEvent commandEvent2 = TacticdslFactory.eINSTANCE.createCommandEvent();
		commandEvent2.setName("Child");
		commandEvent2.setExtends(commandEvent1);
		commandEvent2.setExtendsName(commandEvent1.getName());

		// when
		SimpleDomainObject result = TacticDslHelper.getSuperclass(commandEvent2);

		// then
		assertEquals("Super", result.getName());
	}
	
	@Test
	void canGetDomainEventsSuperClass() {
		// given
		DomainEvent domainEvent1 = TacticdslFactory.eINSTANCE.createDomainEvent();
		domainEvent1.setName("Super");
		DomainEvent domainEvent2 = TacticdslFactory.eINSTANCE.createDomainEvent();
		domainEvent2.setName("Child");
		domainEvent2.setExtends(domainEvent1);
		domainEvent2.setExtendsName(domainEvent1.getName());

		// when
		SimpleDomainObject result = TacticDslHelper.getSuperclass(domainEvent2);

		// then
		assertEquals("Super", result.getName());
	}
	
	@Test
	void canGetDTOsSuperClass() {
		// given
		DataTransferObject dto1 = TacticdslFactory.eINSTANCE.createDataTransferObject();
		dto1.setName("Super");
		DataTransferObject dto2 = TacticdslFactory.eINSTANCE.createDataTransferObject();
		dto2.setName("Child");
		dto2.setExtends(dto1);
		dto2.setExtendsName(dto1.getName());

		// when
		SimpleDomainObject result = TacticDslHelper.getSuperclass(dto2);

		// then
		assertEquals("Super", result.getName());
	}

}
