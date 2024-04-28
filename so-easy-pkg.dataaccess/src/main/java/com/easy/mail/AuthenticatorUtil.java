package com.easy.mail;

import javax.mail.PasswordAuthentication;
/**
 * so-easy private
 * 
 * @author yewh 2015-07-7
 * 
 * @version 7.0.0
 * 
 */
public class AuthenticatorUtil
    extends javax.mail.Authenticator
{
  private String strUser;
  private String strPwd;

  public AuthenticatorUtil(String user, String password)
  {
    this.strUser = user;
    this.strPwd = password;
  }

  protected PasswordAuthentication getPasswordAuthentication()
  {
    return new PasswordAuthentication(strUser, strPwd);
  }
}
