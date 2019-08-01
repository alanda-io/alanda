/**
 * 
 */
package io.alanda.base.service.impl;

import java.security.SecureRandom;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.inject.Named;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author jlo
 */
@Named("passwordService")
public class PasswordServiceImpl {

  //The higher the number of iterations the more
  // expensive computing the hash is for us and
  // also for an attacker.
  private static final int iterations = 20 * 1000;

  private static final int saltLen = 32;

  private static final int desiredKeyLen = 256;

  /**
   * Computes a salted PBKDF2 hash of given plaintext password suitable for storing in a database. Empty passwords are
   * not supported.
   */
  public String createPassword(String password) throws Exception {
    byte[] salt = SecureRandom.getInstance("SHA1PRNG").generateSeed(saltLen);
    // store the salt with the password
    return DatatypeConverter.printBase64Binary(salt) + "$" + hash(password, salt);
  }

  //using PBKDF2 from Sun, an alternative is https://github.com/wg/scrypt
  // cf. http://www.unlimitednovelty.com/2012/03/dont-use-bcrypt.html
  private String hash(String password, byte[] salt) throws Exception {
    if (password == null || password.length() == 0)
      throw new IllegalArgumentException("Empty passwords are not supported.");
    SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
    SecretKey key = f.generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterations, desiredKeyLen));
    return DatatypeConverter.printBase64Binary(key.getEncoded());

  }

  /**
   * Checks whether given plaintext password corresponds to a stored salted hash of the password.
   */
  public boolean checkPassword(String password, String stored) throws Exception {
    String[] saltAndPass = stored.split("\\$");
    if (saltAndPass.length != 2) {
      throw new IllegalStateException("The stored password have the form 'salt$hash'");
    }
    String hashOfInput = hash(password, DatatypeConverter.parseBase64Binary(saltAndPass[0]));
    return hashOfInput.equals(saltAndPass[1]);
  }

  public static void main(String[] args) throws Exception {
    PasswordServiceImpl pwd = new PasswordServiceImpl();
    int count = 1;
    for (int i = 0; i < count; i++ ) {
      String s = RandomStringUtils.randomAlphanumeric(10);
      System.out.println(s + "\t" + pwd.createPassword(s));
    }

  }

}
