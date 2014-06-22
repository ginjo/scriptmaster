SM_DecryptDES ( password ; textToDecode )

import javax.crypto.*;
import javax.crypto.spec.*;
import sun.misc.*;

byte[] salt = [4, 7, 29, 81, 5, 121, 117, 73]; //Any random set of 8 bytes will do.
// It is a good idea to change these to something else than the demo values for yourself.
// If you do change them, be sure to make the same change for both decryptiong/encrypting.


PBEKeySpec keySpec = new PBEKeySpec( password.toCharArray() );
SecretKey secretKey = SecretKeyFactory.getInstance( "PBEWithMD5AndDES" ).generateSecret( keySpec );
Cipher cipher = Cipher.getInstance( "PBEWithMD5AndDES" );
cipher.init( Cipher.DECRYPT_MODE, secretKey, new PBEParameterSpec( salt, 1 ) );
byte[] bytes = new BASE64Decoder().decodeBuffer( textToDecode );
return new String( cipher.doFinal( bytes ) );