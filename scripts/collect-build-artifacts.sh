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

printf "\nCollecting build artifacts...\n\n"
printf "Working directory:\n\n"
printf "  %s\n\n" `pwd`
printf "Output directory:\n\n"
printf "  %s\n\n" "`pwd`/$BUILD_ARTIFACTS_COLLECTION_DIR"
printf "Projects to collect from:\n\n"
for PROJECT in "$ROOT_PROJECT" "${SUBPROJECTS[@]}"; do printf "  $PROJECT\n"; done
printf "\nArtifacts to collect from each project:\n\n"
for ARTIFACT in "${BUILD_ARTIFACTS[@]}"; do printf "  $ARTIFACT\n"; done
printf "\nCollecting:\n\n"

rm -rf "$BUILD_ARTIFACTS_COLLECTION_DIR"
mkdir -p "$BUILD_ARTIFACTS_COLLECTION_DIR"

for PROJECT in "$ROOT_PROJECT" "${SUBPROJECTS[@]}"; do
  for ARTIFACT in "${BUILD_ARTIFACTS[@]}"; do
    SOURCE=$([ "$PROJECT" == "$ROOT_PROJECT" ] && echo "$ARTIFACT" || echo "$PROJECT/$ARTIFACT")
    DEST=$BUILD_ARTIFACTS_COLLECTION_DIR/$PROJECT
    [[ -z $(find . -path "*$SOURCE" -print -quit) ]] && printf "  $SOURCE NOT FOUND - SKIPPING\n"
    rsync -am --out-format="%f" $SOURCE "$DEST/" 2>/dev/null | awk -F'\t' '{ if ($1 > 0) print "  " $1 }'
  done
done
