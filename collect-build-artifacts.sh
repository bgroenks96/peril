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

ROOT_DIR="build/uploaded"

mkdir -p $ROOT_DIR

for MODULE in android client common core desktop integration ios server
do
  rsync --archive --prune-empty-dirs $MODULE/build/libs $ROOT_DIR/$MODULE/ >/dev/null 2>&1
  rsync --archive --prune-empty-dirs $MODULE/build/reports $ROOT_DIR/$MODULE/ >/dev/null 2>&1
done

rsync --archive build/reports $ROOT_DIR/peril/ >/dev/null 2>&1
rsync --archive android/build/outputs $ROOT_DIR/android/ >/dev/null 2>&1
rsync --archive integration/test-latest.log $ROOT_DIR/integration/ >/dev/null 2>&1
