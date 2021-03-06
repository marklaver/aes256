import java.util.*;
import java.io.*;

public class AES {

	static int[][] plain_text;
	static int[][] cipher_text;
	static int[][] st;

	AES(int size) {
		st = new int[4][4];
		plain_text = new int[size][16];
		cipher_text = new int[size][16];
	}

	// Nb = 4          # of columns in the state
	// Nk = 8          # of 32-bit words in Key
	// Nr = 14         # of rounds

	public static void main(String[] args) {
		char option = args[0].charAt(0);
		String keyFile = args[1];
		String inputFile = args[2];

		ArrayList<String> text = readFile(inputFile);

		ArrayList<String> keyarray = readFile(keyFile);
		int[][] key = reshapeKey(hexToBin(keyarray.get(0)));

		ArrayList<String> cleanText = cleanHex(text);
		AES state = new AES(cleanText.size());

		if (option == 'e') {
			for (int i = 0; i < plain_text.length; ++i) {
				plain_text[i] = hexToBin(cleanText.get(i));
			}
		}
		else if (option == 'd') {
			for (int i = 0; i < plain_text.length; ++i) {
				cipher_text[i] = hexToBin(cleanText.get(i));
			}			
		}

		int [][] expanded_key = key_expansion(key);
		
		if(option == 'e') {
			encrypt(expanded_key);
			writeFile(cipher_text, inputFile, 'e');
		}
		else if (option == 'd') {
			decrypt(expanded_key);
			writeFile(plain_text, inputFile, 'd');
		}
		else if (option == 't') {
			encrypt(expanded_key);
			writeFile(cipher_text, inputFile, 'e');
			decrypt(expanded_key);
			writeFile(plain_text, inputFile, 'd');
		}
		else
			System.out.println("Invalid flag.");

	}

	static void encrypt(int[][] expanded_key) {
		for (int row = 0; row < plain_text.length; ++row) {
			setState(row, 'e');
			addRoundKey(expanded_key, 0);

			for (int round = 1; round <= 13; ++round) {
				subBytes();
				shiftRows();
				for (int i = 0; i < 4; ++i)
					mixColumn2(i);
				addRoundKey(expanded_key, round);
				
			}

			subBytes();
			shiftRows();
			addRoundKey(expanded_key, 14);
			save_state(row, 'e');
		}
	}

	static void decrypt(int[][] expanded_key) {
		for (int row = 0; row < plain_text.length; ++row) {
			setState(row, 'd');
			addRoundKey(expanded_key,14);
			invShiftRows();
			invSubBytes();
			
			for (int round = 13; round >= 1; --round) {
				addRoundKey(expanded_key, round);
				for (int i = 0; i < 4; ++i)
					invMixColumn2(i);
				invShiftRows();
				invSubBytes();
			}

			addRoundKey(expanded_key, 0);
			save_state(row, 'd');
		}		
	}

	static int[][] key_expansion(int[][] key) {
		int nk = 8;
		int nb = 4;
		int nr = 14;

		int[][] expanded_key  = new int[4][nb*(nr+1)];
		//copy first 8 columns
		for (int r = 0; r < nb; r++) {
			for (int c = 0; c < nk; c++) {
				expanded_key[r][c] = key[r][c];
			}
		}
		int rcon_idx = 0;
		for (int c = nk; c < nb*(nr+1); ++c) {

			int[] temp = new int[4];
			for (int i = 0; i < 4; i++) {
				// Get the previous column
				temp[i] = expanded_key[i][c-1]; 
			}

			if (c % nk == 0) {  // each 8th column 
				temp = rot_word(temp);  //rcon[0][rcon_idx++];
				temp = sub_word(temp);
				temp[0] = temp[0] ^ rcon[rcon_idx++];
			}
			else if (c % nk == 4) { // each 4th column
				temp = sub_word(temp);
			}
			for (int i = 0; i < 4; i++) {
				// Fill the new column with the xor of temp - 1 and temp - 8
				expanded_key[i][c] = expanded_key[i][c-nk] ^ temp[i];
			}
		}
		return expanded_key;
	}
	
	final static int[] rcon = {0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40 };

	static void addRoundKey(int [][] expanded_key, int c) {
		c *= 4;
		for (int row = 0; row < 4; row++)
			for (int i = 0; i < 4; i++) {
				int col = i + c;
				st[row][i] = (st[row][i]) ^ (expanded_key[row][col]);
			}
	}
	
	static int[] sub_word(int[] w) {
		int[] result = new int[4];
		for (int i = 0; i < 4; ++i) {
				int n = w[i]; 
				result[i] = sbox[(n & 0xf0) >> 4][n & 0xf];
		}   
		return result;
	}

	static int[] rot_word(int[] w) {
		int x = w[0];
		w[0] = w[1];
		w[1] = w[2];
		w[2] = w[3];
		w[3] = x;
		return w;
	}

	static void save_state(int row, char flag) {
		int idx = 0;
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j) {
				if (flag == 'e')
					cipher_text[row][idx++] = st[j][i];
				if (flag == 'd')
					plain_text[row][idx++] = st[j][i];
			}
	}

	static void shiftRows() {
		int[] original = new int[4];
		for (int row = 1; row < 4; ++row) {
			for (int i = 0; i < 4; ++i) {
				original[i] = st[row][i];
			}
			for (int i = 0; i < 4; ++i) {
				st[row][i] = original[(i + row) % 4];
			}
		}
	}
	static void invShiftRows() {
		int[] original = new int[4];
		for (int row = 1; row < 4; ++row) {
			for (int i = 0; i < 4; ++i) {
				original[i] = st[row][i];
			}
			for (int i = 0; i < 4; ++i) {
				st[row][(i + row) % 4] = original[i];
			}
		}
	}

	static void invSubBytes() {
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j) {
				int n = st[i][j]; 
				st[i][j] = inv_sbox[(n & 0xf0) >> 4][n & 0xf];
			}
	}

	static void subBytes() {
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j) {
				int n = st[i][j]; 
				st[i][j] = sbox[(n & 0xf0) >> 4][n & 0xf];
			}
	}

	// Pass in the row of the plain_text[][] array we are working on
	static void setState(int row, char flag) {
		int idx = 0;
		for (int i = 0; i < 4; ++i)
			for (int j = 0; j < 4; ++j) {
				if (flag == 'd')
					st[j][i] = cipher_text[row][idx++];
				else  
					st[j][i] = plain_text[row][idx++];
			}
	}

	static int[][] reshapeKey(int[] keyin) {
		int[][] keyout = new int[4][8];
		int idx = 0;
		for (int i = 0; i < 8; ++i) {
			for (int j = 0; j < 4; ++j) {
				keyout[j][i] = keyin[idx++];
			}
		}

		return keyout;
	}

// Pulled this method from Stack Overflow:
// http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
	static int[] hexToBin(String s) {
		int len = s.length();
		int[] result = new int[len / 2];
		for (int i = 0; i < len; i += 2) {
			result[i / 2] = (int)((Character.digit(s.charAt(i), 16) << 4)
														+ Character.digit(s.charAt(i+1), 16));
		}

		return result;
	}

	static ArrayList<String> cleanHex(ArrayList<String> text) {
		ArrayList<String> result = new ArrayList<String>();

		for (String s : text) {
			String t = "";

			if (s.matches("-?[0-9a-fA-F]+")) {
				if (s.length() == 32)
					result.add(s.toUpperCase());
				else if (s.length() < 32)
					result.add((s + "00000000000000000000000000000000".substring(s.length())).toUpperCase() );
				else if (s.length() > 32)
					result.add((s.substring(0, 32)).toUpperCase());
			}
		}
		return result;
	}

	final static int[][] sbox = {
		{0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76 },
		{0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0 },
		{0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15 },
		{0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75 },
		{0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84 },
		{0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf },
		{0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8 },
		{0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2 },
		{0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73 },
		{0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb },
		{0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79 },
		{0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08 },
		{0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a },
		{0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e },
		{0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf },
		{0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16 }};

	final static int[][] inv_sbox = {
		{0x52, 0x09, 0x6A, 0xD5, 0x30, 0x36, 0xA5, 0x38, 0xBF, 0x40, 0xA3, 0x9E, 0x81, 0xF3, 0xD7, 0xFB},
		{0x7C, 0xE3, 0x39, 0x82, 0x9B, 0x2F, 0xFF, 0x87, 0x34, 0x8E, 0x43, 0x44, 0xC4, 0xDE, 0xE9, 0xCB},
		{0x54, 0x7B, 0x94, 0x32, 0xA6, 0xC2, 0x23, 0x3D, 0xEE, 0x4C, 0x95, 0x0B, 0x42, 0xFA, 0xC3, 0x4E},
		{0x08, 0x2E, 0xA1, 0x66, 0x28, 0xD9, 0x24, 0xB2, 0x76, 0x5B, 0xA2, 0x49, 0x6D, 0x8B, 0xD1, 0x25},
		{0x72, 0xF8, 0xF6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xD4, 0xA4, 0x5C, 0xCC, 0x5D, 0x65, 0xB6, 0x92},
		{0x6C, 0x70, 0x48, 0x50, 0xFD, 0xED, 0xB9, 0xDA, 0x5E, 0x15, 0x46, 0x57, 0xA7, 0x8D, 0x9D, 0x84},
		{0x90, 0xD8, 0xAB, 0x00, 0x8C, 0xBC, 0xD3, 0x0A, 0xF7, 0xE4, 0x58, 0x05, 0xB8, 0xB3, 0x45, 0x06},
		{0xD0, 0x2C, 0x1E, 0x8F, 0xCA, 0x3F, 0x0F, 0x02, 0xC1, 0xAF, 0xBD, 0x03, 0x01, 0x13, 0x8A, 0x6B},
		{0x3A, 0x91, 0x11, 0x41, 0x4F, 0x67, 0xDC, 0xEA, 0x97, 0xF2, 0xCF, 0xCE, 0xF0, 0xB4, 0xE6, 0x73},
		{0x96, 0xAC, 0x74, 0x22, 0xE7, 0xAD, 0x35, 0x85, 0xE2, 0xF9, 0x37, 0xE8, 0x1C, 0x75, 0xDF, 0x6E},
		{0x47, 0xF1, 0x1A, 0x71, 0x1D, 0x29, 0xC5, 0x89, 0x6F, 0xB7, 0x62, 0x0E, 0xAA, 0x18, 0xBE, 0x1B},
		{0xFC, 0x56, 0x3E, 0x4B, 0xC6, 0xD2, 0x79, 0x20, 0x9A, 0xDB, 0xC0, 0xFE, 0x78, 0xCD, 0x5A, 0xF4},
		{0x1F, 0xDD, 0xA8, 0x33, 0x88, 0x07, 0xC7, 0x31, 0xB1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xEC, 0x5F},
		{0x60, 0x51, 0x7F, 0xA9, 0x19, 0xB5, 0x4A, 0x0D, 0x2D, 0xE5, 0x7A, 0x9F, 0x93, 0xC9, 0x9C, 0xEF},
		{0xA0, 0xE0, 0x3B, 0x4D, 0xAE, 0x2A, 0xF5, 0xB0, 0xC8, 0xEB, 0xBB, 0x3C, 0x83, 0x53, 0x99, 0x61},
		{0x17, 0x2B, 0x04, 0x7E, 0xBA, 0x77, 0xD6, 0x26, 0xE1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0C, 0x7D}};


		////////////////////////  the mixColumns Tranformation ////////////////////////

	final static int[] LogTable = {
	0,   0,  25,   1,  50,   2,  26, 198,  75, 199,  27, 104,  51, 238, 223,   3, 
	100,   4, 224,  14,  52, 141, 129, 239,  76, 113,   8, 200, 248, 105,  28, 193, 
	125, 194,  29, 181, 249, 185,  39, 106,  77, 228, 166, 114, 154, 201,   9, 120, 
	101,  47, 138,   5,  33,  15, 225,  36,  18, 240, 130,  69,  53, 147, 218, 142, 
	150, 143, 219, 189,  54, 208, 206, 148,  19,  92, 210, 241,  64,  70, 131,  56, 
	102, 221, 253,  48, 191,   6, 139,  98, 179,  37, 226, 152,  34, 136, 145,  16, 
	126, 110,  72, 195, 163, 182,  30,  66,  58, 107,  40,  84, 250, 133,  61, 186, 
	43, 121,  10,  21, 155, 159,  94, 202,  78, 212, 172, 229, 243, 115, 167,  87, 
	175,  88, 168,  80, 244, 234, 214, 116,  79, 174, 233, 213, 231, 230, 173, 232, 
	44, 215, 117, 122, 235,  22,  11, 245,  89, 203,  95, 176, 156, 169,  81, 160, 
	127,  12, 246, 111,  23, 196,  73, 236, 216,  67,  31,  45, 164, 118, 123, 183, 
	204, 187,  62,  90, 251,  96, 177, 134,  59,  82, 161, 108, 170,  85,  41, 157, 
	151, 178, 135, 144,  97, 190, 220, 252, 188, 149, 207, 205,  55,  63,  91, 209, 
	83,  57, 132,  60,  65, 162, 109,  71,  20,  42, 158,  93,  86, 242, 211, 171, 
	68,  17, 146, 217,  35,  32,  46, 137, 180, 124, 184,  38, 119, 153, 227, 165, 
	103,  74, 237, 222, 197,  49, 254,  24,  13,  99, 140, 128, 192, 247, 112,   7};

	final static int[] AlogTable = {
	1,   3,   5,  15,  17,  51,  85, 255,  26,  46, 114, 150, 161, 248,  19,  53, 
	95, 225,  56,  72, 216, 115, 149, 164, 247,   2,   6,  10,  30,  34, 102, 170, 
	229,  52,  92, 228,  55,  89, 235,  38, 106, 190, 217, 112, 144, 171, 230,  49, 
	83, 245,   4,  12,  20,  60,  68, 204,  79, 209, 104, 184, 211, 110, 178, 205, 
	76, 212, 103, 169, 224,  59,  77, 215,  98, 166, 241,   8,  24,  40, 120, 136, 
	131, 158, 185, 208, 107, 189, 220, 127, 129, 152, 179, 206,  73, 219, 118, 154, 
	181, 196,  87, 249,  16,  48,  80, 240,  11,  29,  39, 105, 187, 214,  97, 163, 
	254,  25,  43, 125, 135, 146, 173, 236,  47, 113, 147, 174, 233,  32,  96, 160, 
	251,  22,  58,  78, 210, 109, 183, 194,  93, 231,  50,  86, 250,  21,  63,  65, 
	195,  94, 226,  61,  71, 201,  64, 192,  91, 237,  44, 116, 156, 191, 218, 117, 
	159, 186, 213, 100, 172, 239,  42, 126, 130, 157, 188, 223, 122, 142, 137, 128, 
	155, 182, 193,  88, 232,  35, 101, 175, 234,  37, 111, 177, 200,  67, 197,  84, 
	252,  31,  33,  99, 165, 244,   7,   9,  27,  45, 119, 153, 176, 203,  70, 202, 
	69, 207,  74, 222, 121, 139, 134, 145, 168, 227,  62,  66, 198,  81, 243,  14, 
	18,  54,  90, 238,  41, 123, 141, 140, 143, 138, 133, 148, 167, 242,  13,  23, 
	57,  75, 221, 124, 132, 151, 162, 253,  28,  36, 108, 180, 199,  82, 246,   1};

	static int mul (int a, int b) {
	//private byte mul (int a, byte b) {
		int inda = (a < 0) ? (a + 256) : a;
		int indb = (b < 0) ? (b + 256) : b;

		if ( (a != 0) && (b != 0) ) {
				int index = (LogTable[inda] + LogTable[indb]);
				// byte val = (byte)(AlogTable[ index % 255 ] );
				int val = (int)(AlogTable[ index % 255 ] );
				return val;
		}
		else 
				return 0;
	} // mul

// In the following two methods, the input c is the column number in
// your evolving state matrix st (which originally contained 
// the plaintext input but is being modified).  Notice that the state here is defined as an
// array of bytes.  If your state is an array of integers, you'll have
// to make adjustments. 

	static void mixColumn2 (int c) {
		// This is another alternate version of mixColumn, using the 
		// logtables to do the computation.

		// *********changed from byte to int************
		int a[] = new int[4];

		// note that a is just a copy of st[.][c]
		for (int i = 0; i < 4; i++) 
				a[i] = st[i][c];
		
		// This is exactly the same as mixColumns1, if 
		// the mul columns somehow match the b columns there.
		st[0][c] = (int)(mul(2,a[0]) ^ a[2] ^ a[3] ^ mul(3,a[1]));
		st[1][c] = (int)(mul(2,a[1]) ^ a[3] ^ a[0] ^ mul(3,a[2]));
		st[2][c] = (int)(mul(2,a[2]) ^ a[0] ^ a[1] ^ mul(3,a[3]));
		st[3][c] = (int)(mul(2,a[3]) ^ a[1] ^ a[2] ^ mul(3,a[0]));
	} // mixColumn2

	static void invMixColumn2 (int c) {
		// *********changed from byte to int************
		// byte a[] = new byte[4];
		int a[] = new int[4];

		
		// note that a is just a copy of st[.][c]
		for (int i = 0; i < 4; i++) 
				a[i] = st[i][c];

		st[0][c] = (int)(mul(0xE,a[0]) ^ mul(0xB,a[1]) ^ mul(0xD, a[2]) ^ mul(0x9,a[3]));
		st[1][c] = (int)(mul(0xE,a[1]) ^ mul(0xB,a[2]) ^ mul(0xD, a[3]) ^ mul(0x9,a[0]));
		st[2][c] = (int)(mul(0xE,a[2]) ^ mul(0xB,a[3]) ^ mul(0xD, a[0]) ^ mul(0x9,a[1]));
		st[3][c] = (int)(mul(0xE,a[3]) ^ mul(0xB,a[0]) ^ mul(0xD, a[1]) ^ mul(0x9,a[2]));
	} // invMixColumn2  

	static ArrayList<String> readFile(String fileName) {
		ArrayList<String> result = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader (fileName));
			String line = br.readLine();

			while (!line.isEmpty()) {
				result.add(line);
				line = br.readLine();
			}
		}
		catch(IOException e) {
			System.out.println(e);
		}

		return result;
	}

	static void writeFile(int[][] text, String fileName, char flag) {
		String[] message = new String[text.length];
		for (int i = 0; i < text.length; ++i) {
			String s = "";
			for (int j = 0; j < text[0].length; ++j) {
				s += String.format("%02x",text[i][j]);
			}
			message[i] = s;
		}

		try {
			File file;
			if (flag == 'e')
				file = new File(fileName + ".enc");
			else
				file = new File(fileName + ".dec");
			if (!file.exists())
				file.createNewFile();
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for (int i = 0; i < message.length; ++i)
				bw.write(message[i] + "\n");
			bw.write("\n");
			bw.close();
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}  
}
