/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.ruyicai.charge.dna.ca;

/**
 *
 * @author Administrator
 */
public interface CFCA {
    void setCertPath(String certPath);
    String getCertPath();
    void setCertPass(String certPass);
    String getCertPass();
    String sign(String src) throws Exception;
    String unSign(String sign)throws Exception;
}
