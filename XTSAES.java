import java.io.BufferedReader;
import java.io.FileReader;
import java.io.RandomAccessFile;

public class XTSAES {
	public static void main(String[] args) throws Exception {
		/**
		 * Yang gw command pake //
		 * optional kayak lagi proses block berapa
		 */
		String plain = "C:\\Users\\Ramadhan\\Documents\\TestCIS\\punisherDecipher.png";
		String key = "C:\\Users\\Ramadhan\\Documents\\TestCIS\\key.txt";
		String cipher = "C:\\Users\\Ramadhan\\Documents\\TestCIS\\punishercipher.png";
		XTSAES xts = new XTSAES(plain, key, cipher);
		xts.encrypt();
		//XTSAES xts = new XTSAES(cipher, key, plain);
		//xts.decrypt();
	}
    private String FILE_PLAIN;
    private String FILE_CIPHER;
    private String FILE_KEY;
    private static byte[] nonce = Util.hex2byte("12345678901234567890123456789012");

    public XTSAES(String plain, String key, String cipher) {
    	this.FILE_PLAIN = plain;
    	this.FILE_KEY = key;
    	this.FILE_CIPHER = cipher;
    }
    
    public void encrypt() throws Exception {
    	RandomAccessFile inputBytes = new RandomAccessFile(this.FILE_PLAIN, "r");
   	 	byte[][] ListBlockData = new byte[(int) inputBytes.length()/16 + 1][16];
   	 	ListBlockData[(int) inputBytes.length()/16] = new byte[(int) inputBytes.length() % 16];
        for (int a = 0; a < ListBlockData.length; a++) {
            inputBytes.read(ListBlockData[a]);
        }
	     BufferedReader br = new BufferedReader (new FileReader(this.FILE_KEY));
	     String key = br.readLine();
  	     String key1 = key.substring(0, key.length()/2);
  	 	 String key2 = key.substring(key.length()/2, key.length()); 	
  	 	 br.close();
  	 	 
	     /**
	      * Pisah menjadi block
	      */
  	 	 int jmlBlockPenuh = (int) (inputBytes.length() / AES.BLOCK_SIZE);
	     byte[][] bufferOut = new byte[(int) (jmlBlockPenuh + 1)][AES.BLOCK_SIZE];
	     bufferOut[jmlBlockPenuh] = new byte[(int) (inputBytes.length() % AES.BLOCK_SIZE)];
	     byte[] key1Byte = Util.hex2byte(key1);
	     byte[] key2Byte = Util.hex2byte(key2);
	     
	     
	     
	     for (int ii = 0; ii < jmlBlockPenuh - 1; ii++) {
//	    	 System.out.println("Processing Block " + ii + " ...");
	    	 bufferOut[ii] = blockEncrypt(ListBlockData[ii], key1Byte, key2Byte, nonce, ii);
	     }
	     
	     
	     /**
	      * Handle check block terakhir
	      * kalo terakhir full berarti jalanin lagi 2 kali encryptnya
	      * kalo nggak, ciphertext stealing jalaninnya.
	      */
	     
	     if(inputBytes.length() % AES.BLOCK_SIZE == 0) {
//	    	 System.out.println("Processing Block " + (jmlBlockPenuh - 1) + " ...");
	    	 bufferOut[jmlBlockPenuh - 1] = blockEncrypt(ListBlockData[jmlBlockPenuh - 1], key1Byte, key2Byte, nonce, jmlBlockPenuh - 1);
	    	 
	     }else {
	    	 /**
	    	  * Ciphertext stealing
	    	  */
	    	 int jumlahSisa = (int) inputBytes.length() % AES.BLOCK_SIZE;
//	    	 System.out.println("Processing Block " + (jmlBlockPenuh) + " ...");
	    	 byte[] xx = blockEncrypt(ListBlockData[jmlBlockPenuh - 1], key1Byte, key2Byte, nonce, jmlBlockPenuh - 1);
	    	 System.arraycopy(xx, 0, bufferOut[jmlBlockPenuh], 0, jumlahSisa);
	    	 byte[] cp = new byte[AES.BLOCK_SIZE - jumlahSisa];
	    	 byte[] yy = new byte[AES.BLOCK_SIZE];
	    	 for (int ii = jumlahSisa; ii < AES.BLOCK_SIZE; ii++) {
	    		 cp[ii - jumlahSisa] = xx[ii];
	    	 }
	    	 System.arraycopy(ListBlockData[jmlBlockPenuh], 0, yy, 0, ListBlockData[jmlBlockPenuh].length);
	    	 System.arraycopy(cp, 0, yy, ListBlockData[jmlBlockPenuh].length, cp.length);
	    	 System.out.println("Processing Block " + (jmlBlockPenuh - 1) + " ...");
	    	 bufferOut[jmlBlockPenuh - 1] = blockEncrypt(yy, key1Byte, key2Byte, nonce, jmlBlockPenuh);
	     }
	     
	     
	     /**
	      * Output
	      */
        RandomAccessFile out = new RandomAccessFile(this.FILE_CIPHER, "rw");
	     for (int a = 0; a < bufferOut.length; a++) {
            out.write(bufferOut[a]);
        }
	     inputBytes.close();
	     out.close();
	     System.out.println("----------Encryption Done--------");
  }
    
    public void decrypt() throws Exception {
    	 RandomAccessFile inputBytes = new RandomAccessFile(this.FILE_PLAIN, "r");
    	 byte[][] ListBlockData = new byte[(int) inputBytes.length()/16 + 1][16];
    	 ListBlockData[(int) inputBytes.length()/16] = new byte[(int) inputBytes.length() % 16];
         for (int a = 0; a < ListBlockData.length; a++) {
             inputBytes.read(ListBlockData[a]);
         }
 	     BufferedReader br = new BufferedReader (new FileReader(this.FILE_KEY));
 	     String key = br.readLine();
   	     String key1 = key.substring(0, key.length()/2);
   	 	 String key2 = key.substring(key.length()/2, key.length());
   	 	 br.close();
   	 	 
 	     /**
 	      * Pisah menjadi block
 	      */
   	 	 int jmlBlockPenuh = (int) (inputBytes.length() / AES.BLOCK_SIZE);
 	     byte[][] bufferOut = new byte[(int) (jmlBlockPenuh + 1)][AES.BLOCK_SIZE];
 	     bufferOut[jmlBlockPenuh] = new byte[(int) (inputBytes.length() % AES.BLOCK_SIZE)];
 	     byte[] key1Byte = Util.hex2byte(key1);
 	     byte[] key2Byte = Util.hex2byte(key2);
 	     
 	     
 	     
 	     for (int ii = 0; ii < jmlBlockPenuh - 1; ii++) {
// 	    	 System.out.println("Processing Block " + ii + " ...");
 	    	 bufferOut[ii] = blockDecrypt(ListBlockData[ii], key1Byte, key2Byte, nonce, ii);
 	     }
 	     
 	     
 	     /**
 	      * Handle check block terakhir
 	      * kalo terakhir full berarti jalanin lagi 2 kali encryptnya
 	      * kalo nggak, ciphertext stealing jalaninnya.
 	      */
 	     
 	     if(inputBytes.length() % AES.BLOCK_SIZE == 0) {
// 	    	 System.out.println("Processing Block " + (jmlBlockPenuh - 1) + " ...");
 	    	 bufferOut[jmlBlockPenuh - 1] = blockDecrypt(ListBlockData[jmlBlockPenuh - 1], key1Byte, key2Byte, nonce, jmlBlockPenuh - 1);
 	    	 
 	     }else {
	    	 /**
	    	  * Ciphertext stealing
	    	  */
	    	 int jumlahSisa = (int) inputBytes.length() % AES.BLOCK_SIZE;
//	    	 System.out.println("Processing Block " + (jmlBlockPenuh) + " ...");
	    	 byte[] xx = blockDecrypt(ListBlockData[jmlBlockPenuh - 1], key1Byte, key2Byte, nonce, jmlBlockPenuh);
	    	 System.arraycopy(xx, 0, bufferOut[jmlBlockPenuh], 0, jumlahSisa);
	    	 byte[] cp = new byte[AES.BLOCK_SIZE - jumlahSisa];
	    	 byte[] yy = new byte[AES.BLOCK_SIZE];
	    	 for (int ii = jumlahSisa; ii < AES.BLOCK_SIZE; ii++) {
	    		 cp[ii - jumlahSisa] = xx[ii];
	    	 }
	    	 System.arraycopy(ListBlockData[jmlBlockPenuh], 0, yy, 0, ListBlockData[jmlBlockPenuh].length);
	    	 System.arraycopy(cp, 0, yy, ListBlockData[jmlBlockPenuh].length, cp.length);
	    	 System.out.println("Processing Block " + (jmlBlockPenuh - 1) + " ...");
	    	 bufferOut[jmlBlockPenuh - 1] = blockDecrypt(yy, key1Byte, key2Byte, nonce, jmlBlockPenuh-1);
 	    	 
 	     }
 	     
 	     
 	     /**
 	      * Output
 	      */
         RandomAccessFile out = new RandomAccessFile(this.FILE_CIPHER, "rw");
 	     for (int a = 0; a < bufferOut.length; a++) {
             out.write(bufferOut[a]);
         }
   	 	 inputBytes.close();
 	     out.close();
 	     System.out.println("----------Decryption Done--------");
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
    	byte[] ret = enc;
    	if (j == 0) {
    		return ret;
    	}
    	
    	for (int ii = 0; ii < j; ii++) {
        	ret[0] = (byte) ((2 * (ret[0] % 128)) ^ (135 * (ret[15] / 128)));
    		for(int kk = 1; kk < 16; kk++) {
    			ret[kk] = (byte) ((2 * (ret[kk] % 128 )) ^ (ret[kk-1] / 128));
    		}
    	}
    	return ret;
    }
}