/*******************************************************************************
 * Copyright (c) 2016 Francisco Gonzalez-Armijo Riádigos
 *
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
 ******************************************************************************/

package com.kuassivi.annotation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Francisco Gonzalez-Armijo
 */
public class FileManager {

    public FileManager() {}

    /**
     * Writes a file to Disk. This is an I/O operation and this method executes in the main thread,
     * so it is recommended to perform this operation using another thread.
     *
     * @param file The file to write to Disk.
     */
    public void writeToFile(File file, String fileContent) {
        if (!file.exists()) {
            //noinspection TryWithIdenticalCatches,EmptyFinallyBlock
            try {
                FileWriter writer = new FileWriter(file);
                if (fileContent != null) {
                    writer.write(fileContent);
                }
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }

    /**
     * Reads a content from a file. This is an I/O operation and this method executes in the main
     * thread, so it is recommended to perform the operation using another thread.
     *
     * @param file The file to read from.
     * @return A string with the content of the file.
     */
    public String readFileContent(File file) {
        StringBuilder fileContentBuilder = new StringBuilder();
        if (file.exists()) {
            String stringLine;
            //noinspection TryWithIdenticalCatches
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                boolean newLine = false;
                while ((stringLine = bufferedReader.readLine()) != null) {
                    if (newLine) {
                        fileContentBuilder.append("\n");
                    }
                    fileContentBuilder.append(stringLine);
                    newLine = true;
                }
                bufferedReader.close();
                fileReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return fileContentBuilder.toString();
    }

    /**
     * Returns the last modified time of a {@link File}
     *
     * @param file The file to check last modified time.
     * @return long value for the time
     */
    public long getLastModifiedTime(File file) {
        return file.lastModified();
    }

    /**
     * Updates the last modified time of a {@link File}
     *
     * @param file The file to update its last modified time.
     * @param time The time to update in the file.
     * @return <code>true</code> if and only if the operation succeeded; <code>false</code>
     * otherwise
     */
    public boolean setLastModifiedTime(File file, long time) {
        return file.setLastModified(time);
    }

    /**
     * Returns a boolean indicating whether this file can be found on the underlying file system.
     *
     * @param file The file to check existence.
     * @return true if this file exists, false otherwise.
     */
    public boolean exists(File file) {
        return file.exists();
    }

    /**
     * Warning: Deletes the content of a directory. This is an I/O operation and this method
     * executes in the main thread, so it is recommended to perform the operation using another
     * thread.
     *
     * @param directory The directory which its content will be deleted.
     */
    public void clearDirectory(File directory) {
        if (directory.exists()) {
            //noinspection ConstantConditions
            for (File file : directory.listFiles()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }
}