/*******************************************************************************
 * Copyright (c) 2011, 2016 Eurotech and/or its affiliates
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eurotech
 *******************************************************************************/
package org.eclipse.kura.cloud.app.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.eclipse.kura.KuraErrorCode;
import org.eclipse.kura.KuraException;

public class UnZip
{
    private static final String INPUT_ZIP_FILE = "/tmp/test.zip";
	//private static final String INPUT_ZIP_FILE = "/tmp/zip_with_script.zip";
	private static final String OUTPUT_FOLDER = "/tmp/";
	
	private static final int BUFFER = 1024;
	private static int tooBig = 0x6400000; // Max size of unzipped data, 100MB
	private static int tooMany = 1024;     // Max number of files

	public static void main( String[] args )
	{
		//UnZip unZip = new UnZip();
		//unZip.unZipIt(INPUT_ZIP_FILE,OUTPUT_FOLDER);
		
		// Create a ZipInputStream from the bag of bytes of the file
		byte[] zipBytes = null;
		try {
			zipBytes = getFileBytes(new File(INPUT_ZIP_FILE));
			unZipBytes(zipBytes, OUTPUT_FOLDER);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			unZipFile(INPUT_ZIP_FILE, OUTPUT_FOLDER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static public void unZipBytes(byte[] bytes, String outputFolder) throws IOException {
		ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(bytes));
		unZipZipInputStream(zis, outputFolder);
	}
	
	static public void unZipFile(String filename, String outputFolder) throws IOException {
		//get the zip file content
		File file = new File(filename);
		ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
		unZipZipInputStream(zis, outputFolder);
	}
	
	static private void unZipZipInputStream(ZipInputStream zis, String outFolder) throws IOException {
		try{
			String outputFolder = outFolder;
			if (outputFolder == null) {
				outputFolder = System.getProperty("user.dir");
			}
			
			//create output directory is not exists
			File folder = new File(outputFolder);
			if(!folder.exists()){
				folder.mkdirs();
			}
	
			int entries = 0;
			long total = 0;
			ZipEntry ze = zis.getNextEntry();
	
			while(ze!=null){
				byte[] buffer = new byte[BUFFER];
	
				String expectedFilePath = new StringBuilder(folder.getPath()).append(File.separator).append(ze.getName()).toString();
				String fileName = validateFileName(expectedFilePath, folder.getPath());
				File newFile = new File(fileName);
	
				if (newFile.isDirectory()) {
					newFile.mkdirs();
					ze = zis.getNextEntry();
					continue;
				}
				
				if (newFile.getParent() != null) {
					File parent = new File(newFile.getParent());
					parent.mkdirs();
				}
	
				FileOutputStream fos = new FileOutputStream(newFile);
	
				int len;
				while ((total + BUFFER <= tooBig) && (len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
					total += len;
				}
				fos.flush();
				fos.close();

				entries++;
				if (entries > tooMany) {
					throw new IllegalStateException("Too many files to unzip.");
				}
				if (total > tooBig) {
					throw new IllegalStateException("File being unzipped is too big.");
				}

				ze = zis.getNextEntry();
			}
	
			zis.closeEntry();
		} catch (IOException e) {
			throw e;
		} catch (KuraException e) {
			throw new IOException("File is outside extraction target directory.");
		} finally {
			if (zis != null) {
				zis.close();
			}
		}
	}
	
	private static String validateFileName(String zipFileName, String intendedDir) throws IOException, KuraException{
		File zipFile = new File(zipFileName);
		String filePath = zipFile.getCanonicalPath();

		File iD = new File(intendedDir);
		String canonicalID = iD.getCanonicalPath();

		if (filePath.startsWith(canonicalID)) {
			return filePath;
		} else {
			throw new KuraException(KuraErrorCode.SECURITY_EXCEPTION);
		}
	}

	private static byte[] getFileBytes(File file) throws IOException {
		ByteArrayOutputStream ous = null;
		InputStream ios = null;
		try {
			byte[] buffer = new byte[4096];
			ous = new ByteArrayOutputStream();
			ios = new FileInputStream(file);
			int read = 0;
			while ((read = ios.read(buffer)) != -1)
				ous.write(buffer, 0, read);
		} finally {
			try {
				if (ous != null)
					ous.close();
			} catch (IOException e) {
				// swallow, since not that important
			}
			try {
				if (ios != null)
					ios.close();
			} catch (IOException e) {
				// swallow, since not that important
			}
		}
		return ous.toByteArray();
	}
}
