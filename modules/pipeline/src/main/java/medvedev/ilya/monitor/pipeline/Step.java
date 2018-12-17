package medvedev.ilya.monitor.pipeline;

public interface Step<Context> {
    void run(Context context) throws Exception;
}
