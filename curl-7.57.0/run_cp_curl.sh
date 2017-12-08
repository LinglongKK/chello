#! /system/bin/sh
su
echo "copy curllib!"
cp /sdcard/curl/libcurl.so /system/lib
cp /sdcard/curl/curl /system/bin

chmod a+x /system/bin/curl

