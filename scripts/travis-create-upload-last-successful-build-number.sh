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

if [[ $TRAVIS = true && $TRAVIS_PULL_REQUEST != false && $TRAVIS_REPO_SLUG = "$PROJECT_REPO_OWNER/$ROOT_PROJECT" ]]
then
  ./scripts/create-last-successful-build-number.sh "$TRAVIS_BUILD_NUMBER" &&
  ./scripts/upload-last-successful-build-number.sh "$TRAVIS_BRANCH"
  ./scripts/remove-last-successful-build-number.sh
else
  printf "\n"
  printf "Skipping creating & uploading last successful build number. Environment does not match requirements.\n\n"
  printf "Relevant environment variables:\n\n"
  printf "  \$TRAVIS = [$TRAVIS] (expects: [true])\n"
  printf "  \$TRAVIS_PULL_REQUEST = [$TRAVIS_PULL_REQUEST] (expects: [pull request number])\n"
  printf "  \$TRAVIS_REPO_SLUG = [$TRAVIS_REPO_SLUG] (expects: [$PROJECT_REPO_OWNER/$ROOT_PROJECT])\n"
  printf "\n"
  printf "See https://docs.travis-ci.com/user/environment-variables/#Default-Environment-Variables for more information.\n\n"
fi
