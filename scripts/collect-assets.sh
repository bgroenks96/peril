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

printf "\nCollecting assets...\n\n"
printf "Working directory:\n\n"
printf "  %s\n\n" `pwd`
printf "Output directory:\n\n"
printf "  %s\n\n" "`pwd`/$ASSETS_COLLECTION_DIR"
printf "Assets to collect:\n\n"
for ASSET in "${ASSETS[@]}"; do printf "  $ASSET\n"; done
printf "\nCollecting:\n\n"

rm -rf "$ASSETS_COLLECTION_DIR"
mkdir -p "$ASSETS_COLLECTION_DIR"

for ASSET in "${ASSETS[@]}"; do
  [[ -z $(find . -path "*$ASSET" -print -quit) ]] && printf "  $ASSET NOT FOUND - SKIPPING\n"
  rsync -am --out-format="%f" $ASSET "$ASSETS_COLLECTION_DIR/" 2>/dev/null | awk -F'\t' '{ if ($1 > 0) print "  " $1 }'
done
