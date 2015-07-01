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

[Test Cases]
[Input of test 1]
[command line]
java AES e key plaintext
java AES d key plaintext.enc

plaintext

[Output of test 1]
plaintext.enc
plaintext.enc.dec
   
[Input of test 2]
[command line]
You need to write down command line.

You need to write down filename.

[Output of test 2]

You need to write down filename.

[Input of test 3]
[command line]

[Output of test 3]

[Input of test 4]
[command line]

[Output of test 4]