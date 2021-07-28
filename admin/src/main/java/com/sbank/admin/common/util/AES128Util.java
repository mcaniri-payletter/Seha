package com.sbank.admin.common.util;

import com.sbank.admin.common.constants.GlobalConstants;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

/**--------------------------------------------------------------------
 * ■AES128 암복호화 유틸 ■payletter ■2018-09-28
 --------------------------------------------------------------------**/
public class AES128Util {
	private static final Logger logger = LoggerFactory.getLogger("UTIL");
	private Key keySpec;
	private byte[] bytIV;

    public AES128Util() {
    	try {
	    	String strAESKey = GlobalConstants.SYSTEM_AES_KEY;

	    	this.bytIV = strAESKey.substring(0, 16).getBytes();

	        byte[] bytKey    = new byte[16];
	        byte[] bytAESKey = strAESKey.getBytes(GlobalConstants.SYSTEM_ENCODING);

	        int intAESKeyLength = bytAESKey.length;

	        if (intAESKeyLength > bytKey.length) {
	        	intAESKeyLength = bytKey.length;
	        }

	        System.arraycopy(bytAESKey, 0, bytKey, 0, intAESKeyLength);
	        SecretKeySpec keySpec = new SecretKeySpec(bytKey, "AES");

	        this.keySpec = keySpec;
    	} catch(Exception ex) {
    		logger.error("{}", ex);
    	}
    }

    /**--------------------------------------------------------------------
     * ■암호화 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public String encrypt(String strPlainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
    	String strCipher = "";

    	Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(bytIV));

        byte[] bytCipher = cipher.doFinal(strPlainText.getBytes(GlobalConstants.SYSTEM_ENCODING));

        Hex.encodeHex(bytCipher);

        strCipher = new String(Hex.encodeHex(bytCipher));

        return strCipher;
    }

    /**--------------------------------------------------------------------
     * ■복호화 ■payletter ■2018-09-28
     --------------------------------------------------------------------**/
    public String decrypt(String strPlainText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, DecoderException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
    	String strPlain = "";

    	Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");

		cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(bytIV));

        byte[] bytPlain = Hex.decodeHex(strPlainText.toCharArray());

        strPlain = new String(cipher.doFinal(bytPlain), GlobalConstants.SYSTEM_ENCODING);

        return strPlain;
    }
}
