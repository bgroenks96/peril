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

OUTPUT_DIR="build/collected"
ARTIFACTS=("build/libs" "build/reports" "build/outputs" "build/*.log")
ROOT_PROJECT=$(sed -n -e 's/^projectName=//p' gradle.properties)
SUBPROJECTS=($(sed -n -e 's/^include //p' settings.gradle | tr -d ",\"" | tr ' ' "\n"))

printf "\nCollecting build artifacts...\n\n"
printf "Working directory:\n\n"
printf "  %s\n\n" `pwd`
printf "Output directory:\n\n"
printf "  %s\n\n" `pwd`/${OUTPUT_DIR}
printf "Projects to collect from:\n\n"
for PROJECT in ${ROOT_PROJECT} ${SUBPROJECTS[@]}; do printf "  %s\n" ${PROJECT}; done
printf "\nArtifacts to collect from each project:\n\n"
for ARTIFACT in ${ARTIFACTS[@]}; do printf "  %s\n" ${ARTIFACT}; done
printf "\nCollecting:\n\n"

mkdir -p ${OUTPUT_DIR}

for PROJECT in ${ROOT_PROJECT} ${SUBPROJECTS[@]}; do
  for ARTIFACT in ${ARTIFACTS[@]}; do
    [[ -z $(find . -path "*${PROJECT}/${ARTIFACT}" -print -quit) ]] && printf "  %s NOT FOUND - SKIPPING\n" ${PROJECT}/${ARTIFACT}
    rsync -am --out-format="%f" ${PROJECT}/${ARTIFACT} ${OUTPUT_DIR}/${PROJECT}/ 2>/dev/null | awk '{ if ($1 > 0) { print "  " $1 } }'
  done
done
