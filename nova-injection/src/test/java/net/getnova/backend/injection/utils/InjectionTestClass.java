package net.getnova.backend.injection.utils;

import com.google.inject.Inject;
import lombok.Getter;

public class InjectionTestClass {

    @Inject
    @Getter
    private InjectionTestObject testObject;
}
