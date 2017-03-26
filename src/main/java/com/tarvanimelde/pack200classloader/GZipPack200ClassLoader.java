/*
 * Copyright 2017 TarVanimelde.
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
 */
package com.tarvanimelde.pack200classloader;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author TarVanimelde
 */
public class GZipPack200ClassLoader extends Pack200ClassLoader {
    GZipPack200ClassLoader(Path gzipFile, ClassLoader parent) throws IOException, NoSuchFileException {
        super(decompressGZIPStream(new GZIPInputStream(Files.newInputStream(gzipFile))), parent);
    }
    
    GZipPack200ClassLoader(GZIPInputStream gzipStream, ClassLoader parent) throws IOException, NoSuchFileException {
        super(decompressGZIPStream(gzipStream), parent);
    }
    
    private static InputStream decompressGZIPStream(GZIPInputStream gIn) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        for (len = gIn.read(buffer, 0, buffer.length); len != -1; len = gIn.read(buffer, 0, buffer.length)) {
            out.write(buffer, 0, len);
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return in;
    }
}
