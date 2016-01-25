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

package com.forerunnergames.peril.common.settings;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class GameSettingsTest
{
  @Test
  public void testValidMapNamePatternPassesNoSpacesAllLowerCase ()
  {
    final String testString = "mapname";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNoSpacesAllUpperCase ()
  {
    final String testString = "MAPNAME";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNoSpacesMixedCase ()
  {
    final String testString = "MapName";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesSpacesMixedCase ()
  {
    final String testString = "A Map name";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNumbers ()
  {
    final String testString = "A m4p n4me";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNumbersOnly ()
  {
    final String testString = "1234";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNumbersBeginning ()
  {
    final String testString = "1234 Test Map Name";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesNumbersEnd ()
  {
    final String testString = "Test Map Name 23487";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesMinLengthNumbersOnly ()
  {
    final String testString = "12";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesMinLengthLowercaseLettersOnly ()
  {
    final String testString = "ab";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternPassesMinLengthUppercaseLettersOnly ()
  {
    final String testString = "ZQ";

    assertTrue (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsMoreThanOneSpaceBetweenWords ()
  {
    final String testString = "A Map  name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsSingleSpaceBeginning ()
  {
    final String testString = " A Map name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsSingleSpaceEnd ()
  {
    final String testString = "A Map name ";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsMultipleSpacesBeginning ()
  {
    final String testString = "   A Map name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsMultipleSpacesEnd ()
  {
    final String testString = "A Map name   ";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsIllegalCharactersMiddle ()
  {
    final String testString = "A Map @#$& name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsIllegalCharacterBeginning ()
  {
    final String testString = "!A Map name";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsIllegalCharacterEnd ()
  {
    final String testString = "A Map name]";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsBeyondMaxLength ()
  {
    final String testString = "A Map name that is way too long to be Valid";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidMapNamePatternFailsLessThanMinLength ()
  {
    final String testString = "";

    assertFalse (GameSettings.VALID_MAP_NAME_PATTERN.matcher (testString).matches ());
  }
}
