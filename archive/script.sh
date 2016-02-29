read line
FILE=$(echo $line | sed -e "s/ HTTP.1.1.*//" -e "s/GET..//" -e "s/#.*//" -e "s/%20/ /g")
EXT=$(echo $FILE | sed "s/.*\.//")
echo -ne "HTTP/1.0 200 OK\r\nContent-Type: text/${EXT}\r\nContent-Length: $(wc -c <${FILE})\r\n\r\n"
cat "${FILE}"
