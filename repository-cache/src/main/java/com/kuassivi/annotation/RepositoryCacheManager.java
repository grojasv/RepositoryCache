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

import java.io.File;

/**
 * @author Francisco Gonzalez-Armijo
 */
public final class RepositoryCacheManager {

    private static final String DEFAULT_FILE_NAME = "r_p_c_"; // repository_proxy_cache_

    private static RepositoryCacheManager instance;

    private FileManager fileManager;

    private RepositoryCacheManager() {
        fileManager = new FileManager();
    }

    public static RepositoryCacheManager getInstance() {
        return instance == null
               ? instance = new RepositoryCacheManager()
               : instance;
    }

    /**
     * Returns a hash code based on the contents of the given array. If the array contains other
     * arrays as its elements, the hash code is based on their identities not their contents. So it
     * is acceptable to invoke this method on an array that contains itself as an element, either
     * directly or indirectly.
     * <p>
     * For any two arrays {@code a} and {@code b}, if {@code Arrays.equals(a, b)} returns {@code
     * true}, it means that the return value of {@code Arrays.hashCode(a)} equals {@code
     * Arrays.hashCode(b)}.
     * <p>
     * The value returned by this method is the same value as the method
     * Arrays.asList(array).hashCode(). If the array is {@code null}, the return value is 0.
     *
     * @param objects the array whose hash code to compute.
     * @return the hash code for {@code array}.
     */
    public static int hashCode(Object... objects) {
        if (objects == null) {
            return 0;
        }
        int hashCode = 1;
        for (Object element : objects) {
            int elementHashCode;

            if (element == null) {
                elementHashCode = 0;
            } else {
                elementHashCode = (element).hashCode();
            }
            hashCode = 31 * hashCode + elementHashCode;
        }
        return hashCode;
    }

    public void save(RepositoryProxyCache proxyCache) {
        File cacheFile = buildFile(proxyCache.getCacheDir(), proxyCache.getFileName());
        new Thread(new CacheWriter(
                fileManager, cacheFile, String.valueOf(proxyCache.getHashCode()))
        ).start();
    }

    public boolean isCached(RepositoryProxyCache proxyCache) {
        File cacheFile = buildFile(proxyCache.getCacheDir(), proxyCache.getFileName());
        return isCached(cacheFile, proxyCache.getHashCode());
    }

    public boolean isCached(File file, int hashCode) {
        return fileManager.exists(file) && containsHashCode(file, hashCode);
    }

    public boolean isExpired(RepositoryProxyCache proxyCache) {
        File cacheFile = buildFile(proxyCache.getCacheDir(), proxyCache.getFileName());
        if (isCached(cacheFile, proxyCache.getHashCode())) {
            long lastModifiedTime = fileManager.getLastModifiedTime(cacheFile);
            long methodCacheTime = proxyCache.getCacheTime();
            return System.currentTimeMillis() > (lastModifiedTime + methodCacheTime * 1000);
        }
        return true;
    }

    public boolean containsHashCode(File file, int hashCode) {
        String content = fileManager.readFileContent(file);
        return content.equals(String.valueOf(hashCode));
    }

    /**
     * Builds a file the cached file method into the disk cache.
     *
     * @param file     The Context File
     * @param fileName The string to store the cache method
     * @return A valid file.
     */
    private File buildFile(File file, String fileName) {
        //noinspection StringBufferReplaceableByString
        StringBuilder fileNameBuilder = new StringBuilder();
        fileNameBuilder.append(file.getPath());
        fileNameBuilder.append(File.separator);
        fileNameBuilder.append(DEFAULT_FILE_NAME);
        fileNameBuilder.append(fileName);

        return new File(fileNameBuilder.toString());
    }

    /**
     * {@link Runnable} class for writing to disk.
     */
    private static class CacheWriter implements Runnable {

        private final FileManager fileManager;
        private final File        fileToWrite;
        private final String      fileContent;

        CacheWriter(FileManager fileManager, File fileToWrite, String fileContent) {
            this.fileManager = fileManager;
            this.fileToWrite = fileToWrite;
            this.fileContent = fileContent;
        }

        @Override
        public void run() {
            this.fileManager.writeToFile(fileToWrite, fileContent);
        }
    }
}