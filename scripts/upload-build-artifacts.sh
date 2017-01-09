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

if [[ -n $1 ]] ; then BRANCH="$1" ; else { echo "Error: Expected branch name of build as 1st argument. Exiting." ; exit 1; } ; fi
if [[ -n $2 ]] ; then BUILD_NUMBER="$2" ; else { echo "Error: Expected build number as 2nd argument. Exiting." ; exit 1; } ; fi

THIS_DIR="${BASH_SOURCE%/*}"
[[ ! -d "$THIS_DIR" ]] && THIS_DIR="$PWD"
[[ ! -v $BUILD_SETTINGS ]] && . "$THIS_DIR/build-settings.sh"

SOURCE="$BUILD_ARTIFACTS_COLLECTION_DIR/"
DEST="s3://$BUILD_ARTIFACTS_S3_BUCKET_NAME/builds/$ROOT_PROJECT/$BRANCH/$BUILD_NUMBER/"

printf "\nUploading build artifacts...\n\n"
printf "Source:\n\n"
printf "  %s\n\n" "`pwd`/$SOURCE"
printf "Destination:\n\n"
printf "  $DEST\n\n"

s3cmd sync --recursive --delete-removed --guess-mime-type --no-mime-magic --no-progress "$SOURCE" "$DEST"
