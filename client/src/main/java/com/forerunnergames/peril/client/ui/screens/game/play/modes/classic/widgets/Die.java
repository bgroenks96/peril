package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.forerunnergames.peril.common.game.DieFaceValue;
import com.forerunnergames.tools.common.Arguments;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public interface Die extends Comparable <Die>
{
  enum DieState
  {
    ENABLED (DieRollable.TRUE),
    ENABLING (DieRollable.FALSE),
    DISABLING (DieRollable.FALSE),
    DISABLED (DieRollable.FALSE);

    private static final Table <DieState, DieStateTransition, DieState> TRANSITION_TABLE = HashBasedTable.create ();

    static
    {
      TRANSITION_TABLE.put (ENABLED, DieStateTransition.ON_HOVER_START, DISABLING);
      TRANSITION_TABLE.put (ENABLING, DieStateTransition.ON_HOVER_END, DISABLED);
      TRANSITION_TABLE.put (ENABLING, DieStateTransition.ON_CLICK, ENABLED);
      TRANSITION_TABLE.put (DISABLING, DieStateTransition.ON_CLICK, DISABLED);
      TRANSITION_TABLE.put (DISABLING, DieStateTransition.ON_HOVER_END, ENABLED);
      TRANSITION_TABLE.put (DISABLED, DieStateTransition.ON_HOVER_START, ENABLING);
    }

    private final DieRollable rollable;

    public boolean isRollable ()
    {
      return rollable == DieRollable.TRUE;
    }

    public DieState transition (final DieStateTransition transition,
                                final Table <DieState, DieStateTransition, DieStateTransitionAction> transitionActionsTable)
    {
      Arguments.checkIsNotNull (transition, "transition");
      Arguments.checkIsNotNull (transitionActionsTable, "transitionActionsTable");

      final DieState toState = TRANSITION_TABLE.get (this, transition);

      if (toState == null) return this;

      final DieStateTransitionAction transitionAction = transitionActionsTable.get (this, transition);
      if (transitionAction != null) transitionAction.onTransition (toState);

      return toState;
    }

    DieState (final DieRollable rollable)
    {
      this.rollable = rollable;
    }
  }

  enum DieStateTransition
  {
    ON_HOVER_START,
    ON_HOVER_END,
    ON_CLICK
  }

  enum DieRollable
  {
    TRUE,
    FALSE
  }

  int getIndex ();

  void roll (final DieFaceValue faceValue);

  void enable ();

  void disable ();

  void setTouchable (boolean isTouchable);

  void addListener (final DieListener listener);

  void reset ();

  void refreshAssets ();

  Actor asActor ();

  @Override
  int hashCode ();

  @Override
  boolean equals (final Object obj);

  @Override
  String toString ();

  interface DieStateTransitionAction
  {
    void onTransition (final DieState toState);
  }
}
