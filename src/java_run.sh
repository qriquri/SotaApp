classpath=".:/home/vstone/lib/*\
:/home/vstone/vstonemagic/*\
:/usr/local/share/OpenCV/java/*\
:/home/root/SotaApp/lib/*\
"

OPTION="-Dfile.encoding=UTF8 -Djava.library.path=/usr/local/share/OpenCV/java/:/home/root/"

echo "java -classpath $classpath $OPTION $1"
java -classpath "$classpath" $OPTION $1