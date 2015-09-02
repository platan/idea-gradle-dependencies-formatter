package com.github.platan.idea.dependencies.maven;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

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
