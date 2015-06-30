UTEID: mcl267;
FIRSTNAME: Mark;
LASTNAME: Laver;
CSACCOUNT: mcl267
EMAIL: crusherven@yahoo.com;

[Program 4]
[Description]
***************************
There are 2 java files: 
SecureSystem.java was improved slightly and made more modular, so that objects can be created and destroyed on the fly as per the requirements.  In the Subject class, String bits was added to assemble a String representation of the bits involved by concatenating a '1' or '0' as needed.  Then it is converted to a byte array, then back to a string, which is printed when run() goes.  run() has a helper function to do the actual file printing.

CovertChannel.java has a few methods.  One sends messages to do Hal's work, another sends the instructions for Lyle's work, and a third converts a String to bits.

[Finish]
I was not able to implement the verbose flag

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