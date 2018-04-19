import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XTSAES {
	public static void main(String[] args) throws Exception {
		String plain = "C:\\Users\\Ramadhan\\Documents\\TestCIS\\plain123.txt";
		String key = "C:\\Users\\Ramadhan\\Documents\\TestCIS\\key.txt";
		String cipher = "C:\\Users\\Ramadhan\\Documents\\TestCIS\\cipher.txt";
		//XTSAES xts = new XTSAES(plain, key, cipher);
		//xts.encrypt();
		XTSAES xts = new XTSAES(cipher, key, plain);
		xts.decrypt();
	}
    private String FILE_PLAIN;
    private String FILE_CIPHER;
    private String FILE_KEY;
    private static int BLOCK_SIZE = 16;                     //128-bits (16-bytes)
    private static int KEY_LENGTH_HEX = 64;                 //256-bits (32-bytes)
    private static byte[] nonce = Util.hex2byte("12345678901234567890123456789012");
    private static int NUMBER_OF_THREAD = 100;
    private byte[] nonceDP = null;
    private byte[][] multiplyDP = null;

    public XTSAES(String plain, String key, String cipher) {
    	this.FILE_PLAIN = plain;
    	this.FILE_KEY = key;
    	this.FILE_CIPHER = cipher;
    }
    
    public void encrypt() throws Exception {
    	 Path path = Paths.get(this.FILE_PLAIN);
 	     byte[] byteIn = Files.readAllBytes(path);
 	     System.out.println(byteIn.length);
 	     BufferedReader br = new BufferedReader (new FileReader(this.FILE_KEY));
 	     String key = br.readLine();
   	     String key1 = key.substring(0, key.length()/2);
   	 	 String key2 = key.substring(key.length()/2, key.length()); 	
   	 	 br.close();
   	 	 
 	     /**
 	      * Pisah menjadi block
 	      */
 	     int numBlock = (int )Math.ceil((byteIn.length) / (double) AES.BLOCK_SIZE);
 	     byte[][] ListBlockData = new byte[numBlock][AES.BLOCK_SIZE];
 	     //System.out.println(numBlock);
 	     for(int ii = 0, jj = 0 ; ii < byteIn.length; ii++) {
 	    	//System.out.print(byteIn[ii]);
 	    	//System.out.print(jj);
 	     	int num = ii % AES.BLOCK_SIZE;
 	    	ListBlockData[jj][num] 
 	    			= byteIn[ii];
 	    	if(ii != 0 && num == 0) {
 	    		jj++;
 	    	}
 	     };
 	     System.out.println(numBlock);
 	     byte[][] bufferOut = new byte[(int) (byteIn.length / AES.BLOCK_SIZE + 1)][AES.BLOCK_SIZE];
 	     bufferOut[byteIn.length/AES.BLOCK_SIZE] = new byte[byteIn.length % AES.BLOCK_SIZE];
 	     byte[] key1Byte = Util.hex2byte(key1);
 	     byte[] key2Byte = Util.hex2byte(key2);
 	     
 	     for (int ii = 0; ii < ListBlockData.length - 2; ii++) {
 	    	 System.out.println("Processing Block " + ii + " ...");
 	    	 bufferOut[ii] = blockEncrypt(ListBlockData[ii], key1Byte, key2Byte, nonce, ii);
 	     }
 	     
 	     
 	     /**
 	      * Handle check block terakhir
 	      * kalo terakhir full berarti jalanin lagi 2 kali encryptnya
 	      * kalo nggak, ciphertext stealing jalaninnya.
 	      */
 	     
	     int indexBeforeLast = ListBlockData.length-2;
	     int indexLast = ListBlockData.length-1;
 	     if(byteIn.length % AES.BLOCK_SIZE == 0) {
 	    	 System.out.println("Processing Block " + indexBeforeLast + " ...");
 	    	 bufferOut[indexBeforeLast] = blockEncrypt(ListBlockData[indexBeforeLast], key1Byte, key2Byte, nonce, indexBeforeLast);
 	    	 System.out.println("Processing Block " + indexLast + " ...");
 	    	 bufferOut[indexLast] = blockEncrypt(ListBlockData[indexLast], key1Byte, key2Byte, nonce, indexLast);
 	    	 
 	     }else {
 	    	 /**
 	    	  * Ciphertext stealing
 	    	  */
 	    	 int jumlahSisa = byteIn.length % AES.BLOCK_SIZE;
 	    	 System.out.println("Processing Block " + indexBeforeLast + " ...");
 	    	 byte[] xx = blockEncrypt(ListBlockData[indexBeforeLast], key1Byte, key2Byte, nonce, indexBeforeLast);
 	    	 System.arraycopy(xx, 0, bufferOut[indexLast], 0, jumlahSisa);
 	    	 byte[] yy = new byte[AES.BLOCK_SIZE];
 	    	 System.out.println("Processing Block " + indexLast + " ...");
 	    	 System.arraycopy(ListBlockData[indexLast], 0, yy, 0, jumlahSisa);
 	    	 System.arraycopy(xx, jumlahSisa, yy, jumlahSisa, AES.BLOCK_SIZE - jumlahSisa);
 	    	 bufferOut[indexBeforeLast] = blockEncrypt(yy, key1Byte, key2Byte, nonce, indexLast);
 	    	 
 	     }
 	     
 	     /**
 	      * Output
 	      */
 	     //path = Paths.get(FILE_CIPHER);
 	     System.out.println(bufferOut[2].length);
 	     FileOutputStream stream = new FileOutputStream(this.FILE_CIPHER);
 	     try {
 	    	 for (int ii = 0; ii < bufferOut.length; ii++) {
 	    		stream.write(bufferOut[ii]);
 	    	 }
 	    	 
 	     } finally {
 	    	 stream.close();
 	     }
 	     System.out.println("----------Encryption Done--------");
    }
    
    public void decrypt() throws Exception {
   	 Path path = Paths.get(this.FILE_PLAIN);
	     byte[] byteIn = Files.readAllBytes(path);
	     System.out.println(byteIn.length);
	     BufferedReader br = new BufferedReader (new FileReader(this.FILE_KEY));
	     String key = br.readLine();
  	     String key1 = key.substring(0, key.length()/2);
  	 	 String key2 = key.substring(key.length()/2, key.length()); 	
  	 	 
	     /**
	      * Pisah menjadi block
	      */
	     
	     int numBlock = (int )Math.ceil((byteIn.length) / (double) AES.BLOCK_SIZE);
	     byte[][] ListBlockData = new byte[numBlock][AES.BLOCK_SIZE];
	     for(int ii = 0, jj = 0 ; ii < byteIn.length; ii++) {
	     	int num = ii % AES.BLOCK_SIZE;
	    	ListBlockData[jj][num] = byteIn[ii];
	    	if(ii != 0 && num == 0) {
	    		jj++;
	    	}
	     };
 	     byte[][] bufferOut = new byte[(int) (byteIn.length / AES.BLOCK_SIZE + 1)][AES.BLOCK_SIZE];
 	     bufferOut[byteIn.length/AES.BLOCK_SIZE] = new byte[byteIn.length % AES.BLOCK_SIZE];
	     byte[] key1Byte = Util.hex2byte(key1);
	     byte[] key2Byte = Util.hex2byte(key2);
	     
	     for (int ii = 0; ii < ListBlockData.length - 2; ii++) {
	    	 System.out.println("Processing Block " + ii + " ...");
	    	 bufferOut[ii] = blockDecrypt(ListBlockData[ii], key1Byte, key2Byte, nonce, ii);
	     }
	     
	     /**
	      * Handle check block terakhir
	      * kalo terakhir full berarti jalanin lagi 2 kali encryptnya
	      * kalo nggak, ciphertext stealing jalaninnya.
	      */
	     System.out.println("--------Done Processing Blocks--------");
	     int indexBeforeLast = ListBlockData.length-2;
	     int indexLast = ListBlockData.length-1;
 	     if(byteIn.length % AES.BLOCK_SIZE == 0) {
 	    	 bufferOut[indexBeforeLast] = blockDecrypt(ListBlockData[indexBeforeLast], key1Byte, key2Byte, nonce, indexBeforeLast);
 	    	 bufferOut[indexLast] = blockDecrypt(ListBlockData[indexLast], key1Byte, key2Byte, nonce, indexLast);
 	    	 
 	     }else {
 	    	 /**
 	    	  * Ciphertext stealing
 	    	  */
 	    	 int jumlahSisa = byteIn.length % AES.BLOCK_SIZE;
 	    	 byte[] xx = blockDecrypt(ListBlockData[indexBeforeLast], key1Byte, key2Byte, nonce, indexBeforeLast);
 	    	 System.arraycopy(xx, 0, bufferOut[indexLast], 0, jumlahSisa);
 	    	 byte[] yy = new byte[AES.BLOCK_SIZE];
 	    	 System.arraycopy(ListBlockData[indexLast], 0, yy, 0, jumlahSisa);
 	    	 System.arraycopy(xx, jumlahSisa, yy, jumlahSisa, AES.BLOCK_SIZE - jumlahSisa);
 	    	 bufferOut[indexBeforeLast] = blockDecrypt(yy, key1Byte, key2Byte, nonce, indexLast);
 	    	 
 	     }
 	     
 	     FileOutputStream stream = new FileOutputStream(this.FILE_CIPHER);
 	     try {
 	    	 for (int ii = 0; ii < bufferOut.length; ii++) {
 	    		stream.write(bufferOut[ii]);
 	    	 }
 	    	 
 	     } finally {
 	    	 stream.close();
 	     }
   }
    
    
    private byte[] blockEncrypt(byte[] plainBlock, byte[] key1, byte[] key2, byte[] tweak, int j) {
    	AES aes = new AES();
    	aes.setKey(key2);
    	byte[] enc1 = aes.encrypt(tweak);
    	byte[] t = multiplyByAlphaJ(enc1, j);
    	byte[] pp = new byte[AES.BLOCK_SIZE];
    	
    	for (int ii = 0; ii < pp.length; ii++) {
    		pp[ii] = (byte) (plainBlock[ii] ^ t[ii]);
    	}
    	aes.setKey(key1);
    	byte[] cc = aes.encrypt(pp);
    	byte[] cipherBlock = new byte[AES.BLOCK_SIZE];
    	for (int ii = 0; ii < cipherBlock.length; ii++) {
    		cipherBlock[ii] = (byte) (cc[ii] ^ t[ii]);
    	}
    	
    	return cipherBlock;
    }
    
    private byte[] blockDecrypt(byte[] plainBlock, byte[] key1, byte[] key2, byte[] tweak, int j) {
    	AES aes = new AES();
    	aes.setKey(key2);
    	byte[] enc1 = aes.encrypt(tweak);
    	byte[] t = multiplyByAlphaJ(enc1, j);
    	byte[] cc = new byte[AES.BLOCK_SIZE];
    	
    	for (int ii = 0; ii < cc.length; ii++) {
    		cc[ii] = (byte) (plainBlock[ii] ^ t[ii]);
    	}
    	aes.setKey(key1);
    	byte[] pp = aes.decrypt(cc);
    	byte[] cipherBlock = new byte[AES.BLOCK_SIZE];
    	for (int ii = 0; ii < cipherBlock.length; ii++) {
    		cipherBlock[ii] = (byte) (pp[ii] ^ t[ii]);
    	}
    	
    	return cipherBlock;
    }
    
    private byte[] multiplyByAlphaJ(byte[] enc, int j) {
    	byte[] ret = new byte[enc.length];
    	ret[0] = (byte) ((2 * (enc[0] % 128)) ^ (135 * (enc[15] / 128)));
    	for (int ii = 0; ii < j; ii++) {
    		for(int kk = 1; kk < 16; kk++) {
    			ret[kk] = (byte) ((2 * (enc[kk] % 128 )) ^ (enc[kk-1] / 128));
    		}
    	}
    	return ret;
    }
}