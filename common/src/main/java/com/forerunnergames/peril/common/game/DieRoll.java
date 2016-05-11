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

package com.forerunnergames.peril.common.game;

import com.forerunnergames.tools.common.Arguments;
import com.forerunnergames.tools.net.annotations.RequiredForNetworkSerialization;

import com.google.common.base.Function;
import com.google.common.collect.Ordering;

import java.util.Comparator;

public final class DieRoll
{
  public static final Comparator <DieRoll> DESCENDING_BY_FACE_VALUE = Ordering.from (DieFaceValue.DESCENDING_ORDER)
          .onResultOf (new Function <DieRoll, DieFaceValue> ()
          {
            @Override
            public DieFaceValue apply (final DieRoll input)
            {
              return input.getDieValue ();
            }
          });

  private final DieFaceValue dieFaceValue;
  private final DieOutcome dieOutcome;

  public DieRoll (final DieFaceValue dieFaceValue, final DieOutcome dieOutcome)
  {
    Arguments.checkIsNotNull (dieFaceValue, "dieFaceValue");
    Arguments.checkIsNotNull (dieOutcome, "dieOutcome");

    this.dieFaceValue = dieFaceValue;
    this.dieOutcome = dieOutcome;
  }

  @RequiredForNetworkSerialization
  public DieRoll ()
  {
    dieFaceValue = null;
    dieOutcome = null;
  }

  public DieFaceValue getDieValue ()
  {
    return dieFaceValue;
  }

  public DieOutcome getOutcome ()
  {
    return dieOutcome;
  }
}
