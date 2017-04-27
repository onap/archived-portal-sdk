/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.analytics.util.jar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ExtractJar {

	public static int bufferSize = 8192;
	public static String JARFILE = "raptor_upgrade.jar";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length > 0 && args[0] != null && args[0].length() > 0)
			extractFilesFromJar(args[0]);
		else {
			System.out.println("Current Directory is taken as webapp path");
			String currentDir = new File(".").getAbsolutePath();
			extractFilesFromJar(currentDir);
		}
	}

	/**
	 * 
	 * @param jarFile
	 * @throws Exception
	 */
	public static void readJar(File jarFile) throws Exception {
		JarInputStream in = new JarInputStream(new FileInputStream(jarFile));
		JarEntry je;
		while ((je = in.getNextJarEntry()) != null) {
			if (je.isDirectory() == false) {
				if (je.getName().startsWith("org/openecomp/portalsdk/analytics/config/")) {
					System.out.println(je.getName() + " " + je.getTime());

				}
			}
		}
		in.close();
	}

	/**
	 * 
	 * @param directory
	 * @throws IOException
	 */
	public static void extractFilesFromJar(String directory) throws IOException {
		Class clazz = ExtractJar.class;
		URL jarUrl = clazz.getProtectionDomain().getCodeSource().getLocation();

		JarInputStream entryStream = new JarInputStream(jarUrl.openStream());
		JarEntry entry;

		while (true) {
			entry = entryStream.getNextJarEntry();
			if (entry == null)
				break;
			if (entry.getName().indexOf("jarutil") < 0) {
				System.out.println(entry.getName());
				File file = new File(directory, entry.getName());
				if (entry.isDirectory()) {
					if (!file.exists())
						file.mkdirs();
				} else {
					File dir = new File(file.getParent());
					if (!dir.exists())
						dir.mkdirs();
					if (file.exists())
						file.delete();
					FileOutputStream fout = new FileOutputStream(file);
					copy(entryStream, fout);
					fout.close();

					if (entry.getTime() >= 0)
						file.setLastModified(entry.getTime());
				}

			}
			entryStream.closeEntry();
		}
		entryStream.close();
		System.out.println("/WEB-INF/classes/org/openecomp/portalsdk/analytics");
		System.out.println("Delete .... ");

		File file1 = new File(directory, "/WEB-INF/classes/org/openecomp/portalsdk/analytics");

		deleteDir(file1);
		System.out.println("Deleted  ....");

		System.out.println("raptor_upgrade_setup.jar");
		file1 = new File(directory, "/raptor_upgrade_setup.jar");

		deleteDir(file1);
		System.out.println("Deleted  ....");

		System.out.println("/org");
		System.out.println("Delete .... ");

		file1 = new File(directory, "/org");
		System.out.println(" ********************************* ");
		deleteDir(file1);
		System.out.println("Deleted  ....");
		System.out.println("org");
		System.out.println("Delete .... ");

		System.out.println("************************************************");
		System.out.println("*                                              *");
		System.out.println("*                                              *");
		System.out.println("*          RAPTOR SETUP COMPLETE.              *");
		System.out.println("*                                              *");
		System.out.println("*         Thank you for upgrading.             *");
		System.out.println("*                                              *");
		System.out.println("************************************************");
	}

	/**
	 * 
	 * @param in
	 * @param out
	 * @param byteCount
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out, long byteCount) throws IOException {
		byte buffer[] = new byte[bufferSize];
		int len = bufferSize;
		if (byteCount >= 0) {
			while (byteCount > 0) {
				if (byteCount < bufferSize)
					len = in.read(buffer, 0, (int) byteCount);
				else
					len = in.read(buffer, 0, bufferSize);
				if (len == -1)
					break;

				byteCount -= len;
				out.write(buffer, 0, len);
			}
		} else {
			while (true) {
				len = in.read(buffer, 0, bufferSize);
				if (len < 0)
					break;
				out.write(buffer, 0, len);
			}
		}
	}

	/**
	 * Copy Reader to Writer for byteCount bytes or until EOF or exception.
	 * 
	 * @param in
	 * @param out
	 * @param byteCount
	 * @throws IOException
	 */
	public static void copy(Reader in, Writer out, long byteCount) throws IOException {
		char buffer[] = new char[bufferSize];
		int len = bufferSize;
		if (byteCount >= 0) {
			while (byteCount > 0) {
				if (byteCount < bufferSize)
					len = in.read(buffer, 0, (int) byteCount);
				else
					len = in.read(buffer, 0, bufferSize);

				if (len == -1)
					break;
				byteCount -= len;
				out.write(buffer, 0, len);
			}
		} else {
			while (true) {
				len = in.read(buffer, 0, bufferSize);
				if (len == -1)
					break;
				out.write(buffer, 0, len);
			}
		}
	}

	/**
	 * Copy Stream in to Stream out until EOF or exception.
	 * 
	 * @param in
	 * @param out
	 * @throws IOException
	 */
	public static void copy(InputStream in, OutputStream out) throws IOException {
		copy(in, out, -1);
	}

	public static boolean deleteDir(File dir) {
		System.out.println("Name: " + dir.getName() + " " + dir.isDirectory());
		if (dir.isDirectory()) {
			String[] children = dir.list();
			System.out.println(children);
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

}
