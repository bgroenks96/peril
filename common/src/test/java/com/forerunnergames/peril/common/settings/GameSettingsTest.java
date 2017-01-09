/*
 * Copyright © 2013 - 2017 Forerunner Games, LLC.
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
  public void testValidPlayMapNamePatternPassesNoSpacesAllLowerCase ()
  {
    final String testString = "playmapname";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesNoSpacesAllUpperCase ()
  {
    final String testString = "PLAYMAPNAME";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesNoSpacesMixedCase ()
  {
    final String testString = "PlayMapName";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesSpacesMixedCase ()
  {
    final String testString = "A Play Map name";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesNumbers ()
  {
    final String testString = "A pl4y m4p n4me";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesNumbersOnly ()
  {
    final String testString = "1234";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesNumbersBeginning ()
  {
    final String testString = "1234 Test Play Map Name";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesNumbersEnd ()
  {
    final String testString = "Test Play Map Name 23487";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesMinLengthNumbersOnly ()
  {
    final String testString = "12";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesMinLengthLowercaseLettersOnly ()
  {
    final String testString = "ab";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternPassesMinLengthUppercaseLettersOnly ()
  {
    final String testString = "ZQ";

    assertTrue (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsMoreThanOneSpaceBetweenWords ()
  {
    final String testString = "A Play Map  name";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsSingleSpaceBeginning ()
  {
    final String testString = " A Play Map name";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsSingleSpaceEnd ()
  {
    final String testString = "A Play Map name ";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsMultipleSpacesBeginning ()
  {
    final String testString = "   A Play Map name";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsMultipleSpacesEnd ()
  {
    final String testString = "A Play Map name   ";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsIllegalCharactersMiddle ()
  {
    final String testString = "A Play Map @#$& name";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsIllegalCharacterBeginning ()
  {
    final String testString = "!A Play Map name";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsIllegalCharacterEnd ()
  {
    final String testString = "A Play Map name]";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsBeyondMaxLength ()
  {
    final String testString = "A Play Map name that is way too long to be Valid";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }

  @Test
  public void testValidPlayMapNamePatternFailsLessThanMinLength ()
  {
    final String testString = "";

    assertFalse (GameSettings.VALID_PLAY_MAP_NAME_PATTERN.matcher (testString).matches ());
  }
}
