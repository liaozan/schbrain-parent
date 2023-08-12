package com.schbrain.common.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author liaozan
 * @since 2021/11/19
 */
@Slf4j
public class HostInfoHolder {

    private static final HostInfo HOST_INFO = findFirstNonLoopBackHostInfo();

    public static HostInfo getHostInfo() {
        return HOST_INFO;
    }

    private static HostInfo findFirstNonLoopBackHostInfo() {
        InetAddress address = findFirstNonLoopBackAddress();
        if (address != null) {
            return convertAddress(address);
        }
        log.warn("Cannot find first non-loopBack address, fallback to localhost");
        HostInfo hostInfo = new HostInfo();
        hostInfo.setHostname("localhost");
        hostInfo.setIpAddress("127.0.0.1");
        return hostInfo;
    }

    private static InetAddress findFirstNonLoopBackAddress() {
        InetAddress result = null;
        try {
            int lowest = Integer.MAX_VALUE;
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isUp()) {
                    if (networkInterface.getIndex() < lowest || result == null) {
                        lowest = networkInterface.getIndex();
                    } else {
                        continue;
                    }

                    Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                    while (inetAddresses.hasMoreElements()) {
                        InetAddress address = inetAddresses.nextElement();
                        if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                            result = address;
                        }
                    }
                }
            }
        } catch (IOException ex) {
            log.error("Cannot get first non-loopBack address", ex);
        }

        if (result != null) {
            return result;
        }

        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            log.warn("Unable to retrieve localhost");
        }

        return null;
    }

    private static HostInfo convertAddress(final InetAddress address) {
        HostInfo hostInfo = new HostInfo();
        String hostname = address.getHostName();
        hostInfo.setHostname(hostname);
        hostInfo.setIpAddress(address.getHostAddress());
        return hostInfo;
    }

    @Data
    public static class HostInfo {

        private String ipAddress;

        private String hostname;

    }

}
