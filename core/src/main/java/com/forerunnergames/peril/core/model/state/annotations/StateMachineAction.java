package com.forerunnergames.peril.core.model.state.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks methods in use as actions by a StateForge state machine, specified in a .fsmjava file.
 */
@Retention (RetentionPolicy.SOURCE)
@Target (ElementType.METHOD)
public @interface StateMachineAction
{
}
