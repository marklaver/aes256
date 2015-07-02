UTEID: mcl267; mw23845;
FIRSTNAME: Mark; Marguerite;
LASTNAME: Laver; West-Driga;
CSACCOUNT: mcl267; mwestdri;
EMAIL: crusherven@yahoo.com; mwestdri@gmail.com;

[Program 4]
[Description]
***************************
There is only one java file: 
AES.java implements AES encryption and decryption in the main method.
First, it reads in the input file and key file as detailed in the
specification. The contents are loaded into arrays of the appropriate
dimensions, and are then encrypted or decrypted based on the 'e' or 'd' flag passed in at execution, via the command line.
Our implementation of the decryption portion of this algorithm, is different from the structure recommended by the standard, and instead directly inverts the order of operations occurring in the encryption procedure.

[Finish]
This program is done.
We used the mix-columns code provided by Professor Young
We pulled a method to convert String to binary from StackOverflow:
http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java

[Timing Information]

We ran a AEStestvectors.py to generate a test vector of 60k linesls -, and ran it with the bash command "time" with our program to find the encryption/decryption time.  Below is the command used to encrypt/decrypt along with the results.

time java AES e key1 throughputtest 

encryption data:
time: 1.396s
size: 1.9MB
throughput: 1.361 MB/sec

decryption data:
time: 1.378s
size: 1.9MB
throughput: 1.379 MB/sec

[Test Cases]
[Input of test 1]
[command line]
javac *.java
java AES e key1 test1
java AES d key1 test1.enc
diff test1 test1.enc.dec

test1

[Output of test 1]
 
test1.enc
test1.enc.dec

[Input of test 2]
[command line]
javac *.java
java AES e key2 test2
java AES d key2 test2.enc

key2
test2

[Output of test 2]

test2.enc
test2.enc.dec

[Input of test 3]
[command line]
javac *.java
java AES e key3 test3
java AES d key3 test3.enc

key3
test3

[Output of test 3]

test3.enc
test3.enc.dec

[Input of test 4]
[command line]
javac *.java
java AES e key4 test4
java AES d key4 test4.enc

key4
test4

[Output of test 4]

test4.enc
test4.enc.dec
