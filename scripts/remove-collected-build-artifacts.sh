#!/usr/bin/env bash
#
# Copyright © 2013 - 2017 Forerunner Games, LLC.
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

printf "\nRemoving collected build artifacts...\n\n"
printf "Directory:\n\n"
printf "  %s\n\n" "`pwd`/$BUILD_ARTIFACTS_COLLECTION_DIR"

find "$BUILD_ARTIFACTS_COLLECTION_DIR" -mindepth 1 -delete
rmdir -p "$BUILD_ARTIFACTS_COLLECTION_DIR" >/dev/null 2>&1
