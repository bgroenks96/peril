#!/usr/bin/env bash
#
# Copyright © 2011 - 2013 Aaron Mahan.
# Copyright © 2013 - 2016 Forerunner Games, LLC.
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program. If not, see <http://www.gnu.org/licenses/>.
#

if [[ -n $1 ]] ; then BUILD_NUMBER="$1" ; else { echo "Error: Expected last successful build number as 1st argument. Exiting." ; exit 1; } ; fi

THIS_DIR="${BASH_SOURCE%/*}"
[[ ! -d "$THIS_DIR" ]] && THIS_DIR="$PWD"
[[ ! -v $BUILD_SETTINGS ]] && . "$THIS_DIR/build-settings.sh"

FILE="$LAST_SUCCESSFUL_BUILD_FILE_DIR/$LAST_SUCCESSFUL_BUILD_FILE"

printf "\nCreating last successful build number file...\n\n"
printf "File:\n\n"
printf "  %s\n\n" "`pwd`/$FILE"
printf "Last successful build number:\n\n"
printf "  $BUILD_NUMBER\n"

mkdir -p "$LAST_SUCCESSFUL_BUILD_FILE_DIR"
echo "$BUILD_NUMBER" > "$FILE"
