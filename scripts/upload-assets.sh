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

if [[ ! -v "$1" ]]
then
  printf "\n"
  printf "Skipping asset uploading. Amazon S3 bucket name was not specified as the first argument to upload-assets.sh."
  printf "\n\n"
  exit 1
fi

SOURCE="$ASSETS_COLLECTION_DIR/"
DEST="s3://$1/"

printf "\nUploading assets...\n\n"
printf "Note:\n\n"
printf "  Assets will be synced based on existence, md5 checksum, & size. \n"
printf "  Assets not present in source will be deleted in destination.\n\n"
printf "Source:\n\n"
printf "  %s\n\n" "`pwd`/$SOURCE"
printf "Destination:\n\n"
printf "  $DEST\n\n"

s3cmd sync --recursive --delete-removed --guess-mime-type --no-mime-magic --no-progress "$SOURCE" "$DEST"
