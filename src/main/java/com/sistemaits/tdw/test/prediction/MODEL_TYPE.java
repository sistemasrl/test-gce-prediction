/**
 * MODEL_TYPE.java
 */
package com.sistemaits.tdw.test.prediction;


/**
 * @author simone.decristofaro
 * 31 ago 2015
 */
public enum MODEL_TYPE {
    NUMERIC,
    TEXT;
    
    
    public static MODEL_TYPE parse(String s){
        if(s.equalsIgnoreCase(MODEL_TYPE.TEXT.toString()))
            return TEXT;
        return MODEL_TYPE.NUMERIC;
    }
    
}
