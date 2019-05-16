package com.betanzos.jsch;

import java.util.Arrays;

public enum ChannelType {
    SESSION("session"),
    SHELL("shell"),
    WRITABLE_SHELL("shell-writable"),
    EXEC("exec"),
    X11("x11"),
    AUTH_AGENT_OPENSSH_COM("auth-agent@openssh.com"),
    DIRECT_TCP_IP("direct-tcpip"),
    FORWARDED_TCP_IP("forwarded-tcpip"),
    SFTP("sftp"),
    SUBSYSTEM("subsystem");

    private final String value;

    ChannelType(String value) {
        this.value = value;
    }

    public static ChannelType forStringValue(String value) {
        return Arrays.stream(values())
                .filter(literal -> literal.value.equals(value))
                .findFirst()
                .orElse(null);
    }
}
