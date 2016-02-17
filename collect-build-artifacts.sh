#!/usr/bin/env bash

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
