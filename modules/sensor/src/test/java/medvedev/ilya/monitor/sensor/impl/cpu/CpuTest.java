package medvedev.ilya.monitor.sensor.impl.cpu;

import medvedev.ilya.monitor.pipeline.Pipeline;
import medvedev.ilya.monitor.sensor.Sensor;
import medvedev.ilya.monitor.sensor.SensorInfo;
import medvedev.ilya.monitor.sensor.SensorValue;
import medvedev.ilya.monitor.sensor.impl.exception.SensorFileNotFound;
import medvedev.ilya.monitor.sensor.test.Context;
import medvedev.ilya.monitor.sensor.test.matcher.SensorInfoMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class CpuTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test(expected = SensorFileNotFound.class)
    public void fileNotFound() throws Exception {
        Cpu.byFile(Paths.get(this.getClass().getResource("/proc").toURI()).resolve("not-found").toFile());
    }

    @Test(expected = SensorFileNotFound.class)
    public void fileRemovedAfterInit() throws Exception {
        File file = temporaryFolder.newFile();
        Sensor sensor = sensorInit(file);
        temporaryFolder.delete();

        sensor.sensorInfo();
    }

    @Test
    public void updateValueBeforeFirstResultTest() throws Exception {
        testPipeline(Pipeline.create(this::updateAndCheck).andThen(this::checkZeroValue));
    }

    @Test
    public void updateValueAfterFirstResultTest() throws Exception {
        testPipeline(Pipeline.create(this::checkZeroValue).andThen(this::updateAndCheck));
    }

    private void testPipeline(Pipeline<Context> pipeline) throws Exception {
        Pipeline<Context> fullPipeline = Pipeline.create(this::checkEmptyValue).andThen(pipeline);
        File file = temporaryFolder.newFile();
        Sensor sensor = sensorInit(file);

        fullPipeline.andThen(this::cleanSensor).andThen(fullPipeline)
                .run(Context.builder()
                        .file(file)
                        .sensor(sensor)
                        .build());
    }

    private Sensor sensorInit(File file) throws Exception {
        Files.copy(this.getClass().getResourceAsStream("/proc/stat"), file.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        return Cpu.byFile(file);
    }

    private void cleanSensor(Context context) {
        context.getSensor().clean();
    }

    private void checkEmptyValue(Context context) {
        Assert.assertThat(context.getSensor().sensorInfo(), SensorInfoMatcher.equal(SensorInfo.builder()
                .name("cpu")
                .values(Collections.emptyList())
                .build()));
    }

    private void checkZeroValue(Context context) {
        Assert.assertThat(context.getSensor().sensorInfo(), SensorInfoMatcher.equal(SensorInfo.builder()
                .name("cpu")
                .values(Stream.concat(Stream.of(""), IntStream.range(0, 6).mapToObj(Integer::toString))
                        .map(i -> SensorValue.builder()
                                .name("cpu" + i)
                                .value(0F)
                                .build())
                        .collect(Collectors.toList()))
                .build()));
    }

    private void updateAndCheck(Context context) {
        IntStream.range(0, 2)
                .mapToObj(i -> Pipeline.create(this::updateSensorInfo)
                        .andThen(this::checkAfterUpdate))
                .reduce(Pipeline::andThen)
                .ifPresent(pipeline -> pipeline.run(context));
    }

    private void checkAfterUpdate(Context context) {
        Assert.assertThat(context.getSensor().sensorInfo(), SensorInfoMatcher.equal(SensorInfo.builder()
                .name("cpu")
                .values(Stream.concat(
                        Stream.of(SensorValue.builder()
                                .name("cpu")
                                .value(0F)),
                        IntStream.rangeClosed(0, 5)
                                .mapToObj(i -> SensorValue.builder()
                                        .name("cpu" + i)
                                        .value(i % 2 == 0 ? 25F : 0F))
                )
                        .map(SensorValue.SensorValueBuilder::build)
                        .collect(Collectors.toList()))
                .build()));
    }

    private void updateSensorInfo(Context context) throws Exception {
        Path path = context.getFile().toPath();
        List<String> buffer = Files.readAllLines(path).stream()
                .map(line -> {
                    int firstSpace = line.indexOf(' ');
                    String name = line.substring(0, firstSpace);
                    if (name.length() > 3 && name.startsWith("cpu")) {
                        String cpuNumberString = name.substring(3);
                        int cpuNumber = Integer.parseInt(cpuNumberString);
                        if (cpuNumber % 2 == 0) {
                            String[] params = line.substring(firstSpace + 1).split(" ", -1);
                            List<Long> metrics = Arrays.stream(params)
                                    .limit(9)
                                    .map(Long::parseLong)
                                    .collect(Collectors.toList());
                            return Stream.of(
                                    Stream.of(name),
                                    Stream.of(
                                            metrics.stream().limit(3).map(metric -> metric + 1),
                                            metrics.stream().skip(3).limit(1)
                                                    .map(metric -> metric + ((metrics.size() - 1) * 3)),
                                            metrics.stream().skip(4).map(metric -> metric + 1)
                                    ).flatMap(Function.identity()).map(metric -> Long.toString(metric)),
                                    Arrays.stream(params).skip(9)
                            ).flatMap(Function.identity()).collect(Collectors.joining(" "));
                        }
                    }

                    return line;
                })
                .collect(Collectors.toList());
        Files.write(path, buffer);
    }
}