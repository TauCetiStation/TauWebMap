#!/bin/sh

echo "Compressing images, this may take several minutes"

count=0
for f in $(find ./data/maps -maxdepth 4 -type f); do count=$((count+1)); done
echo "  - Total: $count"

for f in $(find ./data/maps -maxdepth 4 -type f); do
    pngquant --ext=.png --force --strip --speed=1 --nofs --posterize=2 "$f"
    printf "\r  - Remain: %s" "$((count--))"
done

printf "\rAll images compressed!\n"
