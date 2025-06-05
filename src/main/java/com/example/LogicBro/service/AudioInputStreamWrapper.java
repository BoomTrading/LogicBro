package com.example.LogicBro.service;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import java.io.File;
import java.io.IOException;

public class AudioInputStreamWrapper extends AudioInputStream {
    
    private final AudioInputStream delegate;
    private final File temporaryFile;
    private final AudioConversionService conversionService;
    
    public AudioInputStreamWrapper(AudioInputStream delegate, File temporaryFile, AudioConversionService conversionService) {
        super(delegate, delegate.getFormat(), delegate.getFrameLength());
        this.delegate = delegate;
        this.temporaryFile = temporaryFile;
        this.conversionService = conversionService;
    }
    
    @Override
    public int read() throws IOException {
        return delegate.read();
    }
    
    @Override
    public int read(byte[] b) throws IOException {
        return delegate.read(b);
    }
    
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return delegate.read(b, off, len);
    }
    
    @Override
    public void close() throws IOException {
        try {
            delegate.close();
        } finally {
            // Clean up temporary converted file
            if (temporaryFile != null && conversionService != null) {
                conversionService.deleteTemporaryFile(temporaryFile);
            }
        }
    }
    
    @Override
    public AudioFormat getFormat() {
        return delegate.getFormat();
    }
    
    @Override
    public long getFrameLength() {
        return delegate.getFrameLength();
    }
}
