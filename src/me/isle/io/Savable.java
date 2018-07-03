package me.isle.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public interface Savable<T> {
	int writeSize();
	byte[] compress();
	T load(FileInputStream fis) throws IOException;
	
	default void save(FileOutputStream fos) throws IOException{
		fos.write(compress());
	}
}
