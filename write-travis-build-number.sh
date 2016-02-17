#!/usr/bin/env bash

mkdir -p build
mkdir -p build/latest-build-number

echo $TRAVIS_BUILD_NUMBER > build/latest-build-number/latest-build-number.txt
