#
# pull-db
# Inspect the database from your device
# Cedric Beust
#
 
PKG=me.zsr.feeder
DB=feed_db
 
adb shell "run-as $PKG chmod 755 /data/data/$PKG/databases"
adb shell "run-as $PKG chmod 666 /data/data/$PKG/databases/$DB"
adb shell "rm /sdcard/$DB"
adb shell "cp /data/data/$PKG/databases/$DB /sdcard/$DB"
 
rm -f /tmp/${DB}
adb pull /sdcard/${DB} /tmp/${DB}
 
sqlitebrowser /tmp/${DB}

