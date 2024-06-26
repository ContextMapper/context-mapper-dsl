/*
 * Copyright 2024 The Context Mapper Project Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.contextmapper.dsl.generator.plantuml;

import java.util.List;

import org.contextmapper.dsl.contextMappingDSL.AbstractStakeholder;
import org.contextmapper.dsl.contextMappingDSL.BoundedContext;
import org.contextmapper.dsl.contextMappingDSL.Stakeholder;
import org.contextmapper.dsl.contextMappingDSL.StakeholderGroup;

import com.google.common.collect.Lists;

public class PlantUMLStakeholderMapGenerator extends AbstractPlantUMLMindMapDiagramCreator<BoundedContext>
		implements PlantUMLDiagramCreator<BoundedContext> {

	private static final String STAR = "*";

	private List<AbstractStakeholder> left = Lists.newArrayList();
	private List<AbstractStakeholder> right = Lists.newArrayList();

	@Override
	protected void printDiagramContent(final BoundedContext context) {
		initData(context);

		sb.append(STAR).append(" ").append(context.getName());
		linebreak();
		printStakeholders(right);
		linebreak();
		sb.append("left side");
		linebreak();
		printStakeholders(left);
	}

	private void printStakeholders(final List<AbstractStakeholder> stakeholders) {
		for (AbstractStakeholder s : stakeholders) {
			if (s instanceof StakeholderGroup) {
				printStakeholderGroup((StakeholderGroup) s);
			} else if (s instanceof Stakeholder) {
				sb.append(STAR).append(STAR).append(" " + s.getName());
				linebreak();
			}
		}
	}

	private void printStakeholderGroup(final StakeholderGroup group) {
		sb.append(STAR).append(STAR).append(" ").append(group.getName());
		linebreak();
		for (Stakeholder s : group.getStakeholders()) {
			sb.append(STAR).append(STAR).append(STAR).append(" ").append(s.getName());
			linebreak();
		}
	}

	private void initData(final BoundedContext context) {
		left.clear();
		right.clear();

		boolean addLeft = true;

		for (AbstractStakeholder s : context.getStakeholders()) {
			if (addLeft)
				left.add(s);
			else
				right.add(s);

			addLeft = !addLeft;
		}
	}

}
