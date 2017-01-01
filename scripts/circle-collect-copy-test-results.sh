#!/usr/bin/env bash
#
# Copyright © 2016 Forerunner Games, LLC.
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

if [[ $CIRCLECI = true && $CIRCLE_PROJECT_USERNAME = $PROJECT_REPO_OWNER && $CIRCLE_PROJECT_REPONAME = $ROOT_PROJECT ]]
then
  mkdir -p "$CIRCLE_TEST_REPORTS/junit/"
  find . -type f -regex ".*/test-results/.*xml" -exec cp {} $CIRCLE_TEST_REPORTS/junit/ \;
else
  printf "\n"
  printf "Skipping test results copying. Environment does not match requirements.\n\n"
  printf "Relevant environment variables:\n\n"
  printf "  \$CIRCLECI = [$CIRCLECI] (expects: [true])\n"
  printf "  \$CIRCLE_PROJECT_USERNAME = [$CIRCLE_PROJECT_USERNAME] (expects: [$PROJECT_REPO_OWNER])\n"
  printf "  \$CIRCLE_PROJECT_REPONAME = [$CIRCLE_PROJECT_REPONAME] (expects: [$ROOT_PROJECT])\n"
  printf "\n"
  printf "See https://circleci.com/docs/environment-variables for more information.\n\n"
fi
