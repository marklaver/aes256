etest:
	javac *.java
	java AESa e keyfile test1
	cat test1.enc

ttest:
	javac *.java
	java AESa t keyfile test1

dtest:
	javac *.java
	cp test1.enc dtest1.in
	java AESa d keyfile dtest1.in
	cat dtest1.in.dec
	diff dtest1.in.dec test1

clean:
	rm -f *.class
	rm -f *.enc
	rm -f *.dec
