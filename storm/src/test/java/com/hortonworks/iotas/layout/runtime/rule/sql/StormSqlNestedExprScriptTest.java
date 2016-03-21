package com.hortonworks.iotas.layout.runtime.rule.sql;

import com.hortonworks.iotas.common.IotasEvent;
import com.hortonworks.iotas.common.IotasEventImpl;
import com.hortonworks.iotas.common.Schema;
import com.hortonworks.iotas.layout.design.rule.condition.Condition;
import com.hortonworks.iotas.layout.runtime.rule.condition.expression.StormSqlExpression;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StormSqlNestedExprScriptTest {

    StormSqlScript<Boolean> stormSqlScript;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testBasic() throws Exception {
        Condition condition = new Condition();
        Condition.ConditionElement conditionElement = new Condition.ConditionElement();
        conditionElement.setFirstOperand(Schema.Field.of("x", Schema.Type.INTEGER));
        conditionElement.setOperation(Condition.ConditionElement.Operation.NOT_EQUAL);
        conditionElement.setSecondOperand("100");
        condition.setConditionElements(Arrays.asList(conditionElement));
        stormSqlScript = new StormSqlScript<Boolean>(new StormSqlExpression(condition), new StormSqlEngine(),
                                                     new StormSqlScript.ValuesToBooleanConverter());

        Map<String, Object> kv = new HashMap<>();
        kv.put("x", 100);
        IotasEvent event = new IotasEventImpl(kv, "1");
        Boolean result = stormSqlScript.evaluate(event);
        assertFalse(result);
    }


    @Test
    public void testEvaluateNestedMap() throws Exception {
        Condition condition = new Condition();
        Condition.ConditionElement conditionElement = new Condition.ConditionElement();
        conditionElement.setFirstOperand(Schema.Field.of("y['b']", Schema.Type.NESTED));
        conditionElement.setOperation(Condition.ConditionElement.Operation.LESS_THAN);
        conditionElement.setSecondOperand("100");
        condition.setConditionElements(Collections.singletonList(conditionElement));
        stormSqlScript = new StormSqlScript<Boolean>(new StormSqlExpression(condition), new StormSqlEngine(),
                                                     new StormSqlScript.ValuesToBooleanConverter());

        Map<String, Object> nested = new HashMap<>();
        nested.put("a", 5);
        nested.put("b", 10);
        Map<String, Object> kv = new HashMap<>();
        kv.put("x", 10);
        kv.put("y", nested);
        IotasEvent event = new IotasEventImpl(kv, "1");
        Boolean result = stormSqlScript.evaluate(event);
        assertTrue(result);
    }


    @Test
    public void testEvaluateNestedMapList() throws Exception {
        Condition condition = new Condition();
        Condition.ConditionElement conditionElement = new Condition.ConditionElement();
        conditionElement.setFirstOperand(Schema.Field.of("y['a'][0]", Schema.Type.NESTED));
        conditionElement.setOperation(Condition.ConditionElement.Operation.LESS_THAN);
        conditionElement.setSecondOperand("100");
        condition.setConditionElements(Collections.singletonList(conditionElement));
        stormSqlScript = new StormSqlScript<Boolean>(new StormSqlExpression(condition), new StormSqlEngine(),
                                                     new StormSqlScript.ValuesToBooleanConverter());

        List<Integer> nestedList = new ArrayList<>();
        nestedList.add(500);
        nestedList.add(1);
        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("a", nestedList);
        Map<String, Object> kv = new HashMap<>();
        kv.put("x", 10);
        kv.put("y", nestedMap);
        IotasEvent event = new IotasEventImpl(kv, "1");
        Boolean result = stormSqlScript.evaluate(event);
        assertFalse(result);
    }
}