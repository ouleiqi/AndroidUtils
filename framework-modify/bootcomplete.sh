#!/system/bin/sh

#tinymix -D 1 48 1
#tinymix -D 1 51 1
#tinymix -D 1 9 255
#tinymix -D 1 12 127
#tinymix -D 1 13 1
#tinymix -D 1 14 5
#tinymix -D 1 15 5


#tinymix -D 1 48 1
#tinymix -D 1 51 1
#tinymix -D 1 9 255
#tinymix -D 1 12 127
#tinymix -D 1 13 1
#tinymix -D 1 14 5
#tinymix -D 1 15 5


#gpio2 2 3 4 5 四个口
array=(66 67 68 69)
cd /sys/class/gpio/
for data in ${array[@]}
do 
    if [[ ! -d "gpio"${data} ]]; then
       echo ${data} > export
       echo out > "gpio${data}"/direction
    fi
done



