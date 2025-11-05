package scouter.plugin.server.none.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * DTO for XLogPack with Lombok Builder
 */
@Getter
@Builder
public class XLogPackDTO {
    private long endTime;
    private int objHash;
    private int service;
    private long txid;
    private int threadNameHash;
    private long caller;
    private long gxid;
    private int elapsed;
    private int error;
    private int cpu;
    private int sqlCount;
    private int sqlTime;
    private byte[] ipaddr;
    private int kbytes;
    private int status;
    private long userid;
    private int userAgent;
    private int referer;
    private int group;
    private int apicallCount;
    private int apicallTime;
    private String countryCode;
    private int city;
    private byte xType;
    private int login;
    private int desc;
    private int webHash;
    private int webTime;
    private byte hasDump;
    private String text1;
    private String text2;
    private int queuingHostHash;
    private int queuingTime;
    private int queuing2ndHostHash;
    private int queuing2ndTime;
    private String text3;
    private String text4;
    private String text5;
    private int profileCount;
    private boolean b3Mode;
    private int profileSize;
    private byte discardType;
    private boolean ignoreGlobalConsequentSampling;
}
