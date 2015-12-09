#!/bin/bash

for i in {0..1000}
do
    cp test_src.vertex test_$i.vertex
    cp test_src.fragment test_$i.fragment
done
