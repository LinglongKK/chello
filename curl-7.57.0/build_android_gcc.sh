#! /bin/bash

TOOLCHAIN_DIR="/home/chenlong/toolchains_r16"
CURL_SOURCE_PATH="/home/chenlong"
#arm-linux-androideabi
#export CC="$TOOLCHAIN_DIR/bin/arm-linux-androideabi-gcc"
export CC="$TOOLCHAIN_DIR/bin/arm-linux-androideabi-clang"
export CFLAGS="-std=c99 -fPIE -fPIC"
export LDFLAGS="-pie"
#export NM="$ANDROID_NM"
#export RANLIB="$ANDROID_RANLIB"
CURL_ARGS="--without-ssl --with-zlib --disable-ftp --disable-gopher 
	--disable-file --disable-imap --disable-ldap --disable-ldaps 
	--disable-pop3 --disable-proxy --disable-rtsp --disable-smtp 
	--disable-telnet --disable-tftp --without-gnutls --without-libidn 
	--without-librtmp --disable-dict"
./configure --host=arm-linux-androideabi --prefix=$CURL_SOURCE_PATH/dest $CURL_ARGS

make clean
make -j4
make install

adb push $CURL_SOURCE_PATH/dest/bin/curl /sdcard/curl
adb push $CURL_SOURCE_PATH/dest/lib/libcurl.so /sdcard/curl
adb shell < run_cp_curl.sh