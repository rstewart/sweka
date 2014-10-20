package com.shopwiki.classification.weka.sandbox;

import java.io.File;

import com.shopwiki.classification.weka.Dataset;

public class JsonDataset extends Dataset
{
    private static final long serialVersionUID = -299796516832039082L;
    
    public JsonDataset(String name, File positiveFile, File negativeFile)
    {
      //super(name, attributes);
        super(name, null);
    }
}
