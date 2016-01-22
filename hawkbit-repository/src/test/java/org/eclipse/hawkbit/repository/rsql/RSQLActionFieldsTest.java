/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.rsql;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.eclipse.hawkbit.AbstractIntegrationTest;
import org.eclipse.hawkbit.repository.ActionFields;
import org.eclipse.hawkbit.repository.model.Action;
import org.eclipse.hawkbit.repository.model.Action.ActionType;
import org.eclipse.hawkbit.repository.model.Target;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.domain.Specification;

import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

@Features("Component Tests - RSQL filtering")
@Stories("RSQL filter actions")
public class RSQLActionFieldsTest extends AbstractIntegrationTest {

    private Target target;
    private Action action;

    @Before
    public void setupBeforeTest() {
        target = new Target("targetId123");
        target.setDescription("targetId123");
        targetManagement.createTarget(target);
        action = new Action();
        action.setActionType(ActionType.SOFT);
        target.getActions().add(action);
        action.setTarget(target);
        actionRepository.save(action);
        for (int i = 0; i < 10; i++) {
            final Action newAction = new Action();
            newAction.setActionType(ActionType.SOFT);
            newAction.setActive(i % 2 == 0);
            newAction.setTarget(target);
            actionRepository.save(newAction);
            target.getActions().add(newAction);
        }

    }

    @Test
    @Description("Test filter action by id")
    public void testFilterByParameterId() {
        assertRSQLQuery(ActionFields.ID.name() + "==" + action.getId(), 1);
        assertRSQLQuery(ActionFields.ID.name() + "==noExist*", 0);
        assertRSQLQuery(ActionFields.ID.name() + "=in=(" + action.getId() + ",1000000)", 1);
        assertRSQLQuery(ActionFields.ID.name() + "=out=(" + action.getId() + ",1000000)", 10);
    }

    @Test
    @Description("Test action by status")
    public void testFilterByParameterStatus() {
        assertRSQLQuery(ActionFields.STATUS.name() + "==pending", 5);
        assertRSQLQuery(ActionFields.STATUS.name() + "=in=(pending)", 5);
        assertRSQLQuery(ActionFields.STATUS.name() + "=out=(pending)", 6);

        try {
            assertRSQLQuery(ActionFields.STATUS.name() + "==true", 5);
            fail();
        } catch (final RSQLParameterUnsupportedFieldException e) {
        }
    }

    private void assertRSQLQuery(final String rsqlParam, final long expectedEntities) {

        final Specification<Action> parse = RSQLUtility.parse(rsqlParam, ActionFields.class);
        final Slice<Action> findEnitity = deploymentManagement.findActionsByTarget(parse, target, new PageRequest(0,
                100));
        final long countAllEntities = deploymentManagement.countActionsByTarget(parse, target);
        assertThat(findEnitity).isNotNull();
        assertThat(countAllEntities).isEqualTo(expectedEntities);
    }
}
