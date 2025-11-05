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

    // Note: Additional Pack types like DroppedXLogPack, InteractionPerfCounterPack,
    // SpanContainerPack, SpanPack, StackPack, StatusPack are available in newer versions
    // For Scouter 1.8.3, we focus on the main Pack types above

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
        // Using getter methods to access fields (some fields may be private)
        if(step instanceof MethodStep) {
            MethodStep ms = (MethodStep) step;
            String methodName = TextService.getMethodName(ms.getHash());
            stepInfo += " hash=" + ms.getHash() + " method=" + methodName + " elapsed=" + ms.getElapsed() + " cputime=" + ms.getCputime();
        } else if(step instanceof MethodStep2) {
            MethodStep2 ms = (MethodStep2) step;
            String methodName = TextService.getMethodName(ms.getHash());
            stepInfo += " hash=" + ms.getHash() + " method=" + methodName + " elapsed=" + ms.getElapsed();
        } else if(step instanceof SqlStep) {
            SqlStep ss = (SqlStep) step;
            String sql = TextService.getSql(ss.getHash());
            stepInfo += " hash=" + ss.getHash() + " sql=" + sql + " elapsed=" + ss.getElapsed() + " error=" + ss.getError();
        } else if(step instanceof SqlStep2) {
            SqlStep2 ss = (SqlStep2) step;
            String sql = TextService.getSql(ss.getHash());
            stepInfo += " hash=" + ss.getHash() + " sql=" + sql + " elapsed=" + ss.getElapsed() + " error=" + ss.getError();
        } else if(step instanceof SqlStep3) {
            SqlStep3 ss = (SqlStep3) step;
            String sql = TextService.getSql(ss.getHash());
            stepInfo += " hash=" + ss.getHash() + " sql=" + sql + " elapsed=" + ss.getElapsed();
        } else if(step instanceof MessageStep) {
            MessageStep ms = (MessageStep) step;
            String message = TextService.getHashMessage(ms.hash);
            stepInfo += " hash=" + ms.hash + " message=" + message + " time=" + ms.time + " value=" + ms.value;
        } else if(step instanceof HashedMessageStep) {
            HashedMessageStep hms = (HashedMessageStep) step;
            String message = TextService.getHashMessage(hms.getHash());
            stepInfo += " hash=" + hms.getHash() + " message=" + message + " time=" + hms.getTime() + " value=" + hms.getValue();
        } else if(step instanceof ParameterizedMessageStep) {
            ParameterizedMessageStep pms = (ParameterizedMessageStep) step;
            stepInfo += " hash=" + pms.getHash() + " time=" + pms.time;
        } else if(step instanceof ApiCallStep) {
            ApiCallStep acs = (ApiCallStep) step;
            String apiCall = TextService.getApiCallName(acs.getHash());
            stepInfo += " hash=" + acs.getHash() + " api=" + apiCall + " elapsed=" + acs.getElapsed() + " error=" + acs.getError();
        } else if(step instanceof ApiCallStep2) {
            ApiCallStep2 acs = (ApiCallStep2) step;
            String apiCall = TextService.getApiCallName(acs.getHash());
            stepInfo += " hash=" + acs.getHash() + " api=" + apiCall + " elapsed=" + acs.getElapsed();
        } else if(step instanceof SocketStep) {
            SocketStep ss = (SocketStep) step;
            stepInfo += " ipaddr=" + ss.getIpaddr() + " port=" + ss.getPort() + " elapsed=" + ss.getElapsed();
        } else if(step instanceof ThreadSubmitStep) {
            ThreadSubmitStep tss = (ThreadSubmitStep) step;
            stepInfo += " hash=" + tss.getHash() + " elapsed=" + tss.getElapsed();
        } else if(step instanceof ThreadCallPossibleStep) {
            ThreadCallPossibleStep tcps = (ThreadCallPossibleStep) step;
            stepInfo += " hash=" + tcps.getHash() + " elapsed=" + tcps.getElapsed();
        } else if(step instanceof DispatchStep) {
            DispatchStep ds = (DispatchStep) step;
            stepInfo += " hash=" + ds.getHash() + " elapsed=" + ds.getElapsed();
        } else if(step instanceof DumpStep) {
            DumpStep ds = (DumpStep) step;
            stepInfo += " hash=" + ds.hash + " elapsed=" + ds.elapsed;
        } else if(step instanceof MethodSum) {
            MethodSum ms = (MethodSum) step;
            stepInfo += " hash=" + ms.getHash() + " count=" + ms.getCount() + " elapsed=" + ms.getElapsed();
        } else if(step instanceof SqlSum) {
            SqlSum ss = (SqlSum) step;
            stepInfo += " hash=" + ss.getHash() + " count=" + ss.getCount() + " elapsed=" + ss.getElapsed();
        } else if(step instanceof MessageSum) {
            MessageSum ms = (MessageSum) step;
            stepInfo += " hash=" + ms.hash + " count=" + ms.getCount();
        } else if(step instanceof ApiCallSum) {
            ApiCallSum acs = (ApiCallSum) step;
            stepInfo += " hash=" + acs.getHash() + " count=" + acs.getCount() + " elapsed=" + acs.getElapsed();
        } else if(step instanceof SocketSum) {
            SocketSum ss = (SocketSum) step;
            stepInfo += " count=" + ss.getCount() + " elapsed=" + ss.getElapsed();
        }

        // Note: SpanStep and SpanCallStep are available in newer versions of Scouter

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
