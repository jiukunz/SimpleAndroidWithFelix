#!/usr/bin/env bash
for f in $*; do
    tmpfile="classes.dex"
    dx --dex --output=${tmpfile} ${f}
    aapt add ${f} ${tmpfile}
    rm -f ${tmpfile}
done