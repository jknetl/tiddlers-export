#!/bin/bash

if [ $# -ne 1 ]; then
    echo "usage: convert_dir [dir]"
    exit 1
fi

dir=$1

old_wd=$(pwd)
cd $dir

PYTHON_SCRIPT="$old_wd/tiddly2md.py"

num_of_files=$(ls *.wt | wc -l)
i=1
for f in $(ls *.wt); do
    python $PYTHON_SCRIPT $f "$(basename $f .wt).md"
    echo "($i/$num_of_files) Converting $f"
    i=$(($i+1))
done


cd $old_wd

