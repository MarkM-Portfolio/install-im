#!/bin/sh
for i in * ; do
    if [ -f ../../tmp/$i.zip ] ; then
        cd $i
        echo unzip $i.zip in $i
        unzip ../../../tmp/$i.zip
        cd ..
    fi
done

git diff -p -R | grep -E "^(diff|(old|new) mode)" | git apply
