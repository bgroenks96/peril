package com.forerunnergames.peril.core.model.state;

import com.stateforge.statemachine.context.IContextEnd;
import com.stateforge.statemachine.listener.IObserver;

public interface StateMachineListener extends IObserver, IContextEnd
{
}
