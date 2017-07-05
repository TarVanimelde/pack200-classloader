# pack200 Classloader

Convenience [class loaders](https://docs.oracle.com/javase/8/docs/api/java/lang/ClassLoader.html) that load classes from pack200-compressed jars. The overhead of scanning the files contained in the jar (e.g., virus scanners) is avoided by performing the necessary decompressions in-memory. Owing to the [design of pack200](http://docs.oracle.com/javase/8/docs/technotes/guides/pack200/pack-spec.html), random file access is not possible, so all the files contained in a pack200 archive are decompressed simultaneously in the constructor of the classloader. The bytes of the decompressed class files are held in memory.

* License: Apache 2.0

## pack200

To load the class files from a pack200 archive, provide a parent ```ClassLoader``` (usually the system class loader) and either a ```Path``` to the archive or an ```InputStream```:

```Java
Path archive = Paths.get("file.pack");
ClassLoader parent = ClassLoader.getSystemClassLoader();
ClassLoader loader = new Pack200ClassLoader(archive, parent);
loader.loadClass("full.class.path");
```

## GZIP pack200

Similar to the above example, in addition to the parent ```ClassLoader```, you can provide either a ```Path``` or a ```GZIPInputStream``` for a GZIP-compressed pack200 archive:

```Java
Path archive = Paths.get("file.pack.gz");
ClassLoader parent = ClassLoader.getSystemClassLoader();
ClassLoader loader = new GZipPack200ClassLoader(archive, parent);
loader.loadClass("full.class.path");
```
<!--
## ZIP pack200

To load the classes contained within a ZIP pack200-compressed archive, provide a parent ```ClassLoader``` amd either a ```Path``` or a ```ZipInputStream```:

```Java
Path archive = Paths.get("file.zip");
ClassLoader parent = ClassLoader.getSystemClassLoader();
ClassLoader loader = new ZipPack200ClassLoader(archive, parent);
loader.loadClass("full.class.path");
```

The ZIP compression format permits multiple files to be stored in an archive. This introduces a potential problem where multiple pack200 archives might exist in a ZIP file, and further, with conflicting class paths. This is handled by loading the first class seen with a given classpath into memory, and discarding the others. The order in which pack200 files are read from the ZIP archive follows the order created by [```ZipInputStream```](https://docs.oracle.com/javase/8/docs/api/java/util/zip/ZipInputStream.html).
-->

## TODO List:

* Implement ZIP pack200
