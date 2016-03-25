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

THIS_DIR="${BASH_SOURCE%/*}"
[[ ! -d "$THIS_DIR" ]] && THIS_DIR="$PWD"
[[ ! -v $BUILD_SETTINGS ]] && . "$THIS_DIR/build-settings.sh"

printf "\nRemoving last successful build number file...\n\n"
printf "File:\n\n"
printf "  %s\n\n" "`pwd`/$LAST_SUCCESSFUL_BUILD_FILE_DIR/$LAST_SUCCESSFUL_BUILD_FILE"

rm -f "$LAST_SUCCESSFUL_BUILD_FILE_DIR/$LAST_SUCCESSFUL_BUILD_FILE" >/dev/null 2>&1
rmdir -p "$LAST_SUCCESSFUL_BUILD_FILE_DIR" >/dev/null 2>&1
