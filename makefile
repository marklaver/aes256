
1test:
	javac *.java
	java AES e key1 test1
	java AES d key1 test1.enc
	diff test1 test1.enc.dec
2test:
	javac *.java
	java AES e key2 test2
	java AES d key2 test2.enc
	diff test2 test2.enc.dec
3test:
	javac *.java
	java AES e key3 test3
	java AES d key3 test3.enc
	diff test3 test3.enc.dec
4test:
	javac *.java
	java AES e key4 test4
	java AES d key4 test4.enc
	diff test4 test4.enc.dec
clean:
	rm -f *.class
	rm -f *.enc
	rm -f *.dec
