/*
 * Copyright © 2011 - 2013 Aaron Mahan.
 * Copyright © 2013 - 2016 Forerunner Games, LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.forerunnergames.peril.client.ui.screens.game.play.modes.classic.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import com.forerunnergames.peril.common.game.DieOutcome;
import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.common.Strings;

import java.util.EnumMap;
import java.util.Map;

public final class DiceArrow implements Comparable <DiceArrow>
{
  public static final DieOutcome DEFAULT_ATTACKER_OUTCOME = DieOutcome.NONE;
  private final int index;
  private final Image image;
  private final BattlePopupWidgetFactory widgetFactory;
  private final Map <DieOutcome, Drawable> attackerOutcomesToDrawables = new EnumMap <> (DieOutcome.class);
  private DieOutcome attackerOutcome = DEFAULT_ATTACKER_OUTCOME;

  public DiceArrow (final int index, final Image image, final BattlePopupWidgetFactory widgetFactory)
  {
    Arguments.checkIsNotNull (image, "image");
    Arguments.checkIsNotNull (widgetFactory, "widgetFactory");

    this.index = index;
    this.image = image;
    this.widgetFactory = widgetFactory;

    refreshDrawablesCache ();
  }

  public void setOutcome (final DieOutcome attackerOutcome, final DieOutcome defenderOutcome)
  {
    Arguments.checkIsNotNull (attackerOutcome, "attackerOutcome");
    Arguments.checkIsNotNull (defenderOutcome, "defenderOutcome");

    if (defenderOutcome == DieOutcome.NONE) return;

    this.attackerOutcome = attackerOutcome;
    image.setDrawable (attackerOutcomesToDrawables.get (attackerOutcome));
  }

  public void reset ()
  {
    attackerOutcome = DEFAULT_ATTACKER_OUTCOME;
    image.setDrawable (attackerOutcomesToDrawables.get (attackerOutcome));
  }

  public void refreshAssets ()
  {
    refreshDrawablesCache ();

    image.setDrawable (attackerOutcomesToDrawables.get (attackerOutcome));
  }

  public Actor asActor ()
  {
    return image;
  }

  private void refreshDrawablesCache ()
  {
    for (final DieOutcome outcome : DieOutcome.values ())
    {
      attackerOutcomesToDrawables.put (outcome, widgetFactory.createBattlePopupDiceArrowDrawable (outcome));
    }
  }

  @Override
  public int compareTo (final DiceArrow o)
  {
    if (index == o.index) return 0;

    return index < o.index ? -1 : 1;
  }

  @Override
  public int hashCode ()
  {
    return index;
  }

  @Override
  public boolean equals (final Object obj)
  {
    if (this == obj) return true;
    if (obj == null || getClass () != obj.getClass ()) return false;

    return index == ((DiceArrow) obj).index;
  }

  @Override
  public String toString ()
  {
    return Strings.format ("{}: Index: {} | Outcome: {}", getClass ().getSimpleName (), index, attackerOutcome);
  }
}
