package com.extraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class AppClass {

	/**
	 * Function Name : File unGzip(File inputFile,File outputDir) Description :
	 * Ungzip given input file into an particular output directory. Input
	 * Parameters : File - Input file with tar.gz extension File - Directory
	 * file. Exceptions : FileNotFoundException, IOException Return : File
	 * object - outputfie with .tar extension
	 */
	private static File unGzip(File inputFile, File outputDir) throws FileNotFoundException, IOException {

		File outputFile = new File(outputDir, inputFile.getName().substring(0, inputFile.getName().length() - 3));

		/* creation input and output streams */
		GZIPInputStream inputStream = new GZIPInputStream(new FileInputStream(inputFile));
		FileOutputStream outputStream = new FileOutputStream(outputFile);

		/* copy streams */
		IOUtils.copy(inputStream, outputStream);

		/* closing all streams */
		inputStream.close();
		outputStream.close();

		return outputFile;
	}

	/**
	 * Function Name : void unTar(File,File) Description : Untar given input
	 * file into an particular output directory. Input Parameters : File - Input
	 * file with tar extension File - Directory file. Exceptions :
	 * ArchiveException, IOException Return : None
	 */
	private static void unTar(File inputFile, File outputDir) throws ArchiveException, IOException {

		InputStream inputStream = new FileInputStream(inputFile);

		/* create TarArchiveInputStream */
		TarArchiveInputStream tarInputStream = (TarArchiveInputStream) new ArchiveStreamFactory()
				.createArchiveInputStream("tar", inputStream);
		TarArchiveEntry entry = null;

		while ((entry = (TarArchiveEntry) tarInputStream.getNextEntry()) != null) {
			File outputFile = new File(outputDir, entry.getName());

			if (entry.isDirectory()) {
				/* Creating output directory */
				if (!outputFile.exists()) {
					if (!outputFile.mkdirs()) {
						throw new IllegalStateException(
								String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
					}
				}
			} else {
				/* Creating Output file */
				OutputStream outputFileStream = new FileOutputStream(outputFile);
				IOUtils.copy(tarInputStream, outputFileStream);
				outputFileStream.close();
			}
		}
		/* Closing IOStreams */
		inputStream.close();
		tarInputStream.close();
	}

	/**
	 * Function Name : void main(String[] args) Description : Extracts tar.gz
	 * into particular directory. Input Parameters : String[] - Input file,
	 * Destination directory path Exceptions : FileNotFoundException,
	 * IOException, ArchiveException Return : None
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ArchiveException {

		File inputFile = new File(args[0]);
		File outputDir = new File(args[1]);

		/* Create destination directory if its not present */
		if (!(outputDir.exists()))
			if (!(outputDir.mkdir()))
				throw new IllegalStateException(
						String.format("Couldn't create directory %s.", outputDir.getAbsolutePath()));

		/* Extracting .tar.gz into .tar */
		File tempFile = unGzip(inputFile, outputDir);

		/* Extracting files from .tar file */
		unTar(tempFile, outputDir);

		/* Deleting Intermediate tar file */
		if (tempFile.exists())
			tempFile.delete();
	}
}