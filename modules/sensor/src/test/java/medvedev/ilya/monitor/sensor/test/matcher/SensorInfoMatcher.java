package medvedev.ilya.monitor.sensor.test.matcher;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import medvedev.ilya.monitor.sensor.SensorInfo;
import medvedev.ilya.monitor.sensor.SensorValue;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorInfoMatcher extends BaseMatcher<SensorInfo> {
    private final String expectedName;
    private final int expectedValuesSize;
    private final List<SensorValue> expectedValues;

    public static SensorInfoMatcher equal(SensorInfo expected) {
        List<SensorValue> expectedValues = expected.getValues();
        return new SensorInfoMatcher(expected.getName(), expectedValues.size(), expectedValues);
    }

    @Override
    public boolean matches(Object actual) {
        if (!(actual instanceof SensorInfo)) {
            return false;
        }
        SensorInfo castedActual = (SensorInfo) actual;
        if (!Objects.equals(expectedName, castedActual.getName())) {
            return false;
        }
        List<SensorValue> actualValues = castedActual.getValues();
        if (expectedValuesSize != actualValues.size()) {
            return false;
        }
        Iterator<SensorValue> expectedValuesIterator = expectedValues.iterator();
        Iterator<SensorValue> actualValuesIterator = actualValues.iterator();

        while (expectedValuesIterator.hasNext()) {
            SensorValue expectedValue = expectedValuesIterator.next();
            SensorValue actualValue = actualValuesIterator.next();

            if (!Objects.equals(expectedValue.getName(), actualValue.getName())
                    || !Objects.equals(expectedValue.getValue(), actualValue.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("sensorInfo(")
                .appendValue(expectedName)
                .appendValue(": ")
                .appendValue(expectedValues)
                .appendText(")");
    }
}
