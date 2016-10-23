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

BUILD_ARTIFACTS=("build/libs" "build/reports" "build/outputs" "build/logs" "build/*.log" "*.log")
BUILD_ARTIFACTS_COLLECTION_DIR="tmp/uploads/artifacts"
BUILD_ARTIFACTS_S3_BUCKET_NAME="ci.forerunnergames.com"
ASSETS=("assets/*" "android/assets/*")
ASSETS_COLLECTION_DIR="tmp/uploads/assets"
PRODUCTION_ASSETS_S3_BUCKET_NAME="assets.peril.forerunner.games"
DEV_ASSETS_S3_BUCKET_NAME="dev-assets.peril.forerunner.games"
LAST_SUCCESSFUL_BUILD_FILE="last-successful-build-number.txt"
LAST_SUCCESSFUL_BUILD_FILE_DIR="tmp/uploads"
COVERAGE_REPORTS_DIR="build/reports/jacoco"
PROJECT_REPO_OWNER="forerunnergames"
ROOT_PROJECT=$(sed -n -e 's/^projectName=//p' gradle.properties)
SUBPROJECTS=($(sed -n -e 's/^include //p' settings.gradle | tr -d ",\"" | tr ' ' "\n"))
BUILD_SETTINGS=true
