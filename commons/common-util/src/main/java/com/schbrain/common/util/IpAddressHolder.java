package com.schbrain.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Optional;

/**
 * @author liaozan
 * @since 2021/11/19
 */
@Slf4j
public class IpAddressHolder {

    private static final String POD_IP = System.getenv("POD_IP");
    private static final String LOCAL_IP = Optional.ofNullable(findFirstNonLoopBackAddress()).map(InetAddress::getHostAddress).orElse("127.0.0.1");

    public static String getLocalIp() {
        return getLocalIp(LOCAL_IP);
    }

    public static String getLocalIp(String fallback) {
        return POD_IP == null ? fallback : POD_IP;
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

}
