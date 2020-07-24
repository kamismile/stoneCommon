/**
 * LY.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.github.kamismile.stone.commmon.vertx.definition;

import java.util.Objects;

/**
 * @author dong.li
 * @version $Id: ConsumerVersion, v 0.1 2020/4/3 14:08 dong.li Exp $
 */
public class ConsumerVersion {


    private short major;

    private short minor;

    private short patch;

    private short build;

    private String version;

    private long numberVersion;


    public ConsumerVersion(String version) {
        validateVersion(version);
    }


    public ConsumerVersion(Long numberVersion) {
        String version = getVersion(numberVersion);

        validateVersion(version);
    }

    private void validateVersion(String version) {
        Objects.requireNonNull(version);

        String[] versions = version.split("\\.", -1);
        if (versions.length != 4) {
            throw new IllegalStateException(String.format("Invalid version \"%s\". must be XXX.XXX.XXX.XXX", version));
        }

        this.major = parseNumber("major", version, versions[0]);
        this.minor = parseNumber("minor", version, versions[1]);
        this.patch = parseNumber("patch", version, versions[2]);
        this.build = parseNumber("build", version, versions[3]);
        this.version = combineStringVersion();
        this.numberVersion = combineVersion();
    }


    private String getVersion(Long numberVersion) {
        int[] b = new int[4];
        b[0] = (int) ((numberVersion >> 48) & 0xff);
        b[1] = (int) ((numberVersion >> 32) & 0xff);
        b[2] = (int) ((numberVersion >> 16) & 0xff);
        b[3] = (int) (numberVersion & 0xff);
        return Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2])
                + "." + Integer.toString(b[3]);
    }

    private String combineStringVersion() {
        return major + "." + minor + "." + patch + "." + build;
    }

    private short parseNumber(String name, String allVersion, String version) {
        short value = 0;
        try {
            value = Short.parseShort(version);
        } catch (NumberFormatException e) {
            throw new IllegalStateException(
                    String.format("Invalid %s \"%s\", version \"%s\".", name, version, allVersion), e);
        }

        if (value < 0) {
            throw new IllegalStateException(
                    String.format("%s \"%s\" can not be negative, version \"%s\".", name, version, allVersion));
        }

        return value;
    }

    private long combineVersion() {
        return (long) major << 48 | (long) minor << 32 | (long) patch << 16 | (long) build;
    }

    public long getNumberVersion() {
        return numberVersion;
    }

    public String getVersion() {
        return version;
    }

}
