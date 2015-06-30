etest:
	javac *.java
	java AESa e keyfile test1

dtest:
	javac *.java
	java AESa d keyfile test1

clean:
	rm *.class, *.enc, *.dec
