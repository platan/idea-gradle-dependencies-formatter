package com.github.platan.idea.dependencies.maven;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum Scope {

    @XmlEnumValue("compile")
    COMPILE,
    @XmlEnumValue("provided")
    PROVIDED,
    @XmlEnumValue("runtime")
    RUNTIME,
    @XmlEnumValue("test")
    TEST,
    @XmlEnumValue("system")
    SYSTEM
}
