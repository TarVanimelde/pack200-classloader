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
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;

/**
 *
 * @author TarVanimelde
 */
public class Pack200ClassLoader extends ClassLoader {

    Map<String, byte[]> classByteMap = new HashMap<>();

    public Pack200ClassLoader(Path pack200File, ClassLoader parent) throws NoSuchFileException, IOException {
        super(parent);
        if (!Files.isRegularFile(pack200File)) {
            throw new NoSuchFileException(pack200File.toString() + " is not a file.");
        }
        ByteArrayOutputStream jarByteStream = pack200ToJar(Files.newInputStream(pack200File));
        JarInputStream in = jarOutToInputStream(jarByteStream);
        parseJar(in);
    }
    
    public Pack200ClassLoader(InputStream pack200File, ClassLoader parent) throws NoSuchFileException, IOException {
        super(parent);
        ByteArrayOutputStream jarByteStream = pack200ToJar(pack200File);
        JarInputStream in = jarOutToInputStream(jarByteStream);
        parseJar(in);
    }
    
    private JarInputStream jarOutToInputStream(ByteArrayOutputStream jarByteStream) throws IOException {
        byte[] jarBytes = jarByteStream.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(jarBytes);
        return new JarInputStream(in);
    }
    
    private ByteArrayOutputStream pack200ToJar(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JarOutputStream jarOut = new JarOutputStream(out);
        Pack200.newUnpacker().unpack(in, jarOut);
        return out;
    }

    private void parseJar(JarInputStream jarIn) throws IOException {
        for (JarEntry entry = jarIn.getNextJarEntry();
                entry != null;
                entry = jarIn.getNextJarEntry()) {
            // Parse class
            if (entry.isDirectory()) {
                continue;
            }

            ByteArrayOutputStream classBytes = parseClass(jarIn);

            String entryName = entry.getName();
            if (!entryName.endsWith(".class")) {
                continue;
            }
            String className = entryName.substring(0, entryName.lastIndexOf('.'));
            className = className.replaceAll("/", ".");
            classByteMap.put(className, classBytes.toByteArray());
        }
    }

    private ByteArrayOutputStream parseClass(JarInputStream jarIn) throws IOException {
        ByteArrayOutputStream classOut = new ByteArrayOutputStream();
        int bytesRead = 0;
        byte[] buffer = new byte[2048];
        while ((bytesRead = jarIn.read(buffer)) > 0) {
            classOut.write(buffer, 0, bytesRead);
        }
        return classOut;
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = classByteMap.get(name);
        if (bytes == null) {
            throw new ClassNotFoundException(name);
        }

        return defineClass(name, bytes, 0, bytes.length);
    }
}
