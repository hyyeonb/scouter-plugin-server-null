package scouter.plugin.server.none;

import scouter.lang.pack.*;
import scouter.lang.plugin.PluginConstants;
import scouter.lang.plugin.annotation.ServerPlugin;
import scouter.lang.step.*;
import scouter.plugin.server.none.util.TextService;
import scouter.server.Configure;

import java.util.List;

/**
 * @author Gun Lee (gunlee01@gmail.com) on 2016. 3. 19.
 * Enhanced to parse all Scouter data types including profile messages
 */
public class NullPlugin {
    Configure conf = Configure.getInstance();

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_ALERT)
    public void alert(AlertPack pack){
        if(conf.getBoolean("ext_plugin_null_alert_enabled", true)) {
            println("[NullPlugin-alert] " + pack);
            printPackDetails("AlertPack", pack);
        }
    }

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_COUNTER)
    public void counter(PerfCounterPack pack){
        if(conf.getBoolean("ext_plugin_null_counter_enabled", true)) {
            println("[NullPlugin-counter] " + pack);
            printPackDetails("PerfCounterPack", pack);
        }
    }

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_OBJECT)
    public void object(ObjectPack pack){
        if(conf.getBoolean("ext_plugin_null_object_enabled", true)) {
            println("[NullPlugin-object] " + pack);
            printPackDetails("ObjectPack", pack);
        }
    }

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_SUMMARY)
    public void summary(SummaryPack pack){
        if(conf.getBoolean("ext_plugin_null_summary_enabled", true)) {
            println("[NullPlugin-summary] " + pack);
            printPackDetails("SummaryPack", pack);
        }
    }

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_XLOG)
    public void xlog(XLogPack pack){
        if(conf.getBoolean("ext_plugin_null_xlog_enabled", true)) {
            println("[NullPlugin-xlog] " + pack);
            printPackDetails("XLogPack", pack);
        }
    }

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_PROFILE)
    public void profile(XLogProfilePack pack){
        if(conf.getBoolean("ext_plugin_null_profile_enabled", true)) {
            println("[NullPlugin-profile] " + pack);
            printPackDetails("XLogProfilePack", pack);
            parseProfileSteps(pack);
        }
    }

    @ServerPlugin(PluginConstants.PLUGIN_SERVER_TEXT)
    public void text(TextPack pack){
        if(conf.getBoolean("ext_plugin_null_text_enabled", true)) {
            // TextPack을 TextService에 저장하여 hash -> text 매핑 관리
            TextService.put(pack);
            println("[NullPlugin-text] " + pack);
            printPackDetails("TextPack", pack);
        }
    }

    // Additional Pack types handlers
    public void mapPack(MapPack pack){
        if(conf.getBoolean("ext_plugin_null_map_enabled", false)) {
            println("[NullPlugin-map] " + pack);
            printPackDetails("MapPack", pack);
        }
    }

    public void batchPack(BatchPack pack){
        if(conf.getBoolean("ext_plugin_null_batch_enabled", false)) {
            println("[NullPlugin-batch] " + pack);
            printPackDetails("BatchPack", pack);
        }
    }

    public void droppedXLogPack(DroppedXLogPack pack){
        if(conf.getBoolean("ext_plugin_null_dropped_xlog_enabled", false)) {
            println("[NullPlugin-dropped-xlog] " + pack);
            printPackDetails("DroppedXLogPack", pack);
        }
    }

    public void interactionPerfCounter(InteractionPerfCounterPack pack){
        if(conf.getBoolean("ext_plugin_null_interaction_counter_enabled", false)) {
            println("[NullPlugin-interaction-counter] " + pack);
            printPackDetails("InteractionPerfCounterPack", pack);
        }
    }

    public void spanContainer(SpanContainerPack pack){
        if(conf.getBoolean("ext_plugin_null_span_container_enabled", false)) {
            println("[NullPlugin-span-container] " + pack);
            printPackDetails("SpanContainerPack", pack);
        }
    }

    public void span(SpanPack pack){
        if(conf.getBoolean("ext_plugin_null_span_enabled", false)) {
            println("[NullPlugin-span] " + pack);
            printPackDetails("SpanPack", pack);
        }
    }

    public void stack(StackPack pack){
        if(conf.getBoolean("ext_plugin_null_stack_enabled", false)) {
            println("[NullPlugin-stack] " + pack);
            printPackDetails("StackPack", pack);
        }
    }

    public void status(StatusPack pack){
        if(conf.getBoolean("ext_plugin_null_status_enabled", false)) {
            println("[NullPlugin-status] " + pack);
            printPackDetails("StatusPack", pack);
        }
    }

    /**
     * Parse profile steps from XLogProfilePack
     */
    private void parseProfileSteps(XLogProfilePack pack) {
        if(!conf.getBoolean("ext_plugin_null_profile_detail_enabled", true)) {
            return;
        }

        try {
            if(pack.profile != null && pack.profile.length > 0) {
                println("  [Profile Steps] Parsing " + pack.profile.length + " bytes");
                List steps = Step.toObjectList(pack.profile);

                if(steps != null && steps.size() > 0) {
                    println("  [Profile Steps] Found " + steps.size() + " steps");

                    for(int i = 0; i < steps.size(); i++) {
                        Step step = (Step) steps.get(i);
                        printStepDetails(i, step);
                    }
                } else {
                    println("  [Profile Steps] No steps found");
                }
            }
        } catch (Exception e) {
            println("  [Profile Steps] Error parsing steps: " + e.getMessage());
            if(conf.getBoolean("ext_plugin_null_debug", true)) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Print detailed information about each step type
     */
    private void printStepDetails(int index, Step step) {
        if(step == null) {
            println("    [" + index + "] null step");
            return;
        }

        String stepInfo = "    [" + index + "] " + step.getClass().getSimpleName() + " (type=" + step.getStepType() + ")";

        // Print specific details based on step type
        if(step instanceof MethodStep) {
            MethodStep ms = (MethodStep) step;
            String methodName = TextService.getMethodName(ms.hash);
            stepInfo += " hash=" + ms.hash + " method=" + methodName + " elapsed=" + ms.elapsed + " cputime=" + ms.cputime;
        } else if(step instanceof MethodStep2) {
            MethodStep2 ms = (MethodStep2) step;
            String methodName = TextService.getMethodName(ms.hash);
            stepInfo += " hash=" + ms.hash + " method=" + methodName + " elapsed=" + ms.elapsed;
        } else if(step instanceof SqlStep) {
            SqlStep ss = (SqlStep) step;
            String sql = TextService.getSql(ss.hash);
            stepInfo += " hash=" + ss.hash + " sql=" + sql + " elapsed=" + ss.elapsed + " error=" + ss.error;
        } else if(step instanceof SqlStep2) {
            SqlStep2 ss = (SqlStep2) step;
            String sql = TextService.getSql(ss.hash);
            stepInfo += " hash=" + ss.hash + " sql=" + sql + " elapsed=" + ss.elapsed + " error=" + ss.error;
        } else if(step instanceof SqlStep3) {
            SqlStep3 ss = (SqlStep3) step;
            String sql = TextService.getSql(ss.hash);
            stepInfo += " hash=" + ss.hash + " sql=" + sql + " elapsed=" + ss.elapsed;
        } else if(step instanceof MessageStep) {
            MessageStep ms = (MessageStep) step;
            String message = TextService.getHashMessage(ms.hash);
            stepInfo += " hash=" + ms.hash + " message=" + message + " time=" + ms.time + " value=" + ms.value;
        } else if(step instanceof HashedMessageStep) {
            HashedMessageStep hms = (HashedMessageStep) step;
            String message = TextService.getHashMessage(hms.hash);
            stepInfo += " hash=" + hms.hash + " message=" + message + " time=" + hms.time + " value=" + hms.value;
        } else if(step instanceof ParameterizedMessageStep) {
            ParameterizedMessageStep pms = (ParameterizedMessageStep) step;
            stepInfo += " hash=" + pms.hash + " time=" + pms.time;
        } else if(step instanceof ApiCallStep) {
            ApiCallStep acs = (ApiCallStep) step;
            String apiCall = TextService.getApiCallName(acs.hash);
            stepInfo += " hash=" + acs.hash + " api=" + apiCall + " elapsed=" + acs.elapsed + " error=" + acs.error;
        } else if(step instanceof ApiCallStep2) {
            ApiCallStep2 acs = (ApiCallStep2) step;
            String apiCall = TextService.getApiCallName(acs.hash);
            stepInfo += " hash=" + acs.hash + " api=" + apiCall + " elapsed=" + acs.elapsed;
        } else if(step instanceof SocketStep) {
            SocketStep ss = (SocketStep) step;
            stepInfo += " ipaddr=" + ss.ipaddr + " port=" + ss.port + " elapsed=" + ss.elapsed;
        } else if(step instanceof SpanStep) {
            SpanStep ss = (SpanStep) step;
            stepInfo += " hash=" + ss.hash + " elapsed=" + ss.elapsed;
        } else if(step instanceof SpanCallStep) {
            SpanCallStep scs = (SpanCallStep) step;
            stepInfo += " hash=" + scs.hash + " elapsed=" + scs.elapsed;
        } else if(step instanceof ThreadSubmitStep) {
            ThreadSubmitStep tss = (ThreadSubmitStep) step;
            stepInfo += " hash=" + tss.hash + " elapsed=" + tss.elapsed;
        } else if(step instanceof ThreadCallPossibleStep) {
            ThreadCallPossibleStep tcps = (ThreadCallPossibleStep) step;
            stepInfo += " hash=" + tcps.hash + " elapsed=" + tcps.elapsed;
        } else if(step instanceof DispatchStep) {
            DispatchStep ds = (DispatchStep) step;
            stepInfo += " hash=" + ds.hash + " elapsed=" + ds.elapsed;
        } else if(step instanceof DumpStep) {
            DumpStep ds = (DumpStep) step;
            stepInfo += " hash=" + ds.hash + " elapsed=" + ds.elapsed;
        } else if(step instanceof MethodSum) {
            MethodSum ms = (MethodSum) step;
            stepInfo += " hash=" + ms.hash + " count=" + ms.count + " elapsed=" + ms.elapsed;
        } else if(step instanceof SqlSum) {
            SqlSum ss = (SqlSum) step;
            stepInfo += " hash=" + ss.hash + " count=" + ss.count + " elapsed=" + ss.elapsed;
        } else if(step instanceof MessageSum) {
            MessageSum ms = (MessageSum) step;
            stepInfo += " hash=" + ms.hash + " count=" + ms.count;
        } else if(step instanceof ApiCallSum) {
            ApiCallSum acs = (ApiCallSum) step;
            stepInfo += " hash=" + acs.hash + " count=" + acs.count + " elapsed=" + acs.elapsed;
        } else if(step instanceof SocketSum) {
            SocketSum ss = (SocketSum) step;
            stepInfo += " count=" + ss.count + " elapsed=" + ss.elapsed;
        }

        println(stepInfo);
    }

    /**
     * Print common pack details
     */
    private void printPackDetails(String packType, Object pack) {
        if(!conf.getBoolean("ext_plugin_null_detail_enabled", false)) {
            return;
        }

        println("  [Details] " + packType + " = " + pack.toString());
    }

    private void println(Object o) {
        if(conf.getBoolean("ext_plugin_null_debug", true)) { //default false normally, but true now for test purpose.
            System.out.println(o);
        }
    }
}
